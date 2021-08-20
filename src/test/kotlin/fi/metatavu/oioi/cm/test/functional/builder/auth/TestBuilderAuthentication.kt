package fi.metatavu.oioi.cm.test.functional.builder.auth

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.test.functional.builder.impl.*

/**
 * Default implementation of test builder authentication provider
 *
 * @author Antti Leppä
 */
class TestBuilderAuthentication(
    private val testBuilder: TestBuilder,
    accessTokenProvider: AccessTokenProvider
):AuthorizedTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider

    val customers: CustomerTestBuilderResource = CustomerTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val applications: ApplicationTestBuilderResource = ApplicationTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val devices: DeviceTestBuilderResource = DeviceTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val resources: ResourceTestBuilderResource = ResourceTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val medias: MediaTestBuilderResource = MediaTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val wallApplication: WallApplicationTestBuilderResource = WallApplicationTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val wallDevice: WallDeviceTestBuilderResource = WallDeviceTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())

    /**
     * Creates a API client
     *
     * @param accessToken access token
     * @return API client
     */
    override fun createClient(accessToken: String): ApiClient {
        val result = ApiClient(testBuilder.settings.apiBasePath)
        ApiClient.accessToken = accessToken
        return result
    }

}