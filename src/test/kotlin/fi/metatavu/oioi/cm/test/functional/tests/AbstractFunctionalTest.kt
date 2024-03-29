package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.oioi.cm.client.models.*
import org.junit.After
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder

import org.junit.Assert.*
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.comparator.CustomComparator
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

/**
 * Abstract base class for functional tests
 *
 * @author Antti Leppä
 */
abstract class AbstractFunctionalTest {

    companion object {
        val CUSTOMER_1_ADMIN_ID = UUID.fromString("3bf6051a-2ebb-401b-93f5-9a4e5dcbfa3b")
        val CUSTOMER_1_USER_ID = UUID.fromString("235da6b3-b638-4d46-83fb-a19d59de6e1a")
        val CUSTOMER_2_ADMIN_ID = UUID.fromString("7106dd56-3548-4bea-a7f7-b0f89d69a382")
        val CUSTOMER_2_USER_ID = UUID.fromString("4d92fddc-2666-41e4-a883-1969c5c1f749")
    }

    @After
    fun assetCleanAfter() {
        TestBuilder().use { builder -> assertEquals(0, builder.admin.customers.listCustomers().size) }
    }

    /**
     * Creates key value object
     *
     * @param key key
     * @param value value
     * @return key value object
     */
    protected fun getKeyValue(key: String?, value: String?): KeyValueProperty {
        return KeyValueProperty(key!!, value!!)
    }

    /**
     * Asserts that objects are deeply equal. Comparison is done by serializing objects to JSON
     *
     * @param expected expected
     * @param actual actual
     */
    protected fun assertDeepEquals(expected: Any?, actual: Any?) {
        val compareResult: JSONCompareResult = this.jsonCompare(expected, actual)
        assertTrue(compareResult.message, compareResult.passed())
    }

    /**
     * Compares two JSON strings
     *
     * @param expected expected
     * @param actual actual
     * @return compare result
     */
    private fun jsonCompare(expected: Any?, actual: Any?): JSONCompareResult {
        val customComparator = CustomComparator(JSONCompareMode.LENIENT, *arrayOfNulls(0))
        return JSONCompare.compareJSON(this.toJSONString(expected), this.toJSONString(actual), customComparator)
    }

    /**
     * Serializes object to JSON string
     *
     * @param `object` object
     * @return JSON string
     */
    private fun toJSONString(`object`: Any?): String? {
        return if (`object` == null) null else ObjectMapper()
            .writeValueAsString(`object`)
    }

    /**
     * Creates a resource and children using resource item as source
     *
     * @param builder test builder
     * @param application application
     * @param customer customer
     * @param device device
     * @param item resource item
     * @param orderNumber order number
     * @param parentId parent id
     * @return created resource
     */
    protected fun createResourceFromItem(builder: TestBuilder, item: ResourceItem, customer: Customer, device: Device, application: Application, parentId: UUID, orderNumber: Int): Resource {
        val result = builder.admin.resources.create(
            customer = customer,
            device = device,
            application = application,
            orderNumber = orderNumber,
            parentId = parentId,
            data = item.data,
            name = item.name ?: item.slug,
            slug = item.slug,
            type = item.type,
            properties = item.properties,
            styles = item.styles
        )

        item.children.forEachIndexed { index, resourceItem ->
            createResourceFromItem(
                builder = builder,
                item = resourceItem,
                customer = customer,
                device = device,
                application = application,
                parentId = result.id!!,
                orderNumber = index
            )
        }

        return result
    }

    /**
     * Finds a resource by slugs from the API
     *
     * @param builder test builder
     * @param parentResource The parent resource
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param slugs slugs
     * @return Resource or null if not found
     */
    protected fun findResourceBySlugs(
        builder: TestBuilder,
        parentResource: Resource,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        slugs: List<String>
    ): Resource? {
        val resource = findResourceBySlug(
            builder = builder,
            parentResource = parentResource,
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            slug = slugs.first()
        ) ?: return null

        if (slugs.size == 1) {
            return resource
        }

        return findResourceBySlugs(
            builder = builder,
            parentResource = resource,
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            slugs = slugs.drop(1)
        )
    }

    /**
     * Finds a resource by slug from the API
     *
     * @param builder test builder
     * @param parentResource The parent resource
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param slug slug
     * @return Resource or null if not found
     */
    private fun findResourceBySlug(
        builder: TestBuilder,
        parentResource: Resource,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        slug: String
    ): Resource? {
        val resources = builder.admin.resources.listResources(
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            parentId = parentResource.id!!
        )

        return resources.find { resource -> resource.slug == slug }
    }

    /**
     * Data class for representing single resource item.
     *
     * Resource items can be used to make large amount of test resources more easily
     *
     * @param name name
     * @param slug slug
     * @param data data
     * @param children child resources
     * @param properties properties
     * @param styles styles
     * @param type type
     */
    protected data class ResourceItem(val slug: String, val type: ResourceType, val children: Array<ResourceItem> = emptyArray(), val data: String? = null, val name: String? = null, val properties: Array<KeyValueProperty> = emptyArray(), val styles: Array<KeyValueProperty> = emptyArray()) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResourceItem

            if (!children.contentEquals(other.children)) return false
            if (!properties.contentEquals(other.properties)) return false
            if (!styles.contentEquals(other.styles)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = children.contentHashCode()
            result = 31 * result + properties.contentHashCode()
            result = 31 * result + styles.contentHashCode()
            return result
        }

    }

}