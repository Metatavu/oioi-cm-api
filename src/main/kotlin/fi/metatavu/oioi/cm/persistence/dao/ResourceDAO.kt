package fi.metatavu.oioi.cm.persistence.dao

import fi.metatavu.oioi.cm.model.ResourceType
import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Resource
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class ResourceDAO : AbstractDAO<Resource>() {

    /**
     * Creates new Resource
     *
     * @param id id
     * @param orderNumber orderNumber
     * @param data data
     * @param keycloakResourceId keycloakResorceId
     * @param name name
     * @param parent parent
     * @param slug slug
     * @param type type
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created resource
     */
    fun create(
        id: UUID?,
        orderNumber: Int?,
        data: String?,
        keycloakResourceId: UUID?,
        name: String?,
        parent: Resource?,
        slug: String?,
        type: ResourceType?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): Resource {
        val resource = Resource()
        resource.data = data
        resource.keycloakResorceId = keycloakResourceId
        resource.name = name
        resource.orderNumber = orderNumber
        resource.parent = parent
        resource.slug = slug
        resource.type = type
        resource.id = id
        resource.creatorId = creatorId
        resource.lastModifierId = lastModifierId
        return persist(resource)
    }

    /**
     * Lists resources by parent
     *
     * @param parent parent
     * @return List of resources
     */
    fun listByParent(parent: Resource): List<Resource> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Resource::class.java)
        val root = criteria.from(Resource::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Resource_.parent), parent))
        criteria.orderBy(criteriaBuilder.asc(root.get(Resource_.orderNumber)))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Find resource by parent and slug
     *
     * @param parent parent
     * @param slug slug
     * @return resource or null if not found
     */
    fun findByParentAndSlug(parent: Resource, slug: String): Resource? {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Resource::class.java)
        val root = criteria.from(Resource::class.java)

        criteria.select(root)
        criteria.where(
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Resource_.parent), parent),
                criteriaBuilder.equal(root.get(Resource_.slug), slug)
            )
        )

        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Find resource by parent and name
     *
     * @param parent parent
     * @param name name
     * @return resource or null if not found
     */
    fun findByParentAndName(parent: Resource, name: String): Resource? {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Resource::class.java)
        val root = criteria.from(Resource::class.java)

        criteria.select(root)
        criteria.where(
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Resource_.parent), parent),
                criteriaBuilder.equal(root.get(Resource_.name), name)
            )
        )

        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Updates order number
     *
     * @param orderNumber order number
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateOrderNumber(resource: Resource, orderNumber: Int?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.orderNumber = orderNumber
        return persist(resource)
    }

    /**
     * Updates data
     *
     * @param data data
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateData(resource: Resource, data: String?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.data = data
        return persist(resource)
    }

    /**
     * Updates keycloakResourceId
     *
     * @param keycloakResourceId keycloakResourceId
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateKeycloakResourceId(resource: Resource, keycloakResourceId: UUID?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.keycloakResorceId = keycloakResourceId
        return persist(resource)
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateName(resource: Resource, name: String?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.name = name
        return persist(resource)
    }

    /**
     * Updates parent
     *
     * @param parent parent
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateParent(resource: Resource, parent: Resource?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.parent = parent
        return persist(resource)
    }

    /**
     * Updates slug
     *
     * @param slug slug
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateSlug(resource: Resource, slug: String?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.slug = slug
        return persist(resource)
    }

    /**
     * Updates type
     *
     * @param type type
     * @param lastModifierId last modifier's id
     * @return updated resource
     */
    fun updateType(resource: Resource, type: ResourceType?, lastModifierId: UUID?): Resource? {
        resource.lastModifierId = lastModifierId
        resource.type = type
        return persist(resource)
    }
}