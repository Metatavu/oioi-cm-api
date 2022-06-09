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
     * Gets display name from keycloak
     *
     * @param userId UUID keycloak ID
     * @return display name
     */
    fun getDisplayName(userId: UUID): String? {
        val userResource = getUserResource(userId) ?: return null
        val userRepresentation = userResource.toRepresentation() ?: return null
        val firstName = userRepresentation.firstName
        val lastName = userRepresentation.lastName

        if (firstName == null || lastName == null) {
            return userRepresentation.email
        }

        return "$firstName $lastName"
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