package fi.metatavu.oioi.cm.persistence.dao

import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for ResourceProperty
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class ResourcePropertyDAO : AbstractDAO<ResourceProperty>() {

    /**
     * Creates new ResourceProperty
     *
     *  @param id id
     * @param key key
     * @param value value
     * @param resource resource
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created resourceProperty
     */
    fun create(
        id: UUID?,
        key: String?,
        value: String?,
        resource: Resource?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): ResourceProperty {
        val resourceProperty = ResourceProperty()
        resourceProperty.key = key
        resourceProperty.value = value
        resourceProperty.resource = resource
        resourceProperty.id = id
        resourceProperty.creatorId = creatorId
        resourceProperty.lastModifierId = lastModifierId
        return persist(resourceProperty)
    }

    /**
     * Updates key
     *
     * @param key key
     * @param lastModifierId last modifier's id
     * @return updated resourceProperty
     */
    fun updateKey(resourceProperty: ResourceProperty, key: String?, lastModifierId: UUID?): ResourceProperty? {
        resourceProperty.lastModifierId = lastModifierId
        resourceProperty.key = key
        return persist(resourceProperty)
    }

    /**
     * Updates value
     *
     * @param value value
     * @param lastModifierId last modifier's id
     * @return updated resourceProperty
     */
    fun updateValue(resourceProperty: ResourceProperty, value: String?, lastModifierId: UUID?): ResourceProperty? {
        resourceProperty.lastModifierId = lastModifierId
        resourceProperty.value = value
        return persist(resourceProperty)
    }

    /**
     * Updates resource
     *
     * @param resource resource
     * @param lastModifierId last modifier's id
     * @return updated resourceProperty
     */
    fun updateResource(
        resourceProperty: ResourceProperty,
        resource: Resource?,
        lastModifierId: UUID?
    ): ResourceProperty? {
        resourceProperty.lastModifierId = lastModifierId
        resourceProperty.resource = resource
        return persist(resourceProperty)
    }

    /**
     * Lists resource properties by resource
     *
     * @param resource resource
     * @return List of resource properties
     */
    fun listByResource(resource: Resource?): List<ResourceProperty> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            ResourceProperty::class.java
        )
        val root = criteria.from(
            ResourceProperty::class.java
        )
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(ResourceProperty_.resource), resource))
        return entityManager.createQuery(criteria).resultList
    }
}