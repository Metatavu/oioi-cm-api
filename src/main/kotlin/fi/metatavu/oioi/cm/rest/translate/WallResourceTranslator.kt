package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.WallResource
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.resources.ResourceController
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for WallResource
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
class WallResourceTranslator : AbstractTranslator<Resource?, WallResource?>() {

    @Inject
    lateinit var resourceController: ResourceController

    override fun translate(entity: Resource?): WallResource? {
        return translate(entity = entity, applicationName = entity?.name)
    }

    /**
     * Translates wall resource
     *
     * @param entity resource entity
     * @param applicationName application name
     * @return translated wall resource
     */
    fun translate(entity: Resource?, applicationName: String?): WallResource? {
        entity ?: return null
        applicationName ?: return null

        val resourceStyles = resourceController.listStyles(entity)
        val resourceProperties = resourceController.listProperties(entity)

        val modifiedAt = resourceStyles.mapNotNull { it.modifiedAt }
            .plus(resourceProperties.mapNotNull{ it.modifiedAt })
            .plus(entity.modifiedAt!!)
            .maxOf { it }

        val result = WallResource()
        result.slug = entity.slug
        result.children = translate(resourceController.listResourcesByParent(parent = entity, resourceType = null))
        result.data = entity.data
        result.name = applicationName
        result.properties = resourceProperties.associate { it.key!! to it.value!! }
        result.styles = resourceStyles.associate { it.key!! to it.value!! }
        result.type = entity.type
        result.modifiedAt = modifiedAt
        return result
    }

}