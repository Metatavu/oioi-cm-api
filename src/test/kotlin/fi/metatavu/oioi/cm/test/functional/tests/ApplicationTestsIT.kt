package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import kotlin.Throws
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*

/**
 * Application functional tests
 *
 * @author Heikki Kurhinen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class ApplicationTestsIT : AbstractFunctionalTest() {

    @Test
    @Throws(Exception::class)
    fun testApplication() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            assertNotNull(builder.admin().applications.create(customer, device, "test application"))
            val anotherCustomer = builder.admin().customers.create()
            builder.admin().applications.assertCreateFailStatus(400, anotherCustomer, device, "fail application")
        }
    }

    @Test
    fun testFindApplication() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val createdApplication = builder.admin().applications.create(customer, device)
            builder.admin().applications.assertFindFailStatus(404, customer, device, UUID.randomUUID())
            builder.admin().applications.assertFindFailStatus(
                404,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
            )
            val foundApplication =
                builder.admin().applications.findApplication(customer, device, createdApplication.id!!)
            builder.admin().applications.assertFindFailStatus(
                404,
                UUID.randomUUID(),
                device.id!!,
                foundApplication.id!!
            )
            builder.admin().applications.assertFindFailStatus(
                404,
                customer.id!!,
                UUID.randomUUID(),
                foundApplication.id
            )
            builder.admin().applications.assertApplicationsEqual(createdApplication, foundApplication)
            val anotherCustomer = builder.admin().customers.create()
            builder.admin().applications.assertFindFailStatus(
                400,
                anotherCustomer.id!!,
                device.id,
                foundApplication.id
            )
            val anotherDevice = builder.admin().devices.create(anotherCustomer)
            builder.admin().applications.assertFindFailStatus(
                400,
                anotherCustomer.id,
                anotherDevice.id!!,
                foundApplication.id
            )
        }
    }

    @Test
    fun testListApplications() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val createdApplication = builder.admin().applications.create(customer, device)
            val foundApplications = builder.admin().applications.listApplications(customer, device)
            assertEquals(1, foundApplications.size.toLong())
            builder.admin().applications.assertApplicationsEqual(createdApplication, foundApplications[0])
            val anotherCustomer = builder.admin().customers.create()
            builder.admin().applications.assertListFailStatus(400, anotherCustomer, device)
        }
    }

    @Test
    fun testUpdateApplication() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val createdApplication = builder.admin().applications.create(customer, device, "test application")
            val updateApplication = builder.admin().applications.findApplication(
                customer,
                device,
                createdApplication.id!!
            ).copy(name = "updated application")

            val (name, id) = builder.admin().applications.updateApplication(customer, device, updateApplication)
            assertEquals(createdApplication.id, id)
            assertNotEquals(createdApplication.name, name)
            val foundApplication =
                builder.admin().applications.findApplication(customer, device, createdApplication.id)
            assertEquals(createdApplication.id, foundApplication.id)
            assertEquals(updateApplication.name, foundApplication.name)
            val randomCustomerId = UUID.randomUUID()
            val randomDeviceId = UUID.randomUUID()
            builder.admin().applications.assertUpdateFailStatus(404, randomCustomerId, randomDeviceId, foundApplication)
            builder.admin().applications.assertUpdateFailStatus(404, randomCustomerId, device.id!!, foundApplication)
            builder.admin().applications.assertUpdateFailStatus(404, customer.id!!, randomDeviceId, foundApplication)
            val anotherCustomer = builder.admin().customers.create()
            val anotherDevice = builder.admin().devices.create(anotherCustomer)
            builder.admin().applications.assertUpdateFailStatus(400, anotherCustomer, device, foundApplication)
            builder.admin().applications.assertUpdateFailStatus(400, anotherCustomer, anotherDevice, foundApplication)
        }
    }

    @Test
    fun testDeleteApplication() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val createdApplication = builder.admin().applications.create(customer, device)
            val foundApplication = builder.admin().applications.findApplication(customer, device, createdApplication.id!!)
            assertEquals(createdApplication.id, foundApplication.id)
            val randomCustomerId = UUID.randomUUID()
            val randomDeviceId = UUID.randomUUID()
            val randomApplicationId = UUID.randomUUID()
            builder.admin().applications.assertDeleteFailStatus(
                404,
                randomCustomerId,
                randomDeviceId,
                randomApplicationId
            )
            builder.admin().applications.assertDeleteFailStatus(
                404,
                randomCustomerId,
                device.id!!,
                foundApplication.id!!
            )
            builder.admin().applications.assertDeleteFailStatus(404, customer.id!!, randomDeviceId, foundApplication.id)
            val anotherCustomer = builder.admin().customers.create()
            val anotherDevice = builder.admin().devices.create(anotherCustomer)
            builder.admin().applications.assertDeleteFailStatus(400, anotherCustomer, device, foundApplication)
            builder.admin().applications.assertDeleteFailStatus(400, anotherCustomer, anotherDevice, foundApplication)
            builder.admin().applications.delete(customer, device, createdApplication)
            builder.admin().applications.assertDeleteFailStatus(404, customer, device, createdApplication)
        }
    }
}