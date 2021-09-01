package fi.metatavu.oioi.cm.persistence.dao

import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for ResourceStyle
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class ResourceStyleDAO : AbstractDAO<ResourceStyle>() {
    /**
     * Creates new ResourceStyle
     *
     *  @param id id
     * @param key key
     * @param value value
     * @param resource resource
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created resourceStyle
     */
    fun create(
        id: UUID?,
        key: String?,
        value: String?,
        resource: Resource?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): ResourceStyle {
        val resourceStyle = ResourceStyle()
        resourceStyle.key = key
        resourceStyle.value = value
        resourceStyle.resource = resource
        resourceStyle.id = id
        resourceStyle.creatorId = creatorId
        resourceStyle.lastModifierId = lastModifierId
        return persist(resourceStyle)
    }

    /**
     * Updates key
     *
     * @param key key
     * @param lastModifierId last modifier's id
     * @return updated resourceStyle
     */
    fun updateKey(resourceStyle: ResourceStyle, key: String?, lastModifierId: UUID?): ResourceStyle? {
        resourceStyle.lastModifierId = lastModifierId
        resourceStyle.key = key
        return persist(resourceStyle)
    }

    /**
     * Updates value
     *
     * @param value value
     * @param lastModifierId last modifier's id
     * @return updated resourceStyle
     */
    fun updateValue(resourceStyle: ResourceStyle, value: String?, lastModifierId: UUID?): ResourceStyle? {
        resourceStyle.lastModifierId = lastModifierId
        resourceStyle.value = value
        return persist(resourceStyle)
    }

    /**
     * Updates resource
     *
     * @param resource resource
     * @param lastModifierId last modifier's id
     * @return updated resourceStyle
     */
    fun updateResource(resourceStyle: ResourceStyle, resource: Resource?, lastModifierId: UUID?): ResourceStyle? {
        resourceStyle.lastModifierId = lastModifierId
        resourceStyle.resource = resource
        return persist(resourceStyle)
    }

    /**
     * Lists resource styles by resource
     *
     * @param resource resource
     * @return List of resource styles
     */
    fun listByResource(resource: Resource?): List<ResourceStyle> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            ResourceStyle::class.java
        )
        val root = criteria.from(ResourceStyle::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(ResourceStyle_.resource), resource))
        return entityManager.createQuery(criteria).resultList
    }
}