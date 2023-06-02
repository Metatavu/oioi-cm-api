package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.lock.ResourceLockController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.resources.WallApplicationImporter
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.ResourcesApi
import io.quarkus.narayana.jta.runtime.TransactionConfiguration
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.Response
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

/**
 * REST - endpoints for resources
 *
 * @author Antti Leppä
 */
@RequestScoped
@Transactional
@Consumes("application/json;charset=utf-8")
@Produces("application/json;charset=utf-8")
class ResourcesApiImpl : AbstractApi(), ResourcesApi {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var deviceController: DeviceController

    @Inject
    lateinit var applicationController: ApplicationController

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var resourceTranslator: ResourceTranslator

    @Inject
    lateinit var resourceLockController: ResourceLockController

    @Inject
    lateinit var resourceLockTranslator: ResourceLockTranslator

    @Inject
    lateinit var wallApplicationImporter: WallApplicationImporter

    @Inject
    lateinit var vertx: Vertx

    override fun createResource(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        copyResourceId: UUID?,
        copyResourceParentId: UUID?,
        resource: Resource?
    ): Response {
        val loggedUserId = loggedUserId ?: return createUnauthorized(UNAUTHORIZED)

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val foundDevice = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (foundDevice.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device?.id != foundDevice.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        val result = if (copyResourceId != null) {
            if (resource != null) {
                return createBadRequest("When copyResourceId is defined, request should not have a body")
            }

            copyResourceParentId ?: return createBadRequest("copyResourceParentId is required when copyResourceId is defined")
            val source = resourceController.findResourceById(copyResourceId) ?: return createBadRequest("Invalid copy resource id")
            val targetParent = resourceController.findResourceById(copyResourceParentId) ?: return createBadRequest("Invalid copy resource parent id")

            if (source.type === ResourceType.CONTENT_VERSION && targetParent.type !== ResourceType.ROOT) {
                return createBadRequest("Content version can only be created under ROOT")
            }

            val sourceApplication = resourceController.getResourceApplication(resource = source) ?: return createInternalServerError("Could not resolve source application")

            val sourceCustomer = sourceApplication.device?.customer ?: return createInternalServerError("Could not resolve source customer")
            if (!isAdminOrHasCustomerGroup(sourceCustomer.name!!)) {
                return createForbidden(FORBIDDEN_MESSAGE)
            }

            resourceController.copyResource(
                authzClient = authzClient,
                source = source,
                targetApplication = application,
                targetParent = targetParent,
                creatorId = loggedUserId
            )
        } else {
            resource ?: return createBadRequest("Request body is required")

            val parentId = resource.parentId ?: return createBadRequest(INVALID_PARENT_ID)
            val parent = resourceController.findResourceById(parentId) ?: return createBadRequest(INVALID_PARENT_ID)

            if (resource.type === ResourceType.CONTENT_VERSION && parent.type !== ResourceType.ROOT) {
                return createBadRequest("Content version can only be created under ROOT")
            }

            if (!resourceController.isApplicationResource(application = application, resource = parent)) {
                return createForbidden(FORBIDDEN_MESSAGE)
            }

            resourceController.createResource(
                authzClient = authzClient,
                customer = customer,
                device = foundDevice,
                application = application,
                orderNumber = resource.orderNumber,
                parent = parent,
                data = resource.data,
                name = resource.name,
                slug = resource.slug,
                type = resource.type,
                properties = resource.properties ?: emptyList(),
                styles = resource.styles ?: emptyList(),
                creatorId = loggedUserId
            )
        }

        result ?: return createInternalServerError("Internal server error when creating resource")

        return createOk(resourceTranslator.translate(result))
    }

    override fun listResources(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        parentId: UUID?,
        resourceType: ResourceType?
    ): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device?.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        parentId ?: return createBadRequest(INVALID_PARENT_ID)
        val parent = resourceController.findResourceById(parentId)
            ?: return createBadRequest(INVALID_PARENT_ID)

        val resources = resourceController.listResourcesByParent(parent, resourceType)
        return createOk(resources.map(resourceTranslator::translate))
    }

    override fun findResource(customerId: UUID, deviceId: UUID, applicationId: UUID, resourceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (device.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (application.device?.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        val resource = resourceController.findResourceById(resourceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)

        return if (!resourceController.isApplicationResource(application, resource)) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else {
            val foundResource = resourceController.findResourceById(resourceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
            createOk(resourceTranslator.translate(foundResource))
        }
    }

    override fun updateResource(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        resource: Resource
    ): Response {
        val loggedUserId = loggedUserId ?: return createUnauthorized("No logged user ID!")
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound("Customer with ID: $customerId could not be found!")
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound("Device with ID: $deviceId could not be found!")
        if (device.customer?.id != customer.id) {
            return createNotFound("Devices: ${device.name} customer ID does not match given customer!")
        }

        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound("Application with ID: $applicationId could not be found!")
        if (application.device?.id != device.id) {
            return createNotFound("Applications: ${application.name} device ID does not match given device!")
        }

        val foundResource = resourceController.findResourceById(resourceId)
            ?: return createNotFound("Resource with ID: $resourceId could not be found!")
        if (foundResource.id == resource.parentId) {
            return createBadRequest(INVALID_PARENT_ID)
        }

        if (!resourceController.isApplicationResource(application, foundResource)) {
            return createBadRequest("Resource ${foundResource.name} does not belong to application ${application.name}!")
        }

        if (resourceLockController.isResourceLockedForAnotherUser(foundResource, loggedUserId)) {
            return createConflict("Resource ${foundResource.id}-${foundResource.name} is already locked for another user!")
        }

        val parentId = resource.parentId
        if (foundResource.type == ResourceType.ROOT) {
            resourceController.setResourceProperties(foundResource, resource.properties ?: emptyList(), loggedUserId)
            resourceController.setResourceStyles(foundResource, resource.styles ?: emptyList(), loggedUserId)
            return createOk(resourceTranslator.translate(foundResource))
        } else if (parentId == null) {
            return createBadRequest(INVALID_PARENT_ID)
        }

        val parent = resourceController.findResourceById(parentId)
            ?: return createBadRequest(INVALID_PARENT_ID)

        // TODO: parent permission?
        val data = resource.data
        val name = resource.name
        val slug = resource.slug
        val type = resource.type
        val orderNumber = resource.orderNumber
        resourceController.setResourceProperties(foundResource, resource.properties ?: emptyList(), loggedUserId)
        resourceController.setResourceStyles(foundResource, resource.styles ?: emptyList(), loggedUserId)

        val updatedResource = resourceController.updateResource(
            resource = foundResource,
            orderNumber = orderNumber,
            data = data,
            name = name,
            parent = parent,
            slug = slug,
            type = type,
            lastModifierId = loggedUserId
        )

        return createOk(resourceTranslator.translate(updatedResource))
    }

    override fun deleteResource(customerId: UUID, deviceId: UUID, applicationId: UUID, resourceId: UUID): Response {
        val loggedUserId = loggedUserId ?: return createUnauthorized("No logged user ID!")

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound("Customer with ID: $customerId could not be found!")

        if (!isCustomerAdmin(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound("Device with ID: $deviceId could not be found!")
        if (device.customer?.id != customer.id) {
            return createNotFound("Devices: ${device.name} customer ID does not match given customer!")
        }

        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with ID: $applicationId could not be found!")

        if (application.device?.id != device.id) {
            return createNotFound("Applications: ${application.name} device ID does not match given device!")
        }

        val resource = resourceController.findResourceById(resourceId) ?: return createNotFound("Resource with ID: $resourceId could not be found!")
        if (!resourceController.isApplicationResource(application, resource)) {
            return createNotFound("Resource ${resource.name} does not belong to application ${application.name}!")
        }

        if (resourceLockController.isResourceLockedForAnotherUser(resource, loggedUserId)) {
            return createConflict("Resource ${resource.id}-${resource.name} is already locked for another user!")
        }

        if (!resourceLockController.isUserAllowedToDeleteResource(resource = resource, loggedUserId = loggedUserId, application = application)) {
            return createConflict("Resource ${resource.id}-${resource.name} can not be deleted because some child element is locked for another user!")
        }

        resourceController.delete(authzClient = authzClient, application = application, resource = resource)

        return createNoContent()
    }

    override fun findResourceLock(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID
    ): Response {
        loggedUserId ?: return createUnauthorized(UNAUTHORIZED)

        val resource = resourceController.findResourceById(resourceId) ?: return createNotFound("Resource with ID $resourceId could not be found!")
        val foundLock = resourceLockController.findResourceLockByResource(resource) ?: return createNotFound("No lock for resource $resourceId")

        return createOk(resourceLockTranslator.translate(foundLock))
    }

    override fun updateResourceLock(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        resourceLock: ResourceLock
    ): Response {
        val loggedUserId = loggedUserId ?: return createUnauthorized(UNAUTHORIZED)

        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with ID $applicationId could not be found")
        val resource = resourceController.findResourceById(resourceId) ?: return createNotFound("Resource with ID $resourceId could not be found!")
        val foundLock = resourceLockController.findResourceLockByResource(resource)

        if (foundLock != null && !resourceLockController.isExpired(foundLock)) {
            if (foundLock.userId != loggedUserId) {
                return createConflict("Resource $resourceId is already locked for user ${foundLock.userId}")
            }

            val updatedLock = resourceLockController.updateResourceLock(foundLock)
            return createOk(resourceLockTranslator.translate(updatedLock))
        } else {
            val createdLock = resourceLockController.createResourceLock(application = application, resource = resource, userId = loggedUserId)
            return createOk(resourceLockTranslator.translate(createdLock))
        }
    }

    override fun deleteResourceLock(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID
    ): Response {
        loggedUserId ?: return createUnauthorized(UNAUTHORIZED)
        val resource = resourceController.findResourceById(resourceId) ?: return createNotFound("Resource with ID $resourceId could not be found!")
        val foundLock = resourceLockController.findResourceLockByResource(resource) ?: return createNotFound("No lock for resource $resourceId")

        if (foundLock.userId != loggedUserId) {
            return createConflict("Resource $resourceId is already locked for user ${foundLock.userId}")
        }

        resourceLockController.deleteResourceLock(foundLock)
        return createNoContent()
    }

    override fun getLockedResourceIds(applicationId: UUID, resourceId: UUID?): Response {
        loggedUserId ?: return createUnauthorized(UNAUTHORIZED)

        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with ID $applicationId could not be found")
        var resource: fi.metatavu.oioi.cm.persistence.model.Resource? = null
        if (resourceId != null) {
            resource = resourceController.findResourceById(resourceId) ?: return createNotFound("Resource with ID $resourceId could not be found!")
        }

        val foundLocks = resourceLockController.list(application = application, resource = resource)
        return createOk(resourceLockTranslator.translateLockedResourceIds(foundLocks))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @TransactionConfiguration (timeout = 60 * 5)
    override fun importWallApplication(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        wallApplication: WallApplication
    ): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound("Customer with id: $customerId could not be found!")
        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound("Device with id: $deviceId could not be found!")
        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound("Application with id: $applicationId could not be found!")

        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        if (wallApplication.root.type != ResourceType.CONTENT_VERSION) {
            return createBadRequest("Root resource must be of type CONTENT_VERSION")
        }

        return CoroutineScope(vertx.dispatcher()).async {
            val contentVersion = wallApplicationImporter.importFromWallApplicationJSON(
                authzClient = authzClient,
                wallApplication = wallApplication,
                customer = customer,
                device = device,
                application = application,
                loggedUserId = loggedUserId
            )

            createOk(resourceTranslator.translate(contentVersion))
        }.getCompleted()
    }

    companion object {
        private const val INVALID_PARENT_ID = "Invalid parent_id"
    }

}