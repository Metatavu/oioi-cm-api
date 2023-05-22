package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.WallDevicesApi
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
class WallDevicesApiImpl : AbstractApi(), WallDevicesApi {

    @Inject
    lateinit var deviceController: DeviceController

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var wallDeviceTranslator: WallDeviceTranslator

    override fun getDeviceJson(deviceId: UUID): Response {
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

}