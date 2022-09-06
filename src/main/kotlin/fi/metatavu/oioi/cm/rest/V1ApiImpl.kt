package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.lock.ResourceLockController
import fi.metatavu.oioi.cm.medias.MediaController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.V1Api
import io.quarkus.arc.Lock
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
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
class V1ApiImpl : AbstractApi(), V1Api {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var customerTranslator: CustomerTranslator

    @Inject
    lateinit var deviceController: DeviceController

    @Inject
    lateinit var deviceTranslator: DeviceTranslator

    @Inject
    lateinit var applicationController: ApplicationController

    @Inject
    lateinit var applicationTranslator: ApplicationTranslator

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var resourceTranslator: ResourceTranslator

    @Inject
    lateinit var resourceLockController: ResourceLockController

    @Inject
    lateinit var resourceLockTranslator: ResourceLockTranslator

    @Inject
    lateinit var mediaController: MediaController

    @Inject
    lateinit var mediaTranslator: MediaTranslator

    @Inject
    lateinit var wallDeviceTranslator: WallDeviceTranslator

    @Inject
    lateinit var wallApplicationTranslator: WallApplicationTranslator

    /** APPLICATIONS  */

    override fun createApplication(customerId: UUID, deviceId: UUID, payload: @Valid Application?): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        payload ?: return createBadRequest(MISSING_PAYLOAD)

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
            name = payload.name,
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
            name = payload.name,
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
        application: @Valid Application?
    ): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        application ?: return createBadRequest(MISSING_PAYLOAD)

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
        val activeContentVersionResource = resourceController.findResourceById(application.activeContentVersionResourceId) ?:
            return createBadRequest("Resource with ID ${application.activeContentVersionResourceId} could not be found")

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

    /** CUSTOMERS  */

    override fun createCustomer(customer: @Valid Customer?): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val result = customerController.createCustomer(customer!!.imageUrl, customer.name, loggedUserId)

        return createOk(customerTranslator.translate(result))
    }

    override fun listCustomers(): Response {
        return createOk(customerController
            .listAllCustomers().filter { isAdminOrHasCustomerGroup(it.name) }
            .map (customerTranslator::translate)
        )
    }

    override fun findCustomer(customerId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else {
            createOk(customerTranslator.translate(customer))
        }
    }

    override fun updateCustomer(customerId: UUID, payload: @Valid Customer?): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        customerController.updateCustomer(customer, payload!!.imageUrl, payload.name, loggedUserId)
        return createOk(customerTranslator.translate(customer))
    }

    override fun deleteCustomer(customerId: UUID): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        customerController.deleteCustomer(authzClient, customer)
        return createNoContent()
    }

    /** DEVICES  */

    override fun createDevice(customerId: UUID, payload: @Valid Device?): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        val apiKey = payload!!.apiKey
        val name = payload.name
        val imageUrl = payload.imageUrl
        val device = deviceController.createDevice(customer, apiKey, name, imageUrl, loggedUserId)
        deviceController.setDeviceMetas(device, payload.metas, loggedUserId)
        return createOk(deviceTranslator.translate(device))
    }

    override fun listDevices(customerId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else {
            createOk(deviceController.listCustomerDevices(customer).map ( deviceTranslator::translate ))
        }
    }

    override fun findDevice(customerId: UUID, deviceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        return if (device.customer?.id != customer.id) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else {
            createOk(deviceTranslator.translate(device))
        }
    }

    override fun updateDevice(customerId: UUID, deviceId: UUID, payload: @Valid Device?): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val apiKey = payload!!.apiKey
        val name = payload.name
        val imageUrl = payload.imageUrl
        deviceController.updateDevice(device, customer, apiKey, name, imageUrl, loggedUserId)
        deviceController.setDeviceMetas(device, payload.metas, loggedUserId)
        return createOk(deviceTranslator.translate(device))
    }

    override fun deleteDevice(customerId: UUID, deviceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (!isCustomerAdmin(customerName = customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        deviceController.deleteDevice(authzClient, device)

        return createNoContent()
    }

    /** RESOURCES  */

    override fun createResource(
        customerId: UUID?,
        deviceId: UUID?,
        applicationId: UUID?,
        copyResourceId: UUID?,
        copyResourceParentId: UUID?,
        payload: Resource?
    ): Response {
        val loggedUserId = loggedUserId ?: return createUnauthorized(UNAUTHORIZED)

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        applicationId ?: return createBadRequest("Missing application ID from request")

        val application = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device?.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        val result = if (copyResourceId != null) {
            if (payload != null) {
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
            payload ?: return createBadRequest("Request body is required")

            val parentId = payload.parentId ?: return createBadRequest(INVALID_PARENT_ID)
            val parent = resourceController.findResourceById(parentId) ?: return createBadRequest(INVALID_PARENT_ID)

            if (payload.type === ResourceType.CONTENT_VERSION && parent.type !== ResourceType.ROOT) {
                return createBadRequest("Content version can only be created under ROOT")
            }

            if (!resourceController.isApplicationResource(application = application, resource = parent)) {
                return createForbidden(FORBIDDEN_MESSAGE)
            }

            resourceController.createResource(
                authzClient = authzClient,
                customer = customer,
                device = device,
                application = application,
                orderNumber = payload.orderNumber,
                parent = parent,
                data = payload.data,
                name = payload.name,
                slug = payload.slug,
                type = payload.type,
                properties = payload.properties,
                styles = payload.styles,
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
        parentId: UUID,
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
        payload: @Valid Resource?
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

        val resource = resourceController.findResourceById(resourceId)
            ?: return createNotFound("Resource with ID: $resourceId could not be found!")
        if (resource.id == payload!!.parentId) {
            return createBadRequest(INVALID_PARENT_ID)
        }

        if (!resourceController.isApplicationResource(application, resource)) {
            return createBadRequest("Resource ${resource.name} does not belong to application ${application.name}!")
        }

        if (resourceLockController.isResourceLockedForAnotherUser(resource, loggedUserId)) {
            return createConflict("Resource ${resource.id}-${resource.name} is already locked for another user!")
        }

        val parentId = payload.parentId
        if (resource.type == ResourceType.ROOT) {
            resourceController.setResourceProperties(resource, payload.properties, loggedUserId)
            resourceController.setResourceStyles(resource, payload.styles, loggedUserId)
            return createOk(resourceTranslator.translate(resource))
        } else if (parentId == null) {
            return createBadRequest(INVALID_PARENT_ID)
        }

        val parent = resourceController.findResourceById(parentId)
            ?: return createBadRequest(INVALID_PARENT_ID)

        // TODO: parent permission?
        val data = payload.data
        val name = payload.name
        val slug = payload.slug
        val type = payload.type
        val orderNumber = payload.orderNumber
        resourceController.setResourceProperties(resource, payload.properties, loggedUserId)
        resourceController.setResourceStyles(resource, payload.styles, loggedUserId)

        val updatedResource = resourceController.updateResource(
            resource = resource,
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

    @Lock
    override fun updateResourceLock(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        payload: ResourceLock?
    ): Response {
        payload ?: return createBadRequest(MISSING_PAYLOAD)
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

    /** MEDIAS  */
    override fun createMedia(customerId: UUID, media: @Valid Media?): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else createOk(
            mediaTranslator.translate(
                mediaController.createMedia(
                    customer,
                    media!!.contentType,
                    media.type,
                    media.url,
                    loggedUserId
                )
            )
        )
    }

    override fun listMedias(customerId: UUID, type: MediaType?): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else createOk(mediaTranslator.translate(mediaController.listMedias(customer, type)))
    }

    override fun findMedia(customerId: UUID, mediaId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val media = mediaController.findMediaById(mediaId)
        return if (media == null || media.customer?.id != customer.id) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else createOk(mediaTranslator.translate(media))
    }

    override fun updateMedia(customerId: UUID, mediaId: UUID, payload: @Valid Media?): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val media = mediaController.findMediaById(mediaId)
        return if (media == null || media.customer?.id != customer.id) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else createOk(
            mediaTranslator.translate(
                mediaController.updateMedia(
                    media,
                    payload!!.contentType,
                    payload.type,
                    payload.url,
                    loggedUserId
                )
            )
        )
    }

    override fun deleteMedia(customerId: UUID, mediaId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (!isCustomerAdmin(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val media = mediaController.findMediaById(mediaId)
        if (media == null || media.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        mediaController.deleteMedia(media)
        return createNoContent()
    }

    /* Wall */

    override fun getApplicationJson(applicationId: UUID?): Response {
        applicationId ?: return createBadRequest("Missing application ID from request")

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

    override fun getApplicationJsonForContentVersion(applicationId: UUID?, slug: String?): Response {
        applicationId ?: return createBadRequest("Missing application ID from request")
        slug ?: return createBadRequest("Missing slug from request")

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

    override fun getDeviceJson(deviceId: UUID?): Response {
        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound("Device with id: $deviceId could not be found!")
        val deviceApiKey = device.apiKey

        if (!deviceApiKey.isNullOrEmpty()) {
            apiKey ?: return createUnauthorized("Missing X-API-KEY header")

            if (apiKey != deviceApiKey) {
                return createForbidden("Invalid API Key provided")
            }
        }

        return createOk(wallDeviceTranslator.translate(device))
    }

    companion object {
        private const val INVALID_PARENT_ID = "Invalid parent_id"
    }
}