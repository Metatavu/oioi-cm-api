package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.KeyValueProperty
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle
import fi.metatavu.oioi.cm.resources.ResourceController
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for Resource REST entity
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ResourceTranslator: AbstractTranslator<Resource, fi.metatavu.oioi.cm.model.Resource>() {

    @Inject
    private lateinit var resourceController: ResourceController

    override fun translate(entity: Resource): fi.metatavu.oioi.cm.model.Resource {
        val result = fi.metatavu.oioi.cm.model.Resource()
        result.id = entity.id
        result.data = entity.data
        result.name = entity.name
        result.orderNumber = entity.orderNumber
        result.parentId = entity.parent?.id
        result.slug = entity.slug
        result.type = entity.type
        result.properties = getProperties(entity)
        result.styles = getStyles(entity)
        result.createdAt = entity.createdAt
        result.creatorId = entity.creatorId
        result.lastModifierId = entity.lastModifierId
        result.modifiedAt = entity.modifiedAt
        return result
    }

    /**
     * Translates styles to REST format
     *
     * @param entity resource
     * @return styles as REST key value pairs
     */
    private fun getStyles(entity: Resource): List<KeyValueProperty> {
        return resourceController.listStyles(entity).map { resourceStyle: ResourceStyle ->
            val result = KeyValueProperty()
            result.key = resourceStyle.key
            result.value = resourceStyle.value
            result
        }
    }

    /**
     * Translates properties to REST format
     *
     * @param entity resource
     * @return properties as REST key value pairs
     */
    private fun getProperties(entity: Resource): List<KeyValueProperty> {
        return resourceController.listProperties(entity).map { resourceProperty: ResourceProperty ->
            val result = KeyValueProperty()
            result.key = resourceProperty.key
            result.value = resourceProperty.value
            result
        }
    }

}
