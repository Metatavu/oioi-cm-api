package fi.metatavu.oioi.cm.test.functional.builder.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.oioi.cm.client.apis.WallDevicesApi
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.client.infrastructure.ClientException
import fi.metatavu.oioi.cm.client.models.WallDevice
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for devices
 *
 * @author Antti Leppä
 */
class WallDeviceTestBuilderResource (
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<WallDevice, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): WallDevicesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return WallDevicesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Returns a wall device JSON
     *
     * @param deviceId device id
     * @param apiKey api key (optional)
     * @return wall device JSON
     * @throws ClientException
     */
    @Throws(ClientException::class)
    fun getDeviceJson(deviceId: UUID, apiKey: String? = null): WallDevice {
        try {
            if (apiKey != null) {
                ApiClient.apiKey["X-API-KEY"] = apiKey
            }

            return api.getDeviceJson(deviceId = deviceId)
        } finally {
            ApiClient.apiKey.remove("X-API-KEY")
        }
    }

    /**
     * Asserts that getDeviceJson status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param deviceId device id
     * @param apiKey api key (optional)
     */
    fun assertGetDeviceJsonStatus(expectedStatus: Int, deviceId: UUID, apiKey: String? = null) {
        try {
            getDeviceJson(deviceId = deviceId, apiKey = apiKey)
            fail(String.format("Expected get JSON to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus, e.statusCode)
        }
    }

    override fun clean(t: WallDevice) {
    }
}