package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.Media
import fi.metatavu.oioi.cm.client.models.MediaType
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

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
class MediaTestsIT : AbstractFunctionalTest() {

    @Test
    fun testMedia() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            assertNotNull(
                builder.admin.medias.create(customer, MediaType.iMAGE, "image/jpeg", "http://example.com/test.jpg")
            )
        }
    }

    @Test
    fun testFindMedia() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdMedia = builder.admin.medias.create(customer)
            builder.admin.medias.assertFindFailStatus(404, customer, UUID.randomUUID())
            builder.admin.medias.assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID())
            val foundMedia = builder.admin.medias.findMedia(customer, createdMedia?.id!!)
            builder.admin.medias.assertFindFailStatus(404, UUID.randomUUID(), foundMedia.id)
            builder.admin.medias.assertMediasEqual(createdMedia, foundMedia)
        }
    }

    @Test
    fun testListMedias() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdMedia = builder.admin.medias.create(customer)
            val foundMedias: Array<Media> = builder.admin.medias.listMedias(customer, null)
            assertEquals(1, foundMedias.size.toLong())
            builder.admin.medias.assertMediasEqual(createdMedia, foundMedias[0])
            assertEquals(1, builder.admin.medias.listMedias(customer, MediaType.vIDEO).size)
            assertEquals(0, builder.admin.medias.listMedias(customer, MediaType.pDF).size)
        }
    }

    @Test
    fun testUpdateMedia() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdMedia = builder.admin.medias.create(customer)
            val updateMedia = builder.admin.medias.findMedia(customer, createdMedia!!.id!!).copy(
                contentType = "image/changed",
                type = MediaType.vIDEO,
                url = "http://www.example.com/changed"
            )

            val updatedMedia = builder.admin.medias.updateMedia(customer, updateMedia)
            assertEquals(updateMedia.id, updatedMedia.id)
            assertEquals(updateMedia.contentType, updatedMedia.contentType)
            assertEquals(updateMedia.url, updatedMedia.url)

            val foundMedia = builder.admin.medias.findMedia(customer, updatedMedia.id!!)
            assertEquals(updateMedia.id, foundMedia.id)
            assertEquals(updateMedia.contentType, foundMedia.contentType)
            assertEquals(updateMedia.url, foundMedia.url)
        }
    }

    @Test
    fun testDeleteMedia() {
        TestBuilder().use { builder ->
            val customer = builder.admin.customers.create()
            val createdMedia = builder.admin.medias.create(customer)!!
            val foundMedia = builder.admin.medias.findMedia(customer, createdMedia.id)
            assertEquals(createdMedia.id, foundMedia.id)
            builder.admin.medias.delete(customer, createdMedia)
            builder.admin.medias.assertDeleteFailStatus(404, customer, createdMedia)
        }
    }

}