package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import kotlin.Throws
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.*

/**
 * Customer functional tests
 *
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class DeviceTestsIT : AbstractFunctionalTest() {
    @Test
    @Throws(Exception::class)
    fun testDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            assertNotNull(
                builder.admin().devices.create(
                    customer,
                    "test customer",
                    "api key",
                    "http://www.example.com/image.png",
                    arrayOf(getKeyValue("key-1", "value-1"))
                )
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val createdDevice = builder.admin().devices.create(customer)
            builder.admin().devices.assertFindFailStatus(404, customer, UUID.randomUUID())
            builder.admin().devices.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID())
            val foundDevice = builder.admin().devices.findDevice(customer, createdDevice!!.id)
            builder.admin().devices.assertFindFailStatus(404, UUID.randomUUID(), foundDevice.id)
            builder.admin().devices.assertDevicesEqual(createdDevice, foundDevice)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testListDevices() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val createdDevice = builder.admin().devices.create(customer)
            val foundDevices = builder.admin().devices.listDevices(customer)
            assertEquals(1, foundDevices.size.toLong())
            builder.admin().devices.assertDevicesEqual(createdDevice, foundDevices[0])
        }
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val createdDevice = builder.admin().devices.create(
                customer,
                "test customer",
                "api key",
                "http://www.example.com/image.png",
                arrayOf(getKeyValue("key-1", "value-1"), getKeyValue("key-2", "value-2"))
            )

            val updateDevice = builder.admin().devices.findDevice(customer, createdDevice!!.id).copy(
                name = "updated customer",
                apiKey = "api key",
                imageUrl = "http://www.example.com/updated.png",
                metas = arrayOf(getKeyValue("key-1", "value-1"), getKeyValue("key-3", "value-3"))
            )

            val (name, apiKey, id, imageUrl, metas) = builder.admin().devices.updateDevice(customer, updateDevice)
            assertEquals(createdDevice.id, id)
            assertEquals(updateDevice.name, name)
            assertEquals(updateDevice.apiKey, apiKey)
            assertEquals(updateDevice.imageUrl, imageUrl)
            builder.admin().devices.assertJsonsEqual(updateDevice.metas, metas)
            val (name1, apiKey1, id1, imageUrl1, metas1) = builder.admin().devices.findDevice(
                customer,
                createdDevice.id
            )
            assertEquals(createdDevice.id, id1)
            assertEquals(updateDevice.name, name1)
            assertEquals(updateDevice.apiKey, apiKey1)
            assertEquals(updateDevice.imageUrl, imageUrl1)
            builder.admin().devices.assertJsonsEqual(updateDevice.metas, metas1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val createdDevice = builder.admin().devices.create(customer)
            val (_, _, id) = builder.admin().devices.findDevice(customer, createdDevice!!.id)
            assertEquals(createdDevice.id, id)
            builder.admin().devices.delete(customer, createdDevice)
            builder.admin().devices.assertDeleteFailStatus(404, customer, createdDevice)
        }
    }
}