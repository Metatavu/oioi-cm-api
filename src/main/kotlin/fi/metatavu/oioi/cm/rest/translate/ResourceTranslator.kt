package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.KeyValueProperty
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle
import fi.metatavu.oioi.cm.resources.ResourceController
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for Resource REST entity
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 */
@ApplicationScoped
class ResourceTranslator: AbstractTranslator<Resource, fi.metatavu.oioi.cm.model.Resource>() {

    @Inject
    private lateinit var resourceController: ResourceController

    override fun translate(entity: Resource): fi.metatavu.oioi.cm.model.Resource {
        val resourceStyles = resourceController.listStyles(entity)
        val resourceProperties = resourceController.listProperties(entity)

        val modifiedAt = resourceStyles.mapNotNull { it.modifiedAt }
            .plus(resourceProperties.mapNotNull{ it.modifiedAt })
            .plus(entity.modifiedAt!!)
            .maxOf { it }

        return fi.metatavu.oioi.cm.model.Resource(
            id = entity.id,
            data = entity.data,
            name = entity.name!!,
            orderNumber = entity.orderNumber,
            parentId = entity.parent?.id,
            slug = entity.slug!!,
            type = entity.type!!,
            properties = getProperties(resourceProperties),
            styles = getStyles(resourceStyles),
            createdAt = entity.createdAt,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId,
            modifiedAt = modifiedAt
        )
    }

    /**
     * Translates styles to REST format
     *
     * @param resourceStyles resource styles
     * @return styles as REST key value pairs
     */
    private fun getStyles(resourceStyles: List<ResourceStyle>): List<KeyValueProperty> {
        return resourceStyles.map { resourceStyle: ResourceStyle ->
            KeyValueProperty(
                key = resourceStyle.key!!,
                value = resourceStyle.value!!
            )
        }
    }

    /**
     * Translates properties to REST format
     *
     * @param resourceProperties resource properties
     * @return properties as REST key value pairs
     */
    private fun getProperties(resourceProperties: List<ResourceProperty>): List<KeyValueProperty> {
        return resourceProperties.map { resourceProperty: ResourceProperty ->
            KeyValueProperty(
                key = resourceProperty.key!!,
                value = resourceProperty.value!!
            )
        }
    }

}
