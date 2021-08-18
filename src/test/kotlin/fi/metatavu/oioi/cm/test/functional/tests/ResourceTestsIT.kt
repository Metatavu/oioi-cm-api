package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.ResourceType
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Resource functional tests
 *
 * @author Antti Leppä
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class ResourceTestsIT : AbstractFunctionalTest() {

    @Test
    fun testCreate() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)!!
            val application = builder.admin().applications.create(customer, device)!!

            assertNotNull(
                builder.admin().resources.create(
                    customer,
                    device,
                    application,
                    0,
                    application.rootResourceId,
                    "data",
                    "name",
                    "slug",
                    ResourceType.mENU
                )
            )
        }
    }

    @Test
    fun testFindResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)!!
            val application = builder.admin().applications.create(customer, device)!!
            val createdResource = builder.admin().resources.create(
                customer,
                device,
                application,
                1,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )!!
            builder.admin().resources.assertFindFailStatus(404, customer, device, application, UUID.randomUUID())
            builder.admin().resources.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            val foundResource = builder.admin().resources.findResource(customer, device, application, createdResource.id!!)
            builder.admin().resources.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), foundResource.id!!)
            builder.admin().resources.assertResourcesEqual(createdResource, foundResource)
        }
    }

    @Test
    fun testListResources() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)!!
            val application = builder.admin().applications.create(customer, device)!!
            val createdResource1 = builder.admin().resources.create(
                customer,
                device,
                application,
                3,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )!!
            val createdResource2 = builder.admin().resources.create(
                customer,
                device,
                application,
                1,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )
            val createdResource3 = builder.admin().resources.create(
                customer,
                device,
                application,
                2,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )
            val rootResource =
                builder.admin().resources.findResource(customer, device, application, application.rootResourceId!!)
            val foundResources = builder.admin().resources.listResources(customer, device, application, rootResource)
            assertEquals(3, foundResources.size.toLong())
            builder.admin().resources.assertResourcesEqual(createdResource2, foundResources[0])
            builder.admin().resources.assertResourcesEqual(createdResource3, foundResources[1])
            builder.admin().resources.assertResourcesEqual(createdResource1, foundResources[2])
        }
    }

    @Test
    fun testUpdateResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)!!
            val application = builder.admin().applications.create(customer, device)!!
            val resource = builder.admin().resources.create(
                customer,
                device, application, 0, application.rootResourceId, "data", "name", "slug", ResourceType.mENU,
                arrayOf(getKeyValue("prop-1", "value"), getKeyValue("prop-2", "value-2")),
                arrayOf(getKeyValue("style-1", "value"), getKeyValue("style-2", "value-2"))
            )!!

            val updateResource = builder.admin().resources.findResource(customer, device, application, resource.id!!)

            val updateProperties = arrayOf(getKeyValue("prop-1", "value-1"), getKeyValue("prop-3", "value-3"))
            val updateStyles = arrayOf(getKeyValue("style-1", "value-1"), getKeyValue("style-3", "value-3"))

            val updatedResource = builder.admin().resources
                .updateResource(customer, device, application, updateResource.copy(
                    data = "updated data",
                    name = "updated name",
                    styles = updateStyles,
                    properties = updateProperties
                ))

            builder.admin().resources.assertJsonsEqual(updateProperties, updatedResource.properties)
            builder.admin().resources.assertJsonsEqual(updateStyles, updatedResource.styles)
            assertEquals("updated data", updatedResource.data)
            assertEquals("updated name", updatedResource.name)
        }
    }

    @Test
    fun testDeleteResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)!!
            val application = builder.admin().applications.create(customer, device)!!
            val createdResource = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )!!

            val foundResource = builder.admin().resources.findResource(customer, device, application, createdResource.id!!)

            assertEquals(createdResource.id, foundResource.id)
            builder.admin().resources.delete(customer, device, application, createdResource)
            builder.admin().resources.assertDeleteFailStatus(404, customer, device, application, createdResource)
        }
    }
}