package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.WallDeviceApplication
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.resources.ResourceController
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for wall device application
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen <heikki.kurhinen></heikki.kurhinen>@metatavu.fi>
 */
@ApplicationScoped
class WallDeviceApplicationTranslator : AbstractTranslator<Application?, WallDeviceApplication?>() {

    @Inject
    lateinit var resourceController: ResourceController

    override fun translate(entity: Application?): WallDeviceApplication? {
        entity ?: return null

        val rootResource = entity.rootResource
        var modifiedAt = entity.modifiedAt
        var styles: Map<String, String>? = null
        var properties: Map<String, String>? = null

        if (rootResource != null) {
            val resourceModifiedAt = rootResource.modifiedAt!!
            if (resourceModifiedAt.isAfter(modifiedAt)) {
                modifiedAt = resourceModifiedAt
            }

            properties = getProperties(rootResource)
            styles = getStyles(rootResource)
        }

        return WallDeviceApplication(
            id = entity.id!!,
            name = entity.name!!,
            modifiedAt = modifiedAt!!,
            styles = styles ?: emptyMap(),
            properties = properties ?: emptyMap(),
        )
    }

    /**
     * Translates styles
     *
     * @param entity resource
     * @return styles as key value pairs
     */
    private fun getStyles(entity: Resource): Map<String, String> {
        return resourceController.listStyles(entity).associate { it.key!! to it.value!! }
    }

    /**
     * Translates styles
     *
     * @param entity resource
     * @return styles as key value pairs
     */
    private fun getProperties(entity: Resource): Map<String, String> {
        return resourceController.listProperties(entity).associate { it.key!! to it.value!! }
    }
}