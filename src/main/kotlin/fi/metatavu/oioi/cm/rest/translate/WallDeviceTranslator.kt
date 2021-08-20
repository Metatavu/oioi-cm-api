package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.applications.ApplicationController
import fi.metatavu.oioi.cm.model.WallDevice
import fi.metatavu.oioi.cm.persistence.model.Device
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for WallDeviceApplication
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen <heikki.kurhinen></heikki.kurhinen>@metatavu.fi>
 */
@ApplicationScoped
class WallDeviceTranslator : AbstractTranslator<Device?, WallDevice?>() {

    @Inject
    lateinit var wallDeviceApplicationTranslator: WallDeviceApplicationTranslator

    @Inject
    lateinit var applicationController: ApplicationController

    override fun translate(entity: Device?): WallDevice? {
        entity ?: return null

        val applications = applicationController.listDeviceApplications(entity)
            .map(wallDeviceApplicationTranslator::translate)

        val result = WallDevice()
        result.modifiedAt = entity.modifiedAt
        result.name = entity.name
        result.applications = applications
        return result
    }
}