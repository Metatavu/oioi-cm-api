package fi.metatavu.oioi.cm.liquibase.changes

import fi.metatavu.oioi.cm.authz.ResourceScope
import fi.metatavu.oioi.cm.authz.ResourceType
import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import org.eclipse.microprofile.config.ConfigProvider
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.authorization.client.Configuration
import org.keycloak.representations.idm.authorization.ResourceRepresentation
import org.keycloak.representations.idm.authorization.ScopeRepresentation
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.stream.Collectors

/**
 * Abstract base class for custom Liquibase migrations
 */
abstract class AbstractCustomTaskChange: CustomTaskChange {

    private val keycloakUrl: String? = getConfigValue("oioi.keycloak.url")
    private val keycloakRealm: String? = getConfigValue("oioi.keycloak.realm")
    private val keycloakClientId: String? = getConfigValue("quarkus.oidc.client-id")
    private val keycloakSecret: String? = getConfigValue("quarkus.oidc.credentials.secret")

    /**
     * Return keycloak authorization client
     */
    protected val authzClient: AuthzClient
        get() {
            val configuration = Configuration()
            configuration.realm = keycloakRealm
            configuration.isBearerOnly = true
            configuration.authServerUrl = keycloakUrl
            configuration.sslRequired = "external"
            configuration.resource = keycloakClientId
            configuration.confidentialPort = 0
            configuration.credentials["secret"] = keycloakSecret
            return AuthzClient.create(configuration)
        }

    override fun setUp() {

    }

    override fun setFileOpener(resourceAccessor: ResourceAccessor?) {

    }

    override fun validate(database: Database?): ValidationErrors {
        return ValidationErrors()
    }

    /**
     * Converts UUID into bytes
     *
     * @param uuid UUID
     * @return bytes
     */
    protected fun getUUIDBytes(uuid: UUID): ByteArray {
        val result = ByteArray(16)

        ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN)
            .putLong(uuid.mostSignificantBits)
            .putLong(uuid.leastSignificantBits)

        return result
    }

    /**
     * Converts UUID bytes into UUID
     *
     * @param bytes bytes
     * @return UUID
     */
    protected fun getUUIDFromBytes(bytes: ByteArray): UUID {
        val byteBuffer = ByteBuffer.wrap(bytes)
        val high = byteBuffer.long
        val low = byteBuffer.long
        return UUID(high, low)
    }

    /**
     * Creates protected resource to Keycloak
     *
     * @param authzClient authz client
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param resourceId resource id
     * @param userId userId
     *
     * @return created resource
     */
    protected fun createProtectedResource(
        authzClient: AuthzClient,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        userId: UUID
    ): ResourceRepresentation? {
        val scopes = Arrays.stream(ResourceScope.values())
            .map { obj: ResourceScope -> obj.scope }
            .map { name: String? ->
                ScopeRepresentation(
                    name
                )
            }
            .collect(Collectors.toSet())
        val resourceUri = String.format(
            "/v1/%s/devices/%s/applications/%s/resources/%s",
            customerId,
            deviceId,
            applicationId,
            resourceId
        )
        val keycloakResource =
            ResourceRepresentation(resourceId.toString(), scopes, resourceUri, ResourceType.RESOURCE.type)
        keycloakResource.setOwner(userId.toString())
        keycloakResource.ownerManagedAccess = true
        val result = authzClient.protection().resource().create(keycloakResource)
        if (result != null) {
            return result
        }
        val resources = authzClient.protection().resource().findByUri(resourceUri)
        return if (resources.isNotEmpty()) {
            resources[0]
        } else null
    }

    /**
     * Returns value from config
     *
     * @param key key
     * @return value from config
     */
    private fun getConfigValue(key: String): String? {
        return ConfigProvider.getConfig().getOptionalValue(key, String::class.java)
            .orElse(null)
    }

}