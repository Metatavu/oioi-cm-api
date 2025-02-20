package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.oioi.cm.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.Customer

import kotlin.Throws
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.HiveMQTestResource
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.UUID

/**
 * Customer functional tests
 *
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(HiveMQTestResource::class)
)
class CustomerTestsIT : AbstractFunctionalTest() {
    @Test
    @Throws(Exception::class)
    fun testCreateCustomer() {
        TestBuilder().use { builder ->
            assertNotNull(
                builder.admin.customers.create(
                    "test customer",
                    "http://example.com/great-image.jpg"
                )
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindCustomer() {
        TestBuilder().use { builder ->
            val createdCustomer = builder.admin.customers.create("test customer", "http://example.com/great-image.jpg")
            builder.admin.customers.assertFindFailStatus(404, UUID.randomUUID())
            val foundCustomer = builder.admin.customers.findCustomer(createdCustomer.id)
            builder.admin.customers.assertCustomersEqual(createdCustomer, foundCustomer)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testListCustomers() {
        TestBuilder().use { builder ->
            val createdCustomer = builder.admin.customers.create("test customer", "http://example.com/great-image.jpg")
            val foundCustomers: Array<Customer> = builder.admin.customers.listCustomers()
            assertEquals(1, foundCustomers.size.toLong())
            builder.admin.customers.assertCustomersEqual(createdCustomer, foundCustomers[0])
        }
    }

    @Test
    @Throws(Exception::class)
    fun testListCustomerPermissions() {
        TestBuilder().use { builder ->
            builder.admin.customers.create("customer-1", "http://example.com/great-image.jpg")
            builder.admin.customers.assertCount(1)
            builder.customer1User.customers.assertCount(1)
            builder.customer1Admin.customers.assertCount(1)
            builder.customer2User.customers.assertCount(0)
            builder.customer2Admin.customers.assertCount(0)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateCustomer() {
        TestBuilder().use { builder ->
            val createdCustomer = builder.admin.customers.create("test customer", "http://example.com/great-image.jpg")

            builder.admin.customers.assertCustomersEqual(
                createdCustomer,
                builder.admin.customers.findCustomer(createdCustomer.id)
            )

            val updateCustomer = builder.admin.customers.findCustomer(createdCustomer.id).copy(
                name = "updated customer",
                imageUrl = "http://example.com/greater-image.jpg"
            )

            val (name, id, imageUrl) = builder.admin.customers.updateCustomer(updateCustomer)
            assertEquals(createdCustomer.id, id)
            assertEquals(updateCustomer.name, name)
            assertEquals(updateCustomer.imageUrl, imageUrl)
            val (name1, id1, imageUrl1) = builder.admin.customers.findCustomer(createdCustomer.id)
            assertEquals(createdCustomer.id, id1)
            assertEquals(updateCustomer.name, name1)
            assertEquals(updateCustomer.imageUrl, imageUrl1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteCustomer() {
        TestBuilder().use { builder ->
            val createdCustomer =
                builder.admin.customers.create("test customer", "http://example.com/great-image.jpg")
            val (_, id) = builder.admin.customers.findCustomer(createdCustomer.id)
            assertEquals(createdCustomer.id, id)
            builder.admin.customers.delete(createdCustomer)
            builder.admin.customers.assertDeleteFailStatus(404, createdCustomer)
        }
    }

    @Test
    fun testDeletePermissions() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create(name = "customer-1")

            builder.customer2User.customers.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer
            )

            builder.customer2Admin.customers.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer
            )

            builder.customer1User.customers.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer
            )

            builder.customer1Admin.customers.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer
            )

            builder.admin.customers.delete(
                customer = customer
            )
        }
    }
}