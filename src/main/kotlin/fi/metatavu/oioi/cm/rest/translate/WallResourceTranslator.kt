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
        entity ?: return null

        val result = WallResource()
        result.slug = entity.slug
        result.children = translate(resourceController.listResourcesByParent(entity))
        result.data = entity.data
        result.name = entity.name
        result.properties = getProperties(entity)
        result.styles = getStyles(entity)
        result.type = entity.type
        result.modifiedAt = entity.modifiedAt
        return result
    }

    /**
     * Translates styles
     *
     * @param entity resource
     * @return styles as key value pairs
     */
    private fun getStyles(entity: Resource): Map<String, String> {
        return resourceController.listStyles(entity).associate { it.key to it.value }
    }

    /**
     * Translates styles
     *
     * @param entity resource
     * @return styles as key value pairs
     */
    private fun getProperties(entity: Resource): Map<String, String> {
        return resourceController.listProperties(entity).associate { it.key to it.value }
    }
}