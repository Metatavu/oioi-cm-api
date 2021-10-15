package fi.metatavu.oioi.cm.test.functional.builder

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider
import fi.metatavu.oioi.cm.client.infrastructure.ApiClient
import fi.metatavu.oioi.cm.test.functional.builder.auth.TestBuilderAuthentication
import org.eclipse.microprofile.config.ConfigProvider
import java.io.IOException

/**
 * TestBuilder implementation
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 */
open class TestBuilder: AbstractTestBuilder<ApiClient> () {

    val settings = ApiTestSettings()
    val admin = createTestBuilderAuthentication(username = "admin", password = "admin")
    val customer1User = createTestBuilderAuthentication(username = "customer-1-user", password = "pass")
    val customer1Admin = createTestBuilderAuthentication(username = "customer-1-admin", password = "pass")
    val customer2User = createTestBuilderAuthentication(username = "customer-2-user", password = "pass")
    val customer2Admin = createTestBuilderAuthentication(username = "customer-2-admin", password = "pass")

    override fun createTestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider): AuthorizedTestBuilderAuthentication<ApiClient> {
        return TestBuilderAuthentication(this, accessTokenProvider)
    }

    /**
     * Creates test builder authenticatior for given user
     *
     * @param username username
     * @param password password
     * @return test builder authenticatior for given user
     */
    private fun createTestBuilderAuthentication(username: String, password: String): TestBuilderAuthentication {
        val authServerUrl: String = ConfigProvider.getConfig().getValue("oioi.keycloak.url", String::class.java)
        val realm: String = ConfigProvider.getConfig().getValue("oioi.keycloak.realm", String::class.java)
        val clientId = "ui"
        return TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, username, password, null))
    }
    
}