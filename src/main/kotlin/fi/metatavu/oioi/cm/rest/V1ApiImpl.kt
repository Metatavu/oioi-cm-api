package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.medias.MediaController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.V1Api
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

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val result = applicationController.createApplication(
            authzClient,
            customer,
            device,
            payload!!.name,
            loggedUserId
        )

        return createOk(applicationTranslator.translate(result))
    }

    override fun listApplications(customerId: UUID, deviceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        return if (device.customer.id != customer.id) {
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
        if (device.customer.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (applicationEntity.device.id != device.id) {
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

        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (applicationEntity.device.id != device.id) {
            return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val updatedApplicationEntity = applicationController.updateApplication(applicationEntity, application!!.name, loggedUserId)

        return createOk(applicationTranslator.translate(updatedApplicationEntity))
    }

    override fun deleteApplication(customerId: UUID, deviceId: UUID, applicationId: UUID): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        val device = deviceController.findDeviceById(deviceId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE)
        }

        val applicationEntity = applicationController.findApplicationById(applicationId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (applicationEntity.device.id != device.id) {
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
        return if (hasRealmRole(ADMIN_ROLE)) {
            createOk(customerController.listAllCustomers().map (customerTranslator::translate))
        } else {
            createOk(customerController.listCustomersByNameIn(loggedUserGroups).map (customerTranslator::translate))
        }
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

        return if (device.customer.id != customer.id) {
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
        if (device.customer.id != customer.id) {
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
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        deviceController.deleteDevice(authzClient, device)
        return createNoContent()
    }

    /** RESOURCES  */
    override fun createResource(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        payload: @Valid Resource?
    ): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val parentId = payload!!.parentId
        val parent = resourceController.findResourceById(parentId)
            ?: return createBadRequest(INVALID_PARENT_ID)

        // TODO: parent permission?
        val data = payload.data
        val name = payload.name
        val slug = payload.slug
        val orderNumber = payload.orderNumber
        val type = payload.type
        return createOk(
            resourceTranslator.translate(
                resourceController.createResource(
                    authzClient,
                    customer,
                    device,
                    application,
                    orderNumber,
                    parent,
                    data,
                    name,
                    slug,
                    type,
                    payload.properties,
                    payload.styles,
                    loggedUserId
                )
            )
        )
    }

    override fun listResources(customerId: UUID, deviceId: UUID, applicationId: UUID, parentId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val parent = resourceController.findResourceById(parentId)
            ?: return createBadRequest(INVALID_PARENT_ID)
        return createOk(resourceTranslator.translate(resourceController.listResourcesByParent(parent)))
    }

    override fun findResource(customerId: UUID, deviceId: UUID, applicationId: UUID, resourceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val resource = resourceController.findResourceById(resourceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!resourceController.isApplicationResource(application, resource)) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else createOk(
            resourceTranslator.translate(
                resourceController.findResourceById(resourceId)
            )
        )
    }

    override fun updateResource(
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        payload: @Valid Resource?
    ): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val resource = resourceController.findResourceById(resourceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (resource.id == payload!!.parentId) {
            return createBadRequest(INVALID_PARENT_ID)
        }
        if (!resourceController.isApplicationResource(application, resource)) {
            return createNotFound(NOT_FOUND_MESSAGE)
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
        return createOk(
            resourceTranslator.translate(
                resourceController.updateResource(
                    resource,
                    orderNumber,
                    data,
                    name,
                    parent,
                    slug,
                    type,
                    loggedUserId
                )
            )
        )
    }

    override fun deleteResource(customerId: UUID, deviceId: UUID, applicationId: UUID, resourceId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val device = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (device.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val application = applicationController.findApplicationById(applicationId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (application.device.id != device.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val resource = resourceController.findResourceById(resourceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!resourceController.isApplicationResource(application, resource)) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        resourceController.delete(authzClient, resource)
        return createNoContent()
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
        return if (media == null || media.customer.id != customer.id) {
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
        return if (media == null || media.customer.id != customer.id) {
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
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val media = mediaController.findMediaById(mediaId)
        if (media == null || media.customer.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        mediaController.deleteMedia(media)
        return createNoContent()
    }

    /* Wall */

    override fun getApplicationJson(applicationId: UUID?): Response {
        val application = applicationController.findApplicationById(applicationId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        val device = application.device
        val deviceApiKey = device.apiKey

        if (!deviceApiKey.isNullOrEmpty()) {
            if (apiKey == null) {
                return createUnauthorized("Missing X-API-KEY header")
            }

            if (apiKey != deviceApiKey) {
                return createForbidden("Invalid API Key provided")
            }
        }

        return Response.ok(wallApplicationTranslator.translate(application)).build()
    }

    override fun getDeviceJson(deviceId: UUID?): Response {
        val device = deviceController.findDeviceById(deviceId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        val deviceApiKey = device.apiKey

        if (!deviceApiKey.isNullOrEmpty()) {
            if (apiKey == null) {
                return createUnauthorized("Missing X-API-KEY header")
            }

            if (apiKey != deviceApiKey) {
                return createForbidden("Invalid API Key provided")
            }
        }

        return Response.ok(wallDeviceTranslator.translate(device)).build()
    }

    companion object {
        private const val INVALID_PARENT_ID = "Invalid parent_id"
    }
}