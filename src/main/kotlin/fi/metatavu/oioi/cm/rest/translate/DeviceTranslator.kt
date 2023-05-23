package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.devices.DeviceController
import fi.metatavu.oioi.cm.model.KeyValueProperty
import fi.metatavu.oioi.cm.persistence.model.Device
import fi.metatavu.oioi.cm.persistence.model.DeviceMeta
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for Device REST entity
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class DeviceTranslator : AbstractTranslator<Device?, fi.metatavu.oioi.cm.model.Device?>() {

    @Inject
    lateinit var deviceController: DeviceController

    override fun translate(entity: Device?): fi.metatavu.oioi.cm.model.Device? {
        if (entity == null) {
            return null
        }

        return fi.metatavu.oioi.cm.model.Device(
            id = entity.id,
            apiKey = entity.apiKey,
            name = entity.name!!,
            metas = getMetas(entity),
            imageUrl = entity.imageUrl,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }

    /**
     * Translates meta values to REST format
     *
     * @param entity device
     * @return meta values as REST key value pairs
     */
    private fun getMetas(entity: Device): List<KeyValueProperty> {
        return deviceController.listMetas(entity).stream().map { resourceProperty: DeviceMeta ->
            KeyValueProperty(
                key = resourceProperty.key!!,
                value = resourceProperty.value!!
            )
        }.collect(Collectors.toList())
    }
}