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
            .mapNotNull(wallDeviceApplicationTranslator::translate)

        return WallDevice(
            modifiedAt = entity.modifiedAt!!,
            name = entity.name!!,
            applications = applications
        )
    }
}