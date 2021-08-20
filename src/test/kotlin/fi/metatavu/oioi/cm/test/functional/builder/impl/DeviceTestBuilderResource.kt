package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.apis.DevicesApi
import java.util.UUID
import java.util.HashMap
import kotlin.jvm.JvmOverloads
import kotlin.Throws
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.Customer
import fi.metatavu.oioi.cm.client.models.Device
import fi.metatavu.oioi.cm.client.models.KeyValueProperty
import java.io.IOException
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.fail

/**
 * Test builder resource for devices
 *
 * @author Antti Leppä
 */
class DeviceTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Device, ApiClient?>(testBuilder, apiClient) {

    private val customerDeviceIds: MutableMap<UUID?, UUID?> = HashMap()

    override fun getApi(): DevicesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return DevicesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new device with default values
     *
     * @param customer customer
     *
     * @return created device
     * @throws  ClientException
     */
    @JvmOverloads
    @Throws(ClientException::class)
    fun create(
        customer: Customer,
        name: String = "default name",
        apiKey: String? = null,
        imageUrl: String? = null,
        metas: Array<KeyValueProperty> = emptyArray<KeyValueProperty>()
    ): Device {
        val device = Device(
            name = name,
            apiKey = apiKey,
            imageUrl = imageUrl,
            metas = metas
        )

        val result = api.createDevice(customer.id!!, device)
        customerDeviceIds[result.id] = customer.id
        return addClosable(result)
    }

    /**
     * Finds a device
     *
     * @param customer customer
     * @param deviceId device id
     * @return found device
     * @throws  ClientException
     */
    @Throws(ClientException::class)
    fun findDevice(customer: Customer, deviceId: UUID?): Device {
        return api.findDevice(customer.id!!, deviceId!!)
    }

    /**
     * Lists devices
     *
     * @param customer customer
     * @return found devices
     * @throws  ClientException
     */
    @Throws(ClientException::class)
    fun listDevices(customer: Customer): Array<Device> {
        return api.listDevices(customer.id!!)
    }

    /**
     * Updates a device into the API
     *
     * @param customer customer
     * @param body body payload
     * @throws  ClientException
     */
    @Throws(ClientException::class)
    fun updateDevice(customer: Customer, body: Device): Device {
        return api.updateDevice(customer.id!!, body.id!!, body)
    }

    /**
     * Deletes a device from the API
     *
     * @param customer customer
     * @param device device to be deleted
     * @throws  ClientException
     */
    @Throws(ClientException::class)
    fun delete(customer: Customer, device: Device) {
        api.deleteDevice(customer.id!!, device.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Device) {
                return@removeCloseable false
            }

            device.id == closable.id
        }
    }

    /**
     * Asserts device count within the system
     *
     * @param expected expected count
     * @param customer customer
     * @throws  ClientException
     */
    @Throws(ClientException::class)
    fun assertCount(expected: Int, customer: Customer) {
        assertEquals(expected, api.listDevices(customer.id!!).size)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param deviceId device id
     */
    fun assertFindFailStatus(expectedStatus: Int, customer: Customer, deviceId: UUID?) {
        assertFindFailStatus(expectedStatus, customer.id, deviceId)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param deviceId device id
     */
    fun assertFindFailStatus(expectedStatus: Int, customerId: UUID?, deviceId: UUID?) {
        try {
            api.findDevice(customerId!!, deviceId!!)
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
     * @param name name
     * @param apiKey API key
     * @param imageUrl image URL
     * @param metas metas
     */
    fun assertCreateFailStatus(
        expectedStatus: Int,
        customer: Customer,
        name: String,
        apiKey: String,
        imageUrl: String?,
        metas: Array<KeyValueProperty>
    ) {
        try {
            create(customer, name, apiKey, imageUrl, metas)
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
     */
    fun assertUpdateFailStatus(expectedStatus: Int, customer: Customer, device: Device) {
        try {
            updateDevice(customer, device)
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
     */
    fun assertDeleteFailStatus(expectedStatus: Int, customer: Customer, device: Device) {
        try {
            api.deleteDevice(customer.id!!, device.id!!)
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
     */
    fun assertListFailStatus(expectedStatus: Int, customer: Customer) {
        try {
            listDevices(customer)
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    /**
     * Asserts that actual device equals expected device when both are serialized into JSON
     *
     * @param expected expected device
     * @param actual actual device
     * @throws JSONException thrown when JSON serialization error occurs
     * @throws IOException thrown when IO Exception occurs
     */
    @Throws(IOException::class, JSONException::class)
    fun assertDevicesEqual(expected: Device?, actual: Device?) {
        assertJsonsEqual(expected, actual)
    }

    override fun clean(t: Device) {
        val customerId = customerDeviceIds.remove(t.id)
        api.deleteDevice(customerId!!, t.id!!)
    }
}