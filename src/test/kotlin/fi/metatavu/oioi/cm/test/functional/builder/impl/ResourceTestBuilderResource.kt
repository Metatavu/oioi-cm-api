package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.client.apis.ResourcesApi
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.*
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import org.junit.Assert.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Test builder resource for resources
 *
 * @author Antti Leppä
 */
class ResourceTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Resource, ApiClient?>(testBuilder, apiClient) {

    private val customerResourceIds: MutableMap<UUID?, UUID?> = HashMap()
    private val deviceResourceIds: MutableMap<UUID?, UUID?> = HashMap()
    private val applicationResourceIds: MutableMap<UUID?, UUID?> = HashMap()

    override fun getApi(): ResourcesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ResourcesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new resource
     *
     * @param customer customer
     * @param device device
     * @param application application
     * @param orderNumber order
     * @param parentId parentId
     * @param data data
     * @param name name
     * @param slug slug
     * @param type slug
     * @return created resource
     * @throws ClientException
     */
    @JvmOverloads
    @Throws(ClientException::class)
    fun create(
        customer: Customer,
        device: Device,
        application: Application,
        orderNumber: Int? = null,
        parentId: UUID? = null,
        data: String? = null,
        name: String,
        slug: String,
        type: ResourceType,
        properties: Array<KeyValueProperty> = emptyArray(),
        styles: Array<KeyValueProperty> = emptyArray()
    ): Resource {
        val resource = Resource(
            type = type,
            name = name,
            slug = slug,
            parentId = parentId,
            orderNumber = orderNumber,
            properties = properties,
            styles = styles,
            data = data
        )

        val result: Resource = api.createResource(
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            resource = resource,
            copyResourceId = null,
            copyResourceParentId = null
        )

        customerResourceIds[result.id] = customer.id
        deviceResourceIds[result.id] = device.id
        applicationResourceIds[result.id] = application.id
        return addClosable(result)
    }

    /**
     * Creates a copy of a resource.
     *
     * This method does not register resources for auto cleaning
     */
    fun copyResource(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        copyResourceId: UUID,
        copyResourceParentId: UUID
    ): Resource {
        val result = api.createResource(
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            resource = null,
            copyResourceId = copyResourceId,
            copyResourceParentId = copyResourceParentId
        )

        customerResourceIds[result.id] = customerId
        deviceResourceIds[result.id] = deviceId
        applicationResourceIds[result.id] = applicationId
        return addClosable(result)
    }

    /**
     * Finds a resource
     *
     * @param customer customer
     * @param resourceId resource id
     * @return found resource
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun findResource(customer: Customer, device: Device, application: Application, resourceId: UUID): Resource {
        return api.findResource(customer.id!!, device.id!!, application.id!!, resourceId)
    }

    /**
     * Lists resources
     *
     * @param customer customer
     * @return found resources
     * @throws ClientException
     */
    fun listResources(customer: Customer, device: Device, application: Application, parent: Resource?): Array<Resource> {
        return listResources(
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            parentId = parent?.id
        )
    }

    /**
     * Lists resources
     *
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param parentId parent id
     * @return found resources
     */
    fun listResources(customerId: UUID, deviceId: UUID, applicationId: UUID, parentId: UUID?): Array<Resource> {
        return api.listResources(
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            parentId = parentId
        )
    }

    /**
     * Updates a resource into the API
     *
     * @param customer customer
     * @param body body payload
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun updateResource(customer: Customer, device: Device, application: Application, body: Resource): Resource {
        return api.updateResource(customer.id!!, device.id!!, application.id!!, body.id!!, body)
    }

    /**
     * Deletes a resource from the API
     *
     * @param customer customer
     * @param resource resource to be deleted
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun delete(customer: Customer, device: Device, application: Application, resource: Resource) {
        api.deleteResource(customer.id!!, device.id!!, application.id!!, resource.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Resource) {
                return@removeCloseable false
            }
            val (_, _, _, id) = closable
            id == resource.id
        }
    }

    /**
     * Asserts resource count within the system
     *
     * @param expected expected count
     * @param customer customer
     * @throws ClientException
     */
    @Suppress("unused")
    fun assertCount(expected: Int, customer: Customer, device: Device, application: Application, parent: Resource?) {
        assertEquals(expected.toLong(), listResources(customer, device, application, parent).size.toLong())
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param resourceId resource id
     */
    fun assertFindFailStatus(
        expectedStatus: Int,
        customer: Customer,
        device: Device,
        application: Application,
        resourceId: UUID
    ) {
        assertFindFailStatus(expectedStatus, customer.id!!, device.id!!, application.id!!, resourceId)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param resourceId resource id
     */
    fun assertFindFailStatus(
        expectedStatus: Int,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID
    ) {
        try {
            api.findResource(customerId, deviceId, applicationId, resourceId)
            fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param device device
     * @param application application
     * @param orderNumber order
     * @param parentId parentId
     * @param data data
     * @param name name
     * @param slug slug
     * @param type type
     * @param properties properties
     * @param styles styles
     */
    @Suppress("unused")
    fun assertCreateFailStatus(
        expectedStatus: Int,
        customer: Customer,
        device: Device,
        application: Application,
        orderNumber: Int?,
        parentId: UUID?,
        data: String?,
        name: String,
        slug: String,
        type: ResourceType,
        properties: Array<KeyValueProperty>,
        styles: Array<KeyValueProperty>
    ) {
        try {
            create(customer, device, application, orderNumber, parentId, data, name, slug, type, properties, styles)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param resource resource
     */
    @Suppress("unused")
    fun assertUpdateFailStatus(
        expectedStatus: Int,
        customer: Customer,
        device: Device,
        application: Application,
        resource: Resource
    ) {
        try {
            updateResource(customer, device, application, resource)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param resource resource
     */
    fun assertDeleteFailStatus(
        expectedStatus: Int,
        customer: Customer,
        device: Device,
        application: Application,
        resource: Resource
    ) {
        try {
            api.deleteResource(customer.id!!, device.id!!, application.id!!, resource.id!!)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param parentId parent id
     */
    @Suppress("unused")
    fun assertListFailStatus(
        expectedStatus: Int,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        parentId: UUID?
    ) {
        try {
            listResources(
                customerId = customerId,
                deviceId = deviceId,
                applicationId = applicationId,
                parentId = parentId
            )

            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts that actual resource equals expected resource when both are serialized into JSON
     *
     * @param expected expected resource
     * @param actual actual resource
     */
    fun assertResourcesEqual(expected: Resource?, actual: Resource?) {
        assertJsonsEqual(expected, actual)
    }

    override fun clean(t: Resource) {
        val customerId: UUID = customerResourceIds.remove(t.id)!!
        val deviceId: UUID = deviceResourceIds.remove(t.id)!!
        val applicationId: UUID = applicationResourceIds.remove(t.id)!!
        api.deleteResource(customerId, deviceId, applicationId, t.id!!)
    }
}