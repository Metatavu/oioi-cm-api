package  fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.model.ErrorResponse
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.authorization.client.Configuration
import org.slf4j.Logger
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext


/**
 * Abstract base class for all API services
 *
 * @author Antti Leppä
 */
@RequestScoped
abstract class AbstractApi {

    @Inject
    private lateinit var logger: Logger

    @Inject
    private lateinit var jsonWebToken: JsonWebToken

    @Context
    private lateinit var securityContext: SecurityContext

    @ConfigProperty(name = "oioi.keycloak.url")
    private lateinit var keycloakUrl: String

    @ConfigProperty(name = "oioi.keycloak.realm")
    private lateinit var keycloakRealm: String

    @ConfigProperty(name = "quarkus.oidc.client-id")
    private lateinit var keycloakClientId: String

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    private lateinit var keycloakSecret: String

    /**
     * Returns logged user id
     *
     * @return logged user id
     */
    protected val loggedUserId: UUID?
        get() {
            if (jsonWebToken.subject != null) {
                return try {
                    UUID.fromString(jsonWebToken.subject)
                } catch (ex: IllegalArgumentException) {
                    logger.error(ex.message)
                    null
                }
            }

            return null
        }

    /**
     * Returns whether user has given realm role
     *
     * @param role role
     * @return whether user has given realm role
     */
    protected fun hasRealmRole(role: String): Boolean {
        return securityContext.isUserInRole(role)
    }

    /**
     * Returns whether logged user is either admin or has customer group membership
     *
     * @return whether logged user is either admin or has customer group membership
     */
    protected fun isAdminOrHasCustomerGroup(customerName: String): Boolean {
        val isAdmin = hasRealmRole(ADMIN_ROLE)
        if (isAdmin) {
            return true
        }

        val groups = jsonWebToken.groups
        return groups.contains(customerName)
    }

    /**
     * Return keycloak authorization client
     */
    protected val authzClient: AuthzClient?
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

    /**
     * Constructs ok response
     *
     * @param entity payload
     * @return response
     */
    protected fun createOk(entity: Any?): Response {
        return Response
            .status(Response.Status.OK)
            .entity(entity)
            .build()
    }

    /**
     * Constructs ok response
     *
     * @return response
     */
    protected fun createOk(): Response {
        return Response
            .status(Response.Status.OK)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @param entity payload
     * @return response
     */
    protected fun createAccepted(entity: Any?): Response {
        return Response
            .status(Response.Status.ACCEPTED)
            .entity(entity)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @return response
     */
    protected fun createNoContent(): Response {
        return Response
            .status(Response.Status.NO_CONTENT)
            .build()
    }

    /**
     * Constructs bad request response
     *
     * @param message message
     * @return response
     */
    protected fun createBadRequest(message: String): Response {
        return createError(Response.Status.BAD_REQUEST, message)
    }

    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createNotFound(message: String): Response {
        return createError(Response.Status.NOT_FOUND, message)
    }

    /**
     * Constructs not found response
     *
     * @return response
     */
    protected fun createNotFound(): Response {
        return Response
            .status(Response.Status.NOT_FOUND)
            .build()
    }


    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createConflict(message: String): Response {
        return createError(Response.Status.CONFLICT, message)
    }

    /**
     * Constructs not implemented response
     *
     * @param message message
     * @return response
     */
    protected fun createNotImplemented(message: String): Response {
        return createError(Response.Status.NOT_IMPLEMENTED, message)
    }

    /**
     * Constructs internal server error response
     *
     * @param message message
     * @return response
     */
    protected fun createInternalServerError(message: String): Response {
        return createError(Response.Status.INTERNAL_SERVER_ERROR, message)
    }

    /**
     * Constructs forbidden response
     *
     * @param message message
     * @return response
     */
    protected fun createForbidden(message: String): Response {
        return createError(Response.Status.FORBIDDEN, message)
    }

    /**
     * Constructs unauthorized response
     *
     * @param message message
     * @return response
     */
    protected fun createUnauthorized(message: String): Response {
        return createError(Response.Status.UNAUTHORIZED, message)
    }

    /**
     * Constructs an error response
     *
     * @param status status code
     * @param message message
     *
     * @return error response
     */
    private fun createError(status: Response.Status, message: String): Response {
        val entity = ErrorResponse()

        entity.message = message

        return Response
            .status(status)
            .entity(entity)
            .build()
    }

    companion object {
        const val NOT_FOUND_MESSAGE = "Not found"
        const val CUSTOMER_DEVICE_MISMATCH_MESSAGE = "Device does not belong to this customer"
        const val APPLICATION_DEVICE_MISMATCH_MESSAGE = "Application does not belong to this device"
        const val FORBIDDEN_MESSAGE = "Forbidden"
        const val ADMIN_ROLE = "admin"
    }

}