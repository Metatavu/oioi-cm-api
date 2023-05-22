package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.WallApplicationsApi
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

/**
 * REST - endpoints for customers
 *
 * @author Antti Leppä
 */
@RequestScoped
@Transactional
@Consumes("application/json;charset=utf-8")
@Produces("application/json;charset=utf-8")
class WallApplicationsApiImpl : AbstractApi(), WallApplicationsApi {

    @Inject
    lateinit var applicationController: ApplicationController

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var wallApplicationTranslator: WallApplicationTranslator

    override fun getApplicationJson(applicationId: UUID): Response {
        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with id: $applicationId could not be found!")
        val device = application.device ?: return createInternalServerError("Could not find application device")
        val deviceApiKey = device.apiKey

        if (!deviceApiKey.isNullOrEmpty()) {
            apiKey ?: return createUnauthorized("Missing X-API-KEY header")

            if (apiKey != deviceApiKey) {
                return createForbidden("Invalid API Key provided")
            }
        }

        return createOk(wallApplicationTranslator.translate(application))
    }

    override fun getApplicationJsonForContentVersion(applicationId: UUID, slug: String): Response {
        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with id: $applicationId could not be found!")
        val device = application.device ?: return createInternalServerError("Could not find application device")
        val rootResource = application.rootResource ?: return createInternalServerError("Could not find root resource from application")
        val deviceApiKey = device.apiKey

        if (!deviceApiKey.isNullOrEmpty()) {
            apiKey ?: return createUnauthorized("Missing X-API-KEY header")

            if (apiKey != deviceApiKey) {
                return createForbidden("Invalid API Key provided")
            }
        }

        val contentVersion = resourceController.findResourceByParentAndSlug(parent = rootResource, slug = slug)
            ?: return createNotFound("Could not find resource with parent: ${rootResource.id} and slug $slug")

        return createOk(wallApplicationTranslator.translate(entity = application, contentVersion = contentVersion))
    }

}