package fi.metatavu.oioi.cm.applications

import fi.metatavu.oioi.cm.persistence.dao.ApplicationDAO
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Customer
import fi.metatavu.oioi.cm.persistence.model.Device
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.resources.ResourceController
import org.keycloak.authorization.client.AuthzClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Application
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 */
@ApplicationScoped
class ApplicationController {

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var applicationDAO: ApplicationDAO

    /**
     * Create application
     *
     * @param applicationId application ID
     * @param rootResource application root resource
     * @param defaultContentVersion application default active content version
     * @param customer customer
     * @param device device
     * @param name name
     * @param creatorId creator id
     * @return created application
     */
    fun createApplication(
        applicationId: UUID,
        rootResource: Resource,
        defaultContentVersion: Resource,
        customer: Customer,
        device: Device,
        name: String,
        creatorId: UUID
    ): Application {
        return applicationDAO.create(
            id = applicationId,
            name = name,
            rootResource = rootResource,
            activeContentVersionResource = defaultContentVersion,
            device = device,
            creatorId = creatorId,
            lastModifierId = creatorId
        )
    }

    /**
     * Find application by id
     *
     * @param id application id
     * @return found application or null if not found
     */
    fun findApplicationById(id: UUID): Application? {
        return applicationDAO.findById(id)
    }

    /**
     * Update application
     *
     * @param application application
     * @param name name
     * @param activeContentVersionResource active content version resource
     * @param lastModifierId last modifier id
     * @return updated application
     */
    fun updateApplication(
        application: Application,
        name: String,
        activeContentVersionResource: Resource,
        lastModifierId: UUID
    ): Application {
        val result = applicationDAO.updateName(application, name, lastModifierId)
        applicationDAO.updateActiveContentVersionResource(result, activeContentVersionResource, lastModifierId)
        return result
    }

    /**
     * Lists applications from device
     *
     * @param device device to list the applications from
     * @return List of applications
     */
    fun listDeviceApplications(device: Device): List<Application> {
        return applicationDAO.listByDevice(device)
    }

    /**
     * Delete application
     *
     * @param authzClient authz client
     * @param application application
     */
    fun deleteApplication(authzClient: AuthzClient, application: Application) {
        val rootResource = application.rootResource ?: return
        applicationDAO.delete(application)
        resourceController.delete(authzClient = authzClient, application = application, resource = rootResource)
    }
}
