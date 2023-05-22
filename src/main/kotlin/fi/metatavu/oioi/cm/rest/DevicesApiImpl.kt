package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.DevicesApi
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
class DevicesApiImpl : AbstractApi(), DevicesApi {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var deviceController: DeviceController

    @Inject
    lateinit var deviceTranslator: DeviceTranslator

    @Inject
    lateinit var resourceController: ResourceController

    override fun createDevice(customerId: UUID, device: @Valid Device): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        val apiKey = device.apiKey
        val name = device.name
        val imageUrl = device.imageUrl
        val createdDevice = deviceController.createDevice(customer, apiKey, name, imageUrl, loggedUserId)
        deviceController.setDeviceMetas(createdDevice, device.metas, loggedUserId)
        return createOk(deviceTranslator.translate(createdDevice))
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

    override fun updateDevice(customerId: UUID, deviceId: UUID, device: @Valid Device): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        val foundDevice = deviceController.findDeviceById(deviceId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (foundDevice.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }
        val apiKey = device.apiKey
        val name = device.name
        val imageUrl = device.imageUrl

        deviceController.updateDevice(foundDevice, customer, apiKey, name, imageUrl, loggedUserId)
        deviceController.setDeviceMetas(foundDevice, device.metas, loggedUserId)

        return createOk(deviceTranslator.translate(foundDevice))
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

}