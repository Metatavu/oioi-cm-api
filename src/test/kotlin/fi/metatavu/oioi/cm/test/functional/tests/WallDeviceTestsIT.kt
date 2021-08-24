package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Wall device functional tests
 *
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class WallDeviceTestsIT : AbstractFunctionalTest() {

    @Test
    fun testWallApplicationExport() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)

            builder.admin().applications.create(
                customer = customer,
                device = device,
                name = "application's name"
            )

            val wallDevice = builder.admin().wallDevice.getDeviceJson(deviceId = device.id!!)
            assertNotNull(wallDevice)
            assertEquals(1, wallDevice.applications.size)

            val wallDeviceApplication = wallDevice.applications[0]
            assertNotNull(wallDeviceApplication)
            assertEquals("application's name", wallDeviceApplication.name)
        }
    }

    @Test
    fun testWallDeviceExportApiKey() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer, apiKey = "example-api-key")

            builder.admin().wallDevice.assertGetDeviceJsonStatus(expectedStatus = 401, deviceId = device.id!!)
            builder.admin().wallDevice.assertGetDeviceJsonStatus(expectedStatus = 403, deviceId = device.id, apiKey = "incorrect-api-key")
            assertNotNull(builder.admin().wallDevice.getDeviceJson(deviceId = device.id, apiKey = "example-api-key"))

            builder.admin().devices.updateDevice(customer = customer, body = device.copy(apiKey = ""))
            assertNotNull(builder.admin().wallDevice.getDeviceJson(deviceId = device.id, apiKey = ""))
            assertNotNull(builder.admin().wallDevice.getDeviceJson(deviceId = device.id, apiKey = null))

            builder.admin().devices.updateDevice(customer = customer, body = device.copy(apiKey = null))
            assertNotNull(builder.admin().wallDevice.getDeviceJson(deviceId = device.id, apiKey = ""))
            assertNotNull(builder.admin().wallDevice.getDeviceJson(deviceId = device.id, apiKey = null))
        }
    }
}