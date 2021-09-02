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

    @After
    fun assetCleanAfter() {
        TestBuilder().use { builder -> assertEquals(0, builder.admin().customers.listCustomers().size) }
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
        val result = builder.admin().resources.create(
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