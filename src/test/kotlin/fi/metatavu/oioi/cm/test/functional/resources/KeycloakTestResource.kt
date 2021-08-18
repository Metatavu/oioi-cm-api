package fi.metatavu.oioi.cm.test.functional.resources

import dasniko.testcontainers.keycloak.KeycloakContainer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

/**
 * Class for Keycloak test resource.
 *
 * @author Jari Nykänen
 */
class KeycloakTestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        keycloak.start()
        val config: MutableMap<String, String> = HashMap()
        config["quarkus.oidc.auth-server-url"] = java.lang.String.format("%s/realms/oioi", keycloak.authServerUrl)
        config["quarkus.oidc.client-id"] = "api"
        config["quarkus.oidc.credentials.secret"] = "a85be692-5a92-4e8f-97b8-e087ca8195f4"

        config["oioi.keycloak.url"] = keycloak.authServerUrl
        config["oioi.keycloak.realm"] = "oioi"

        return config
    }

    override fun stop() {
        keycloak.stop()
    }

    companion object {
        val keycloak: KeycloakContainer = KeycloakContainer()
            .withRealmImportFile("kc.json")
    }
}