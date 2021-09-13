package fi.metatavu.oioi.cm.keycloak

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Class for keycloak controller
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class KeycloakController {

    @Inject
    @ConfigProperty(name = "oioi.keycloak.url")
    private lateinit var authServerUrl: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.realm")
    private lateinit var realm: String

    @Inject
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    private lateinit var clientSecret: String

    @Inject
    @ConfigProperty(name = "quarkus.oidc.client-id")
    private lateinit var clientId: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.api-admin.user")
    private lateinit var apiAdminUser: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.api-admin.password")
    private lateinit var apiAdminPassword: String

    /**
     * Gets username from keycloak
     *
     * @param userId UUID keycloak ID
     * @return username
     */
    fun getUsername(userId: UUID): String? {
        val userResource = getUserResource(userId) ?: return null
        val userRepresentation = userResource.toRepresentation() ?: return null
        return userRepresentation.username
    }

    /**
     * Gets user resources object from keycloak
     *
     * @param userId UUID
     * @return found UserResource or null
     */
    private fun getUserResource(userId: UUID): UserResource? {
        val keycloakClient = getKeycloakClient()
        val foundRealm = keycloakClient.realm(realm)  ?: return null
        val users: UsersResource = foundRealm.users()
        return users[userId.toString()] ?: return null
    }

    /**
    * Constructs a Keycloak client
    *
    * @return Keycloak client
    */
    private fun getKeycloakClient(): Keycloak {

        return KeycloakBuilder.builder()
          .serverUrl(authServerUrl)
          .realm(realm)
          .username(apiAdminUser)
          .password(apiAdminPassword)
          .clientId(clientId)
          .clientSecret(clientSecret)
          .build()
    }

}