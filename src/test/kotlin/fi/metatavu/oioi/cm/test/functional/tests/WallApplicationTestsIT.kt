package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
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
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device!!)
            val langFi = builder.admin().resources.create(
                customer,
                device,
                application!!,
                0,
                application.rootResourceId,
                null,
                "fi",
                "fi",
                ResourceType.lANGUAGE,
                arrayOf(getKeyValue("description", "Finnish language page")),
                arrayOf(getKeyValue("background", "#fff"), getKeyValue("color", "#00f"))
            )

            val intro = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                langFi!!.id,
                null,
                "Intro",
                "intro",
                ResourceType.iNTRO
            )

            val introSlide = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                intro!!.id,
                null,
                "Intro slideshow",
                "slideshow",
                ResourceType.sLIDESHOW
            )

            val introPage1 = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                introSlide!!.id,
                null,
                "Intro slideshow page 1",
                "page-1",
                ResourceType.pAGE
            )

            builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                introPage1!!.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e",
                "Intro PDF",
                "pdf",
                ResourceType.pDF
            )

            val introPage2 = builder.admin().resources.create(
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

            val introPage2Image = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                introPage2!!.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a",
                "Intro Image",
                "image",
                ResourceType.iMAGE
            )

            val introPage2Text = builder.admin().resources.create(
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

            val menu = builder.admin().resources.create(
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

            val menuPage1 = builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                menu!!.id,
                null,
                "Menu Page 1",
                "page-1",
                ResourceType.pAGE
            )

            builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                menuPage1!!.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b",
                "Video",
                "video",
                ResourceType.vIDEO
            )

            val menuPage2 = builder.admin().resources.create(
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

            builder.admin().resources.create(
                customer,
                device,
                application,
                0,
                menuPage2!!.id,
                "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b",
                "Video",
                "video",
                ResourceType.vIDEO
            )

            val menuPage2Video = builder.admin().resources.create(
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

            val exportWallApplication = builder.admin().wallApplication.getApplicationJson(application.id!!)
            assertNotNull(exportWallApplication, "Assert JSON not null",)
            assertNotNull(exportWallApplication.root, "Assert JSON root not null",)
            Asserts.assertEqualsOffsetDateTime(menuPage2Video?.modifiedAt, exportWallApplication.modifiedAt)

            val exportRootChildren = exportWallApplication.root.children
            assertEquals(1, exportRootChildren.size.toLong(), "Assert 1 root child")
            assertEquals(langFi.slug, exportRootChildren[0].slug, "Assert exported root child slug")
            val exportIntro = exportRootChildren[0].children[0]
            assertEquals(intro.slug, exportIntro.slug, "Assert intro slug")
            val exportIntroSlide = exportIntro.children[0]
            assertEquals(introSlide.slug, exportIntroSlide.slug, "Assert intro slide slug")
            val exportIntroPage2 = exportIntroSlide.children[1]
            assertEquals(introPage2.slug, exportIntroPage2.slug, "Assert intro page 2 slug")
            val exportIntroPage2Image = exportIntroPage2.children[0]
            assertEquals(exportIntroPage2Image.slug, introPage2Image!!.slug,"Assert intro page 2 image slug")
            val exportIntroPage2Text = exportIntroPage2.children[1]
            assertEquals(exportIntroPage2Text.slug, introPage2Text!!.slug,"Assert intro page 2 text slug")
        }
    }
}