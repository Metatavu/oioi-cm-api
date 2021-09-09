package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.client.apis.WallApplicationsApi
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.WallApplication
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for applications
 *
 * @author Antti Leppä
 */
class WallApplicationTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<WallApplication, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): WallApplicationsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return WallApplicationsApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Returns a wall application JSON
     *
     * @param applicationId application id
     * @param apiKey api key (optional)
     * @return wall application JSON
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun getApplicationJson(applicationId: UUID, apiKey: String? = null): WallApplication {
        try {
            if (apiKey != null) {
                ApiClient.apiKey["X-API-KEY"] = apiKey
            }

            return api.getApplicationJson(applicationId = applicationId)
        } finally {
            ApiClient.apiKey.remove("X-API-KEY")
        }
    }

    /**
     * Returns a wall application JSON for specific content version
     *
     * @param applicationId application id
     * @param apiKey api key (optional)
     * @param slug slug
     * @return wall application JSON
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun getApplicationJsonForContentVersion(applicationId: UUID, apiKey: String? = null, slug: String): WallApplication {
        try {
            if (apiKey != null) {
                ApiClient.apiKey["X-API-KEY"] = apiKey
            }

            return api.getApplicationJsonForContentVersion(applicationId = applicationId, slug = slug)
        } finally {
            ApiClient.apiKey.remove("X-API-KEY")
        }
    }

    /**
     * Asserts that getApplicationJson status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param applicationId application id
     * @param apiKey api key (optional)
     */
    fun assertGetApplicationJsonStatus(expectedStatus: Int, applicationId: UUID, apiKey: String? = null) {
        try {
            getApplicationJson(applicationId = applicationId, apiKey = apiKey)
            fail(String.format("Expected get JSON to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    override fun clean(t: WallApplication) {
    }
}