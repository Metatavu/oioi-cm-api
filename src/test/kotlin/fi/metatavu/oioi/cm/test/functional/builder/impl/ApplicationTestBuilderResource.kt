package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.client.apis.ApplicationsApi
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.Application
import fi.metatavu.oioi.cm.client.models.Customer
import fi.metatavu.oioi.cm.client.models.Device
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import org.junit.Assert.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Test builder resource for applications
 *
 * @author Heikki Kurhinen
 */
class ApplicationTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Application, ApiClient?>(testBuilder, apiClient) {

    private val customerApplicationIds: MutableMap<UUID?, UUID?> = HashMap()
    private val deviceApplicationIds: MutableMap<UUID?, UUID?> = HashMap()

    override fun getApi(): ApplicationsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ApplicationsApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new application with default values
     *
     * @param customer customer
     * @param device device
     *
     * @return created application
     * @throws ClientException
     */
    @JvmOverloads
    @Throws(ClientException::class)
    fun create(customer: Customer, device: Device, name: String = "default name"): Application? {
        val application = Application(
            name = name
        )

        val result: Application = api.createApplication(customer.id!!, device.id!!, application)
        customerApplicationIds[result.id] = customer.id
        deviceApplicationIds[result.id] = device.id
        return addClosable(result)
    }

    /**
     * Finds a application
     *
     * @param customer customer
     * @param device device
     * @param applicationId application id
     * @return found application
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun findApplication(customer: Customer, device: Device, applicationId: UUID): Application {
        return api.findApplication(customer.id!!, device.id!!, applicationId)
    }

    /**
     * Lists applications
     *
     * @param customer customer
     * @param device device
     * @return found applications
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun listApplications(customer: Customer, device: Device): Array<Application> {
        return api.listApplications(customer.id!!, device.id!!)
    }

    /**
     * Updates a application into the API
     *
     * @param customer customer
     * @param device device
     * @param body body payload
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun updateApplication(customer: Customer, device: Device, body: Application): Application {
        return api.updateApplication(customer.id!!, device.id!!, body.id!!, body)
    }

    /**
     * Deletes a application from the API
     *
     * @param customer customer
     * @param device device
     * @param application application to be deleted
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun delete(customer: Customer, device: Device, application: Application) {
        api.deleteApplication(customer.id!!, device.id!!, application.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Application) {
                return@removeCloseable false
            }
            val (_, id) = closable
            id == application.id
        }
    }

    /**
     * Asserts application count within the system
     *
     * @param expected expected count
     * @param customer customer
     * @param device device
     * @throws ClientException
     */
    fun assertCount(expected: Int, customer: Customer, device: Device) {
        assertEquals(expected, api.listApplications(customer.id!!, device.id!!).size)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param device device
     * @param applicationId application id
     */
    fun assertFindFailStatus(expectedStatus: Int, customer: Customer, device: Device, applicationId: UUID) {
        assertFindFailStatus(expectedStatus, customer.id!!, device.id!!, applicationId)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     */
    fun assertFindFailStatus(expectedStatus: Int, customerId: UUID, deviceId: UUID, applicationId: UUID) {
        try {
            api.findApplication(customerId, deviceId, applicationId)
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
     * @param name name
     */
    fun assertCreateFailStatus(expectedStatus: Int, customer: Customer, device: Device, name: String) {
        try {
            create(customer, device, name)
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
     * @param device device
     * @param application application
     */
    fun assertUpdateFailStatus(expectedStatus: Int, customer: Customer, device: Device, application: Application) {
        try {
            updateApplication(customer, device, application)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param deviceId device id
     * @param application application
     */
    fun assertUpdateFailStatus(expectedStatus: Int, customerId: UUID, deviceId: UUID, application: Application) {
        try {
            api.updateApplication(customerId, deviceId, application.id!!, application)
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
     * @param device device
     * @param application application
     */
    fun assertDeleteFailStatus(expectedStatus: Int, customer: Customer, device: Device, application: Application) {
        try {
            api.deleteApplication(customer.id!!, device.id!!, application.id!!)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     */
    fun assertDeleteFailStatus(expectedStatus: Int, customerId: UUID, deviceId: UUID, applicationId: UUID) {
        try {
            api.deleteApplication(customerId, deviceId, applicationId)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param device device
     */
    fun assertListFailStatus(expectedStatus: Int, customer: Customer, device: Device) {
        try {
            listApplications(customer, device)
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts that actual application equals expected application when both are serialized into JSON
     *
     * @param expected expected application
     * @param actual actual application
     */
    fun assertApplicationsEqual(expected: Application?, actual: Application?) {
        assertJsonsEqual(expected, actual)
    }

    override fun clean(t: Application) {
        val customerId: UUID = customerApplicationIds.remove(t.id)!!
        val deviceId: UUID = deviceApplicationIds.remove(t.id)!!
        api.deleteApplication(customerId, deviceId, t.id!!)
    }
}