package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.ApplicationsApi
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
class ApplicationsApiImpl : AbstractApi(), ApplicationsApi {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var deviceController: DeviceController

    @Inject
    lateinit var applicationController: ApplicationController

    @Inject
    lateinit var applicationTranslator: ApplicationTranslator

    @Inject
    lateinit var resourceController: ResourceController

    override fun createApplication(customerId: UUID, deviceId: UUID, application: Application): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val loggedUserId = loggedUserId ?: return createUnauthorized(UNAUTHORIZED)
        val applicationId = UUID.randomUUID()
        val rootResource = resourceController.createRootResource(
            authzClient = authzClient,
            customer = customer,
            device = device,
            applicationId = applicationId,
            name = application.name,
            creatorId = loggedUserId
        )

        val defaultContentVersion = resourceController.createDefaultContentVersionResource(
            authzClient = authzClient,
            customer = customer,
            device = device,
            applicationId = applicationId,
            parent = rootResource,
            creatorId = loggedUserId
        )

        val result = applicationController.createApplication(
            applicationId = applicationId,
            rootResource = rootResource,
            defaultContentVersion = defaultContentVersion,
            customer = customer,
            device = device,
            name = application.name,
            creatorId = loggedUserId
        )

        return createOk(applicationTranslator.translate(result))
    }

    override fun listApplications(customerId: UUID, deviceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        return if (device.customer?.id != customer.id) {
            createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        } else createOk(
            applicationTranslator.translate(applicationController.listDeviceApplications(device))
        )
    }

    override fun findApplication(customerId: UUID, deviceId: UUID, applicationId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (applicationEntity.device?.id != device.id) {
            createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE)
        } else {
            createOk(applicationTranslator.translate(applicationEntity))
        }
    }

    override fun updateApplication(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        application: Application
    ): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (applicationEntity.device?.id != device.id) {
            return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE)
        }

        val loggedUserId = loggedUserId ?: return createUnauthorized("No logged user found!")
        val name = application.name
        val activeContentVersionResourceId = application.activeContentVersionResourceId ?: return createBadRequest("No active content version resource ID found!")

        val activeContentVersionResource = resourceController.findResourceById(activeContentVersionResourceId) ?:
            return createBadRequest("Resource with ID $activeContentVersionResourceId could not be found")

        if (activeContentVersionResource.type != ResourceType.CONTENT_VERSION) {
            return createBadRequest("Resource type for active content version resource was not typed as CONTENT_VERSION!")
        }

        if (!resourceController.isApplicationResource(applicationEntity, activeContentVersionResource)) {
            return createBadRequest("Resource ${activeContentVersionResource.name} does not belong to application ${application.name}!")
        }

        val updatedApplicationEntity = applicationController.updateApplication(
            application = applicationEntity,
            name = name,
            activeContentVersionResource = activeContentVersionResource,
            lastModifierId = loggedUserId
        )

        return createOk(applicationTranslator.translate(updatedApplicationEntity))
    }

    override fun deleteApplication(customerId: UUID, deviceId: UUID, applicationId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isCustomerAdmin(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (applicationEntity.device?.id != device.id) {
            return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE)
        }

        applicationController.deleteApplication(authzClient, applicationEntity)

        return createNoContent()
    }
}