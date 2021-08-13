package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.apis.MediasApi
import java.util.UUID
import java.util.HashMap
import kotlin.jvm.JvmOverloads
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.Customer
import fi.metatavu.oioi.cm.client.models.Media
import fi.metatavu.oioi.cm.client.models.MediaType
import kotlin.Throws
import java.io.IOException
import org.json.JSONException
import org.junit.Assert

/**
 * Test builder resource for medias
 *
 * @author Antti Leppä
 */
class MediaTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Media, ApiClient?>(testBuilder, apiClient) {

    private val customerMediaIds: MutableMap<UUID?, UUID> = HashMap()

    override fun getApi(): MediasApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return MediasApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new media
     *
     * @param customer customer
     * @return media
     */
    @JvmOverloads
    fun create(
        customer: Customer,
        type: MediaType = MediaType.vIDEO,
        contentType: String = "video/mpeg",
        url: String = "http://example.com/video"
    ): Media? {
        val media = Media(
            contentType = contentType,
            type = type,
            url = url
        )

        val result = api.createMedia(customer.id!!, media)
        customerMediaIds[result.id!!] = customer.id

        return addClosable(result)
    }

    /**
     * Finds a media
     *
     * @param customer customer
     * @param mediaId media id
     * @return found media
     */
    fun findMedia(customer: Customer, mediaId: UUID?): Media {
        return api.findMedia(customer.id!!, mediaId!!)
    }

    /**
     * Lists medias
     *
     * @param customer customer
     * @return found medias
     */
    fun listMedias(customer: Customer, mediaType: MediaType?): Array<Media> {
        return api.listMedias(customer.id!!, mediaType)
    }

    /**
     * Updates a media into the API
     *
     * @param customer customer
     * @param body body payload
     */
    fun updateMedia(customer: Customer, body: Media): Media {
        return api.updateMedia(customer.id!!, body.id!!, body)
    }

    /**
     * Deletes a media from the API
     *
     * @param customer customer
     * @param media media to be deleted
     */
    fun delete(customer: Customer, media: Media) {
        api.deleteMedia(customer.id!!, media.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Media) {
                return@removeCloseable false
            }
            val (_, _, _, id) = closable
            id == media.id
        }
    }

    /**
     * Asserts media count within the system
     *
     * @param expected expected count
     * @param customer customer
     * @param mediaType media type
     */
    fun assertCount(expected: Int, customer: Customer, mediaType: MediaType?) {
        Assert.assertEquals(expected.toLong(), api.listMedias(customer.id!!, mediaType).size.toLong())
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param mediaId media id
     */
    fun assertFindFailStatus(expectedStatus: Int, customer: Customer, mediaId: UUID?) {
        assertFindFailStatus(expectedStatus, customer.id, mediaId)
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customerId customer id
     * @param mediaId media id
     */
    fun assertFindFailStatus(expectedStatus: Int, customerId: UUID?, mediaId: UUID?) {
        try {
            api.findMedia(customerId!!, mediaId!!)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param type type
     * @param contentType content type
     * @param url url
     */
    fun assertCreateFailStatus(
        expectedStatus: Int,
        customer: Customer,
        type: MediaType,
        contentType: String,
        url: String
    ) {
        try {
            create(customer, type, contentType, url)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param media media
     */
    fun assertUpdateFailStatus(expectedStatus: Int, customer: Customer, media: Media) {
        try {
            updateMedia(customer, media)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param media media
     */
    fun assertDeleteFailStatus(expectedStatus: Int, customer: Customer, media: Media) {
        try {
            api.deleteMedia(customer.id!!, media.id!!)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param customer customer
     * @param mediaType media type
     */
    fun assertListFailStatus(expectedStatus: Int, customer: Customer, mediaType: MediaType?) {
        try {
            listMedias(customer, mediaType)
            Assert.fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts that actual media equals expected media when both are serialized into JSON
     *
     * @param expected expected media
     * @param actual actual media
     * @throws JSONException thrown when JSON serialization error occurs
     * @throws IOException thrown when IO Exception occurs
     */
    @Throws(IOException::class, JSONException::class)
    fun assertMediasEqual(expected: Media?, actual: Media?) {
        assertJsonsEqual(expected, actual)
    }

    override fun clean(t: Media) {
        val customerId = customerMediaIds.remove(t.id)
        api.deleteMedia(customerId!!, t.id!!)
    }
}