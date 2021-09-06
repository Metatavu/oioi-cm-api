package fi.metatavu.oioi.cm.applications

import fi.metatavu.oioi.cm.model.ResourceType
import fi.metatavu.oioi.cm.persistence.dao.ApplicationDAO
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Customer
import fi.metatavu.oioi.cm.persistence.model.Device
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
    private lateinit var resourceController: ResourceController

    @Inject
    private lateinit var applicationDAO: ApplicationDAO

    /**
     * Create application
     *
     * @param authzClient authz client
     * @param customer customer
     * @param device device
     * @param name name
     * @param creatorId creator id
     * @return created application
     */
    fun createApplication(
        authzClient: AuthzClient,
        customer: Customer,
        device: Device,
        name: String,
        creatorId: UUID
    ): Application {
        val applicationId = UUID.randomUUID()
        val rootResource = resourceController.createResource(
            authzClient = authzClient,
            customer = customer,
            device = device,
            applicationId = applicationId,
            orderNumber = 0,
            parent = null,
            data = null,
            name = name,
            slug = "[root]",
            type = ResourceType.ROOT,
            creatorId = creatorId
        )

        val defaultContentVersion = resourceController.createResource(
            authzClient = authzClient,
            customer = customer,
            device = device,
            applicationId = applicationId,
            orderNumber = 0,
            parent = rootResource,
            data = null,
            name = "1",
            slug = "1",
            type = ResourceType.CONTENT_VERSION,
            creatorId = creatorId
        )

        return applicationDAO.create(applicationId, name, rootResource, defaultContentVersion, device, creatorId, creatorId)
    }

    /**
     * Find application by id
     *
     * @param id application id
     * @return found application or null if not found
     */
    fun findApplicationById(id: UUID?): Application? {
        return applicationDAO.findById(id!!)
    }

    /**
     * Update application
     *
     * @param name name
     * @param lastModifierId last modifier id
     * @return updated application
     */
    fun updateApplication(application: Application, name: String?, lastModifierId: UUID?): Application {
        applicationDAO.updateName(application, name, lastModifierId)
        return application
    }

    /**
     * Lists applications from device
     *
     * @param device device to list the applications from
     * @return List of applications
     */
    fun listDeviceApplications(device: Device?): List<Application> {
        return applicationDAO.listByDevice(device)
    }

    /**
     * Delete application
     *
     * @param authzClient authz client
     * @param application application
     */
    fun deleteApplication(authzClient: AuthzClient, application: Application) {
        val rootResource = application.rootResource
        applicationDAO.delete(application)
        resourceController.delete(authzClient, rootResource!!)
    }
}
