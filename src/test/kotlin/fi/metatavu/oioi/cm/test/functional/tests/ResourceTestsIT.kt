package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.*
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.HiveMQTestResource
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
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(HiveMQTestResource::class)
)
class ResourceTestsIT : AbstractFunctionalTest() {

    @Test
    fun testCreate() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)

            assertNotNull(
                builder.admin.resources.create(
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
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val createdResource = builder.admin.resources.create(
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

            builder.admin.resources.assertFindFailStatus(404, customer, device, application, UUID.randomUUID())
            builder.admin.resources.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            val foundResource = builder.admin.resources.findResource(customer, device, application, createdResource.id!!)
            builder.admin.resources.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), foundResource.id!!)
            builder.admin.resources.assertResourcesEqual(createdResource, foundResource)
        }
    }

    @Test
    fun testListResources() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val createdResource1 = builder.admin.resources.create(
                customer,
                device,
                application,
                3,
                application.rootResourceId,
                "data",
                "name",
                "slug",
                ResourceType.mENU
            )

            val createdResource2 = builder.admin.resources.create(
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
            val createdResource3 = builder.admin.resources.create(
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
                builder.admin.resources.findResource(customer, device, application, application.rootResourceId!!)
            val foundResources = builder.admin.resources.listResources(
                customerId = customer.id!!,
                deviceId = device.id!!,
                applicationId = application.id!!,
                parentId = rootResource.id!!,
                resourceType = null
            )
            assertEquals(4, foundResources.size.toLong())
            builder.admin.resources.assertResourcesEqual(createdResource2, foundResources[1])
            builder.admin.resources.assertResourcesEqual(createdResource3, foundResources[2])
            builder.admin.resources.assertResourcesEqual(createdResource1, foundResources[3])
        }
    }

    @Test
    fun testUpdateResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val resource = builder.admin.resources.create(
                customer,
                device, application, 0, application.rootResourceId, "data", "name", "slug", ResourceType.mENU,
                arrayOf(getKeyValue("prop-1", "value"), getKeyValue("prop-2", "value-2")),
                arrayOf(getKeyValue("style-1", "value"), getKeyValue("style-2", "value-2"))
            )

            val updateResource = builder.admin.resources.findResource(customer, device, application, resource.id!!)

            val updateProperties = arrayOf(getKeyValue("prop-1", "value-1"), getKeyValue("prop-3", "value-3"))
            val updateStyles = arrayOf(getKeyValue("style-1", "value-1"), getKeyValue("style-3", "value-3"))

            val updatedResource = builder.admin.resources
                .updateResource(customer, device, application, updateResource.copy(
                    data = "updated data",
                    name = "updated name",
                    styles = updateStyles,
                    properties = updateProperties
                ))

            builder.admin.resources.assertJsonsEqual(updateProperties, updatedResource.properties)
            builder.admin.resources.assertJsonsEqual(updateStyles, updatedResource.styles)
            assertEquals("updated data", updatedResource.data)
            assertEquals("updated name", updatedResource.name)
        }
    }

    @Test
    fun testDeleteResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val createdResource = builder.admin.resources.create(
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

            val foundResource = builder.admin.resources.findResource(customer, device, application, createdResource.id!!)

            assertEquals(createdResource.id, foundResource.id)
            builder.admin.resources.delete(customer, device, application, createdResource)
            builder.admin.resources.assertDeleteFailStatus(404, customer, device, application, createdResource)
        }
    }

    @Test
    fun testContentVersionCopy() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)

            val rootItem = ResourceItem(slug = "1", ResourceType.cONTENTVERSION, children = arrayOf(
                ResourceItem(slug = "l", ResourceType.lANGUAGEMENU, children = arrayOf(
                    ResourceItem(slug = "fi", ResourceType.lANGUAGE, properties = arrayOf(getKeyValue("description", "Finnish language page")), styles = arrayOf(getKeyValue("background", "#fff"), getKeyValue("color", "#00f")), children = arrayOf(
                        ResourceItem(slug = "intro", ResourceType.iNTRO, children = arrayOf(
                            ResourceItem(slug = "s", ResourceType.sLIDESHOW, children = arrayOf(
                                ResourceItem(slug = "page-1", ResourceType.pAGE, children = arrayOf(
                                    ResourceItem(slug = "pdf", ResourceType.pDF, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e")
                                )),
                                ResourceItem(slug = "page-2", ResourceType.pAGE, children = arrayOf(
                                    ResourceItem(slug = "img", ResourceType.iMAGE, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a"),
                                    ResourceItem(slug = "txt", ResourceType.tEXT, data = "Hello world")
                                ))
                           ))
                        )),
                        ResourceItem(slug = "menu", ResourceType.mENU, children = arrayOf(
                            ResourceItem(slug = "menu-page-1", ResourceType.pAGE, children = arrayOf(
                                ResourceItem(slug = "video", ResourceType.vIDEO, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b"),
                            )),
                            ResourceItem(slug = "menu-page-2", ResourceType.pAGE, children = arrayOf(
                                ResourceItem(slug = "video", ResourceType.vIDEO, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b"),
                                ResourceItem(slug = "text", ResourceType.tEXT, data = "Hello again"),
                            ))
                        ))
                    ))
                ))
            ))

            val version1 = createResourceFromItem(
                builder = builder,
                item = rootItem,
                customer = customer,
                device = device,
                application = application,
                parentId = application.rootResourceId!!,
                orderNumber = 0
            )

            val version2 = builder.admin.resources.copyResource(
                customerId = customer.id!!,
                deviceId = device.id!!,
                applicationId = application.id!!,
                copyResourceId = version1.id!!,
                copyResourceParentId = application.rootResourceId
            )

            assertNotNull(version2)
            assertEquals("2", version2.slug)
            assertEquals("2", version2.name)

            assertCopiedTree(
                builder = builder,
                customer = customer,
                device = device,
                application = application,
                source = version1,
                target = version2
            )
        }
    }

    @Test
    fun testUpdateLockedResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create(name = "customer-1")
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val resource = builder.admin.resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.rootResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                type = ResourceType.mENU,
                properties = arrayOf(getKeyValue("prop-1", "value"), getKeyValue("prop-2", "value-2")),
                styles = arrayOf(getKeyValue("style-1", "value"), getKeyValue("style-2", "value-2"))
            )

            builder.admin.resources.updateResourceLock(
                customer = customer,
                device = device,
                application = application,
                resource = resource
            )

            val updateResource = builder.admin.resources.findResource(
                customer = customer,
                device = device,
                application = application,
                resourceId = resource.id!!
            )

            val updateProperties = arrayOf(getKeyValue("prop-1", "value-1"), getKeyValue("prop-3", "value-3"))
            val updateStyles = arrayOf(getKeyValue("style-1", "value-1"), getKeyValue("style-3", "value-3"))

            val updatedResource = builder.admin.resources.updateResource(
                customer = customer,
                device = device,
                application = application,
                resource = updateResource.copy(
                    data = "updated data",
                    name = "updated name",
                    styles = updateStyles,
                    properties = updateProperties
                ))

            builder.admin.resources.assertJsonsEqual(updateProperties, updatedResource.properties)
            builder.admin.resources.assertJsonsEqual(updateStyles, updatedResource.styles)
            assertEquals("updated data", updatedResource.data)
            assertEquals("updated name", updatedResource.name)

            builder.customer1User.resources.assertUpdateFailStatus(
                expectedStatus = 409,
                customer = customer,
                device = device,
                application = application,
                resource = updateResource.copy(
                    data = "updated data",
                    name = "updated name",
                    styles = updateStyles,
                    properties = updateProperties
                )
            )
        }
    }

    @Test
    fun testDeleteLockedResource() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create(name = "customer-1")
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val rootItem = ResourceItem(slug = "1", ResourceType.cONTENTVERSION, children = arrayOf(
                ResourceItem(slug = "l", ResourceType.lANGUAGEMENU, children = arrayOf(
                    ResourceItem(slug = "fi", ResourceType.lANGUAGE, properties = arrayOf(getKeyValue("description", "Finnish language page")), styles = arrayOf(getKeyValue("background", "#fff"), getKeyValue("color", "#00f")), children = arrayOf(
                        ResourceItem(slug = "intro", ResourceType.iNTRO, children = arrayOf(
                            ResourceItem(slug = "s", ResourceType.sLIDESHOW, children = arrayOf(
                                ResourceItem(slug = "page-1", ResourceType.pAGE, children = arrayOf(
                                    ResourceItem(slug = "pdf", ResourceType.pDF, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e")
                                )),
                                ResourceItem(slug = "page-2", ResourceType.pAGE, children = arrayOf(
                                    ResourceItem(slug = "img", ResourceType.iMAGE, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a"),
                                    ResourceItem(slug = "txt", ResourceType.tEXT, data = "Hello world")
                                ))
                            ))
                        )),
                        ResourceItem(slug = "menu", ResourceType.mENU, children = arrayOf(
                            ResourceItem(slug = "menu-page-1", ResourceType.pAGE, children = arrayOf(
                                ResourceItem(slug = "video", ResourceType.vIDEO, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b"),
                            )),
                            ResourceItem(slug = "menu-page-2", ResourceType.pAGE, children = arrayOf(
                                ResourceItem(slug = "video", ResourceType.vIDEO, data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b"),
                                ResourceItem(slug = "text", ResourceType.tEXT, data = "Hello again"),
                            ))
                        ))
                    ))
                ))
            ))

            val version1 = createResourceFromItem(
                builder = builder,
                item = rootItem,
                customer = customer,
                device = device,
                application = application,
                parentId = application.rootResourceId!!,
                orderNumber = 0
            )

            val version1ChildResources = builder.admin.resources.listResources(
                customerId = customer.id!!,
                deviceId = device.id!!,
                applicationId = application.id!!,
                parentId = version1.id!!,
                resourceType = null
            )

            val firstChild = version1ChildResources[0]

            val secondList = builder.admin.resources.listResources(
                customerId = customer.id,
                deviceId = device.id,
                applicationId = application.id,
                parentId = firstChild.id!!,
                resourceType = null
            )

            val secondChild = secondList[0]

            val thirdList = builder.admin.resources.listResources(
                customerId = customer.id,
                deviceId = device.id,
                applicationId = application.id,
                parentId = secondChild.id!!,
                resourceType = null
            )

            val thirdChild = thirdList[0]

            val fourthList = builder.admin.resources.listResources(
                customerId = customer.id,
                deviceId = device.id,
                applicationId = application.id,
                parentId = thirdChild.id!!,
                resourceType = null
            )

            val fourthChild = fourthList[0]

            val fifthList = builder.admin.resources.listResources(
                customerId = customer.id,
                deviceId = device.id,
                applicationId = application.id,
                parentId = fourthChild.id!!,
                resourceType = null
            )

            val fifthChild = fifthList[0]

            builder.admin.resources.updateResourceLock(
                customer = customer,
                device = device,
                application = application,
                resource = fifthChild
            )

            builder.customer1Admin.resources.assertDeleteFailStatus(
                expectedStatus = 409,
                customer = customer,
                device = device,
                application = application,
                resource = fifthChild
            )

            builder.customer1Admin.resources.assertDeleteFailStatus(
                expectedStatus = 409,
                customer = customer,
                device = device,
                application = application,
                resource = version1
            )

        }
    }

    @Test
    fun testDeletePermissions() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create(name = "customer-1")
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val resource = createResourceFromItem(
                builder = builder,
                item = ResourceItem(slug = "1", ResourceType.cONTENTVERSION),
                customer = customer,
                device = device,
                application = application,
                parentId = application.rootResourceId!!,
                orderNumber = 0
            )

            builder.customer2User.resources.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer,
                device = device,
                application = application,
                resource = resource
            )

            builder.customer2Admin.resources.assertDeleteFailStatus(
                expectedStatus = 403,
                customer = customer,
                device = device,
                application = application,
                resource = resource
            )

            builder.customer1User.resources.delete(
                customer = customer,
                device = device,
                application = application,
                resource = resource
            )

            builder.customer1Admin.resources.assertDeleteFailStatus(
                expectedStatus = 404,
                customer = customer,
                device = device,
                application = application,
                resource = resource
            )
        }
    }

    @Test
    fun testWallApplicationJsonImport(): Unit = TestBuilder().use { builder ->
        val customer = builder.admin.customers.create()
        val device = builder.admin.devices.create(customer)

        val application = builder.admin.applications.create(
            customer = customer,
            device = device,
            name = "application's name"
        )

        val applicationId = application.id!!
        val originalContentVersionId = application.activeContentVersionResourceId!!

        val rootItem = ResourceItem(
            slug = "1", ResourceType.cONTENTVERSION, children = arrayOf(
                ResourceItem(
                    slug = "l", ResourceType.lANGUAGEMENU, children = arrayOf(
                        ResourceItem(
                            slug = "fi",
                            ResourceType.lANGUAGE,
                            properties = arrayOf(getKeyValue("description", "Finnish language page")),
                            styles = arrayOf(getKeyValue("background", "#fff"), getKeyValue("color", "#00f")),
                            children = arrayOf(
                                ResourceItem(
                                    slug = "menu", ResourceType.mENU, children = arrayOf(
                                        ResourceItem(
                                            slug = "menu-page-1", ResourceType.pAGE, children = arrayOf(
                                                ResourceItem(
                                                    slug = "video",
                                                    ResourceType.vIDEO,
                                                    data = "https://cdn.example.com/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b"
                                                ),
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val createdContentVersion = createResourceFromItem(
            builder = builder,
            item = rootItem,
            customer = customer,
            device = device,
            application = application,
            parentId = application.rootResourceId!!,
            orderNumber = 0
        )

        builder.admin.applications.updateApplication(
            customer = customer,
            device = device,
            application = application.copy(activeContentVersionResourceId = createdContentVersion.id!!)
        )

        assertNotNull(createdContentVersion)

        val exportedWallJson = builder.admin.wallApplication.getApplicationJson(applicationId = applicationId)
        assertNotNull(exportedWallJson)
        assertEquals(rootItem.children.size, exportedWallJson.root.children.size)

        val importedContentVersion = builder.admin.resources.importWallApplication(
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = applicationId,
            wallApplication = exportedWallJson
        )

        assertNotNull(importedContentVersion)

        assertImportedResourceTree(
            builder = builder,
            customer = customer,
            device = device,
            application = application,
            importedResource = importedContentVersion,
            originalResource = createdContentVersion
        )

        builder.admin.applications.updateApplication(
            customer = customer,
            device = device,
            application = application.copy(activeContentVersionResourceId = originalContentVersionId)
        )
    }

    @Test
    fun testWallApplicationJsonImportInvalidJson(): Unit = TestBuilder().use { builder ->
        val customer = builder.admin.customers.create()
        val device = builder.admin.devices.create(customer)

        val application = builder.admin.applications.create(
            customer = customer,
            device = device,
            name = "application's name"
        )

        builder.admin.resources.assertImportWallApplicationFail(
            expectedStatus = 400,
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            wallApplication = WallApplication(
                modifiedAt = "2000-01-01T00:00:00.000Z",
                root = WallResource(
                    name = "1",
                    slug = "1",
                    data = "placeholder",
                    type = ResourceType.mENU,
                    modifiedAt = "2000-01-01T00:00:00.000Z",
                    children = arrayOf(),
                    styles = mapOf(),
                    properties = mapOf()
                )
            )
        )
    }

    @Test
    fun testWallApplicationJsonImportPermissions(): Unit = TestBuilder().use { builder ->
        val customer = builder.admin.customers.create(name = "customer-1")
        val device = builder.admin.devices.create(customer)

        val application = builder.admin.applications.create(
            customer = customer,
            device = device,
            name = "application's name"
        )

        val wallApplication = WallApplication(
            modifiedAt = "2000-01-01T00:00:00.000Z",
            root = WallResource(
                name = "1",
                slug = "1",
                data = "placeholder",
                type = ResourceType.cONTENTVERSION,
                modifiedAt = "2000-01-01T00:00:00.000Z",
                children = arrayOf(),
                styles = mapOf(),
                properties = mapOf()
            )
        )

        builder.customer1User.resources.assertImportWallApplicationFail(
            expectedStatus = 403,
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            wallApplication = wallApplication
        )

        builder.customer1Admin.resources.assertImportWallApplicationFail(
            expectedStatus = 403,
            customerId = customer.id,
            deviceId = device.id,
            applicationId = application.id,
            wallApplication = wallApplication
        )

        builder.customer2User.resources.assertImportWallApplicationFail(
            expectedStatus = 403,
            customerId = customer.id,
            deviceId = device.id,
            applicationId = application.id,
            wallApplication = wallApplication
        )

        builder.customer2Admin.resources.assertImportWallApplicationFail(
            expectedStatus = 403,
            customerId = customer.id,
            deviceId = device.id,
            applicationId = application.id,
            wallApplication = wallApplication
        )
    }

    /**
     * Asserts that resource tree is copied correctly
     *
     * @param builder builder
     * @param customer customer
     * @param device device
     * @param application application
     * @param source source resource
     * @param target target resource
     */
    private fun assertCopiedTree(builder: TestBuilder, customer: Customer, device: Device, application: Application, source: Resource, target: Resource) {
        assertCopiedResource(source = source, target = target)

        val sourceChildren = builder.admin.resources.listResources(
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            parentId = source.id!!,
            resourceType = null
        )

        val targetChildren = builder.admin.resources.listResources(
            customerId = customer.id,
            deviceId = device.id,
            applicationId = application.id,
            parentId = target.id!!,
            resourceType = null
        )

        assertEquals(sourceChildren.size, targetChildren.size)

        sourceChildren.forEachIndexed { index, sourceChild ->
            val targetChild = targetChildren[index]

            assertCopiedTree(
                builder = builder,
                customer = customer,
                device = device,
                application = application,
                source = sourceChild,
                target = targetChild
            )
        }
    }

    /**
     * Asserts that copied resource is copied correctly
     *
     * @param source source resource
     * @param target target resource
     */
    private fun assertCopiedResource(source: Resource, target: Resource?) {
        assertNotNull(source)
        assertNotNull(target)
        assertNotEquals(source.id, target!!.id)
        assertEquals(source.data, target.data)
        assertEquals(source.orderNumber, target.orderNumber)
        assertEquals(source.type, target.type)
        assertDeepEquals(source.properties, target.properties)
        assertDeepEquals(source.styles, target.styles)

        if (source.parentId == target.parentId) {
            assertNotEquals(source.name, target.name)
            assertNotEquals(source.slug, target.slug)
        } else {
            assertEquals(source.name, target.name)
            assertEquals(source.slug, target.slug)
        }
    }

    /**
     * Asserts that imported resource tree is imported as expected
     *
     * @param builder test builder
     * @param customer customer
     * @param device device
     * @param application application
     * @param importedResource imported resource
     * @param originalResource original resource
     */
    private fun assertImportedResourceTree(
        builder: TestBuilder,
        customer: Customer,
        device: Device,
        application: Application,
        importedResource: Resource,
        originalResource: Resource
    ) {
        assertNotEquals(originalResource.id, importedResource.id)
        assertEquals(originalResource.slug, importedResource.slug)
        assertEquals(originalResource.type, importedResource.type)
        assertEquals(originalResource.orderNumber, importedResource.orderNumber)
        assertDeepEquals(originalResource.properties, importedResource.properties)
        assertDeepEquals(originalResource.styles, importedResource.styles)
        assertEquals(originalResource.data, importedResource.data)

        val originalChildren = builder.admin.resources.listResources(
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = application.id!!,
            parentId = originalResource.id!!
        )

        val importedChildren = builder.admin.resources.listResources(
            customerId = customer.id,
            deviceId = device.id,
            applicationId = application.id,
            parentId = importedResource.id!!
        )

        assertEquals(originalChildren.size, importedChildren.size)

        originalChildren.forEachIndexed { index, originalChild ->
            val importedChild = importedChildren[index]

            assertNotEquals(originalChild.parentId, importedChild.parentId)
            assertImportedResourceTree(
                builder = builder,
                customer = customer,
                device = device,
                application = application,
                importedResource = importedChild,
                originalResource = originalChild
            )
        }
    }
}

