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
            val customer = builder.admin.customers.create()
            assertNotNull(
                builder.admin.devices.create(
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
            val customer = builder.admin.customers.create()
            val createdDevice = builder.admin.devices.create(customer)
            builder.admin.devices.assertFindFailStatus(404, customer, UUID.randomUUID())
            builder.admin.devices.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID())
            val foundDevice = builder.admin.devices.findDevice(customer, createdDevice.id)
            builder.admin.devices.assertFindFailStatus(404, UUID.randomUUID(), foundDevice.id)
            builder.admin.devices.assertDevicesEqual(createdDevice, foundDevice)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testListDevices() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdDevice = builder.admin.devices.create(customer)
            val foundDevices = builder.admin.devices.listDevices(customer)
            assertEquals(1, foundDevices.size.toLong())
            builder.admin.devices.assertDevicesEqual(createdDevice, foundDevices[0])
        }
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdDevice = builder.admin.devices.create(
                customer,
                "test customer",
                "api key",
                "http://www.example.com/image.png",
                arrayOf(getKeyValue("key-1", "value-1"), getKeyValue("key-2", "value-2"))
            )

            val updateDevice = builder.admin.devices.findDevice(customer, createdDevice.id).copy(
                name = "updated customer",
                apiKey = "api key",
                imageUrl = "http://www.example.com/updated.png",
                metas = arrayOf(getKeyValue("key-1", "value-1"), getKeyValue("key-3", "value-3"))
            )

            val updatedDevice = builder.admin.devices.updateDevice(customer, updateDevice)
            assertEquals(createdDevice.id, updatedDevice.id)
            assertEquals(updateDevice.name, updatedDevice.name)
            assertEquals(updateDevice.apiKey, updatedDevice.apiKey)
            assertEquals(updateDevice.imageUrl, updatedDevice.imageUrl)
            builder.admin.devices.assertJsonsEqual(updateDevice.metas, updatedDevice.metas)

            val foundDevice = builder.admin.devices.findDevice(
                customer,
                createdDevice.id
            )

            assertEquals(createdDevice.id, foundDevice.id)
            assertEquals(updateDevice.name, foundDevice.name)
            assertEquals(updateDevice.apiKey, foundDevice.apiKey)
            assertEquals(updateDevice.imageUrl, foundDevice.imageUrl)

            builder.admin.devices.assertJsonsEqual(updateDevice.metas, foundDevice.metas)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteDevice() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdDevice = builder.admin.devices.create(customer)
            val device = builder.admin.devices.findDevice(customer, createdDevice.id)
            assertEquals(createdDevice.id, device.id)
            builder.admin.devices.delete(customer, createdDevice)
            builder.admin.devices.assertDeleteFailStatus(404, customer, createdDevice)
        }
    }
}