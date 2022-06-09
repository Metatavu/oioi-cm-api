package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.KeyValueProperty
import fi.metatavu.oioi.cm.client.models.ResourceType
import fi.metatavu.oioi.cm.test.common.Asserts
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Wall export functional tests
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class WallApplicationTestsIT : AbstractFunctionalTest() {

    @Test
    fun testWallApplicationExport() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val langFi = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                application.activeContentVersionResourceId,
                null,
                "fi",
                "fi",
                ResourceType.lANGUAGE,
                arrayOf(getKeyValue("description", "Finnish language page")),
                arrayOf(getKeyValue("background", "#fff"), getKeyValue("color", "#00f"))
            )

            val intro = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                langFi.id,
                null,
                "Intro",
                "intro",
                ResourceType.iNTRO
            )

            val introSlide = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                intro.id,
                null,
                "Intro slideshow",
                "slideshow",
                ResourceType.sLIDESHOW
            )

            val introPage1 = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                introSlide.id,
                null,
                "Intro slideshow page 1",
                "page-1",
                ResourceType.pAGE
            )

            builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                introPage1.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e",
                "Intro PDF",
                "pdf",
                ResourceType.pDF
            )

            val introPage2 = builder.admin.resources.create(
                customer,
                device,
                application,
                1,
                introSlide.id,
                null,
                "Intro slideshow page 1",
                "page-2",
                ResourceType.pAGE
            )

            val introPage2Image = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                introPage2.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a",
                "Intro Image",
                "image",
                ResourceType.iMAGE
            )

            val introPage2Text = builder.admin.resources.create(
                customer,
                device,
                application,
                1,
                introPage2.id,
                "Heippa maailma",
                "Intro text",
                "text",
                ResourceType.tEXT
            )

            val menu = builder.admin.resources.create(
                customer,
                device,
                application,
                1,
                langFi.id,
                null,
                "Main Menu",
                "mainmenu",
                ResourceType.mENU
            )

            val menuPage1 = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                menu.id,
                null,
                "Menu Page 1",
                "page-1",
                ResourceType.pAGE
            )

            builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                menuPage1.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b",
                "Video",
                "video",
                ResourceType.vIDEO
            )

            val menuPage2 = builder.admin.resources.create(
                customer,
                device,
                application,
                1,
                menu.id,
                null,
                "Menu Page 2",
                "page-2",
                ResourceType.pAGE
            )

            builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                menuPage2.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b",
                "Video",
                "video",
                ResourceType.vIDEO
            )

            val menuPage2Video = builder.admin.resources.create(
                customer,
                device,
                application,
                0,
                menuPage2.id,
                "Heippa taas",
                "Video text",
                "text",
                ResourceType.tEXT
            )

            val wallApplication = builder.admin.wallApplication.getApplicationJson(application.id!!)
            assertNotNull(wallApplication, "Assert JSON not null")
            assertNotNull(wallApplication.root, "Assert JSON root not null")
            Asserts.assertEqualsOffsetDateTime(menuPage2Video.modifiedAt, wallApplication.modifiedAt)

            val exportRootChildren = wallApplication.root.children
            assertEquals(1, exportRootChildren.size.toLong(), "Assert 1 root child")
            assertEquals(langFi.slug, exportRootChildren[0].slug, "Assert exported root child slug")
            val exportIntro = exportRootChildren[0].children[0]
            assertEquals(intro.slug, exportIntro.slug, "Assert intro slug")
            val exportIntroSlide = exportIntro.children[0]
            assertEquals(introSlide.slug, exportIntroSlide.slug, "Assert intro slide slug")
            val exportIntroPage2 = exportIntroSlide.children[1]
            assertEquals(introPage2.slug, exportIntroPage2.slug, "Assert intro page 2 slug")
            val exportIntroPage2Image = exportIntroPage2.children[0]
            assertEquals(exportIntroPage2Image.slug, introPage2Image.slug,"Assert intro page 2 image slug")
            val exportIntroPage2Text = exportIntroPage2.children[1]
            assertEquals(exportIntroPage2Text.slug, introPage2Text.slug,"Assert intro page 2 text slug")
        }
    }

    @Test
    fun testWallApplicationExportRootProperties() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)
            val applicationId = application.id!!
            val originalContentVersionResourceId = application.activeContentVersionResourceId

            val contentVersion1 = builder.admin.resources.create(
                customer = customer,
                device = device,
                application = application,
                name = "Content version 1",
                slug = "version-1",
                type = ResourceType.cONTENTVERSION,
                orderNumber = 1,
                parentId = application.rootResourceId,
                properties = arrayOf(
                    KeyValueProperty(key = "version-1-prop-1", value = "version 2, value 1"),
                    KeyValueProperty(key = "version-1-prop-2", value = "version 2, value 2")
                ),
                styles = arrayOf(
                    KeyValueProperty(key = "version-1-style-1", value = "version 2, style 1"),
                    KeyValueProperty(key = "version-1-style-2", value = "version 2, style 2")
                )
            )

            val contentVersion2 = builder.admin.resources.create(
                customer = customer,
                device = device,
                application = application,
                name = "Content version 2",
                slug = "version-2",
                type = ResourceType.cONTENTVERSION,
                orderNumber = 2,
                parentId = application.rootResourceId,
                properties = arrayOf(
                    KeyValueProperty(key = "version-2-prop-1", value = "version 1, value 1"),
                    KeyValueProperty(key = "version-2-prop-2", value = "version 1, value 2")
                ),
                styles = arrayOf(
                    KeyValueProperty(key = "version-2-style-1", value = "version 1, style 1"),
                    KeyValueProperty(key = "version-2-style-2", value = "version 1, style 2")
                )
            )

            builder.admin.applications.updateApplication(customer, device, application.copy(
                activeContentVersionResourceId = contentVersion1.id!!
            ))

            val wallApplication1 = builder.admin.wallApplication.getApplicationJson(applicationId)
            assertNotNull(wallApplication1, "Assert JSON not null")
            assertNotNull(wallApplication1.root, "Assert JSON root not null")
            assertNotNull(contentVersion1.properties)
            assertEquals(2, contentVersion1.properties!!.size)
            assertEquals(2, contentVersion1.styles!!.size)

            for (property in contentVersion1.properties) {
                assertEquals(property.value, wallApplication1.root.properties[property.key])
            }

            for (style in contentVersion1.styles) {
                assertEquals(style.value, wallApplication1.root.styles[style.key])
            }

            builder.admin.applications.updateApplication(customer, device, application.copy(
                activeContentVersionResourceId = contentVersion2.id!!
            ))

            val wallApplication2 = builder.admin.wallApplication.getApplicationJson(applicationId)
            assertNotNull(wallApplication2, "Assert JSON not null")
            assertNotNull(wallApplication2.root, "Assert JSON root not null")
            assertNotNull(contentVersion2.properties)
            assertEquals(2, contentVersion2.properties!!.size)
            assertEquals(2, contentVersion2.styles!!.size)

            for (property in contentVersion2.properties) {
                assertEquals(property.value, wallApplication2.root.properties[property.key])
            }

            for (style in contentVersion2.styles) {
                assertEquals(style.value, wallApplication2.root.styles[style.key])
            }

            builder.admin.applications.updateApplication(customer, device, application.copy(
                activeContentVersionResourceId = originalContentVersionResourceId
            ))
        }
    }

    @Test
    fun testWallApplicationExportApiKey() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer, apiKey = "example-api-key")
            val application = builder.admin.applications.create(customer, device)

            builder.admin.wallApplication.assertGetApplicationJsonStatus(expectedStatus = 401, applicationId = application.id!!)
            builder.admin.wallApplication.assertGetApplicationJsonStatus(expectedStatus = 403, applicationId = application.id, apiKey = "incorrect-api-key")
            assertNotNull(builder.admin.wallApplication.getApplicationJson(applicationId = application.id, apiKey = "example-api-key"))

            builder.admin.devices.updateDevice(customer = customer, body = device.copy(apiKey = ""))
            assertNotNull(builder.admin.wallApplication.getApplicationJson(applicationId = application.id, apiKey = ""))
            assertNotNull(builder.admin.wallApplication.getApplicationJson(applicationId = application.id, apiKey = null))

            builder.admin.devices.updateDevice(customer = customer, body = device.copy(apiKey = null))
            assertNotNull(builder.admin.wallApplication.getApplicationJson(applicationId = application.id, apiKey = ""))
            assertNotNull(builder.admin.wallApplication.getApplicationJson(applicationId = application.id, apiKey = null))
        }
    }

    @Test
    fun testFindSpecificContentVersion() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val device = builder.admin.devices.create(customer)
            val application = builder.admin.applications.create(customer, device)

            builder.admin.resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 1,
                parentId = application.rootResourceId,
                data = null,
                name = "second_content_version",
                slug = "second_content_version",
                type = ResourceType.cONTENTVERSION
            )

            val activeContentVersion = builder.admin.wallApplication.getApplicationJson(applicationId =  application.id!!)
            val specificContentVersion = builder.admin.wallApplication.getApplicationJsonForContentVersion(applicationId = application.id, slug = "second_content_version")

            assertEquals(activeContentVersion.root.slug, "1")
            assertEquals(specificContentVersion.root.slug, "second_content_version")

        }
    }
}