package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.client.apis.CustomersApi
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import kotlin.jvm.JvmOverloads
import kotlin.Throws
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.Customer
import java.util.UUID
import java.io.IOException
import org.json.JSONException
import org.junit.Assert.*

/**
 * Test builder resource for customers
 *
 * @author Antti Leppä
 */
class CustomerTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Customer, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): CustomersApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return CustomersApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new customer with default values
     *
     * @return created customer
     * @throws ClientException
     */
    @JvmOverloads
    @Throws(ClientException::class)
    fun create(name: String = "default name", imageUrl: String? = "http://default.example.com"): Customer {
        val customer = Customer(
            name = name,
            imageUrl = imageUrl
        )

        val result = api.createCustomer(customer)
        return addClosable(result)
    }

    /**
     * Finds a customer
     *
     * @param customerId customer id
     * @return found customer
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun findCustomer(customerId: UUID?): Customer {
        return api.findCustomer(customerId!!)
    }

    /**
     * Lists customers
     *
     * @return found customers
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun listCustomers(): Array<Customer> {
        return api.listCustomers()
    }

    /**
     * Updates a customer into the API
     *
     * @param body body payload
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun updateCustomer(body: Customer): Customer {
        return api.updateCustomer(body.id!!, body)
    }

    /**
     * Deletes a customer from the API
     *
     * @param customer customer to be deleted
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun delete(customer: Customer) {
        api.deleteCustomer(customer.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Customer) {
                return@removeCloseable false
            }
            val (_, id) = closable
            id == customer.id
        }
    }

    /**
     * Asserts customer count within the system
     *
     * @param expected expected count
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun assertCount(expected: Int) {
        assertEquals(expected, api.listCustomers().size)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     */
    fun assertFindFailStatus(expectedStatus: Int, customerId: UUID?) {
        try {
            api.findCustomer(customerId!!)
            fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param name name
     * @param imageUrl image URL
     */
    fun assertCreateFailStatus(expectedStatus: Int, name: String, imageUrl: String?) {
        try {
            create(name, imageUrl)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     */
    fun assertUpdateFailStatus(expectedStatus: Int, customer: Customer) {
        try {
            updateCustomer(customer)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     */
    fun assertDeleteFailStatus(expectedStatus: Int, customer: Customer) {
        try {
            api.deleteCustomer(customer.id!!)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertListFailStatus(expectedStatus: Int) {
        try {
            api.listCustomers()
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts that actual customer equals expected customer when both are serialized into JSON
     *
     * @param expected expected customer
     * @param actual actual customer
     * @throws JSONException thrown when JSON serialization error occurs
     * @throws IOException thrown when IO Exception occurs
     */
    @Throws(IOException::class, JSONException::class)
    fun assertCustomersEqual(expected: Customer?, actual: Customer?) {
        assertJsonsEqual(expected, actual)
    }

    @Throws(ClientException::class)
    override fun clean(t: Customer) {
        api.deleteCustomer(t.id!!)
    }

}