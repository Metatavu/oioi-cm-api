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
     * @return wall application JSON
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun getApplicationJson(applicationId: UUID): WallApplication {
        return api.getApplicationJson(applicationId = applicationId)
    }

    /**
     * Asserts that getApplicationJson status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param applicationId application id
     */
    fun assertGetApplicationJsonStatus(expectedStatus: Int, applicationId: UUID) {
        try {
            getApplicationJson(applicationId = applicationId)
            fail(String.format("Expected get JSON to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    override fun clean(t: WallApplication) {
    }
}