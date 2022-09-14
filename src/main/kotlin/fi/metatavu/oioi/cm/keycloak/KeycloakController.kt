package fi.metatavu.oioi.cm.keycloak

import io.quarkus.cache.CacheResult
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import org.slf4j.Logger
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
    lateinit var logger: Logger

    @Inject
    @ConfigProperty(name = "oioi.keycloak.url")
    lateinit var authServerUrl: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.realm")
    lateinit var realm: String

    @Inject
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    lateinit var clientSecret: String

    @Inject
    @ConfigProperty(name = "quarkus.oidc.client-id")
    lateinit var clientId: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.api-admin.user")
    lateinit var apiAdminUser: String

    @Inject
    @ConfigProperty(name = "oioi.keycloak.api-admin.password")
    lateinit var apiAdminPassword: String

    /**
     * Gets display name from keycloak
     *
     * @param userId UUID keycloak ID
     * @return display name
     */
    @CacheResult(cacheName = "user-display-name-cache")
    fun getDisplayName(userId: UUID): String? {
        try {
            val userResource = getUserResource(userId) ?: return null
            val userRepresentation = userResource.toRepresentation() ?: return null
            val firstName = userRepresentation.firstName
            val lastName = userRepresentation.lastName

            if (firstName == null || lastName == null) {
                return userRepresentation.email
            }

            return "$firstName $lastName"
        } catch (e: Exception) {
            logger.warn("Failed to fetch display name of user $userId", e)
            return null
        }
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