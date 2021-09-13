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

    private var admin: TestBuilderAuthentication? = null

    private var user: TestBuilderAuthentication? = null

    override fun createTestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider): AuthorizedTestBuilderAuthentication<ApiClient> {
        return TestBuilderAuthentication(this, accessTokenProvider)
    }

    /**
     * Returns authentication resource for admin
     *
     * @return authentication resource for admin
     * @throws IOException
     */
    @kotlin.jvm.Throws(IOException::class)
    fun admin(): TestBuilderAuthentication {
        if (admin == null) {
            val authServerUrl: String = ConfigProvider.getConfig().getValue("oioi.keycloak.url", String::class.java)
            val realm: String = ConfigProvider.getConfig().getValue("oioi.keycloak.realm", String::class.java)
            val clientId = "ui"
            val username = "admin"
            val password = "admin"
            admin = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, username, password, null))
        }

        return admin!!
    }

    /**
     * Returns authentication resource authenticated as non registered Tero Äyrämö
     *
     * @return authentication resource authenticated as non registered Tero Äyrämö
     * @throws IOException
     */
    @kotlin.jvm.Throws(IOException::class)
    fun user(): TestBuilderAuthentication {
        if (user == null) {
            val authServerUrl: String = ConfigProvider.getConfig().getValue("oioi.keycloak.url", String::class.java)
            val realm: String = ConfigProvider.getConfig().getValue("oioi.keycloak.realm", String::class.java)
            val clientId = "ui"
            val username = "user"
            val password = "user"
            user = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, username, password, null))
        }

        return user!!
    }
    
}