package fi.metatavu.oioi.cm.persistence.dao

import fi.metatavu.oioi.cm.persistence.model.*
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for resource locks
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ResourceLockDao: AbstractDAO<ResourceLock>() {

    /**
     * Creates resource lock
     *
     * @param id ID
     * @param application application
     * @param resource resource
     * @param userId user ID
     * @return created resource lock
     */
    fun create(
        id: UUID,
        application: Application,
        resource: Resource,
        userId: UUID
    ): ResourceLock {
        val resourceLock = ResourceLock()
        resourceLock.id = id
        resourceLock.application = application
        resourceLock.resource = resource
        resourceLock.userId = userId
        return persist(resourceLock)
    }

    /**
     * List resource locks
     *
     * @param application filter by application
     * @param resource filter by resource
     * @return list of resource locks
     */
    fun list(application: Application, resource: Resource?): List<ResourceLock> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(ResourceLock::class.java)
        val root = criteria.from(ResourceLock::class.java)
        criteria.select(root)

        val restrictions = ArrayList<Predicate>()
        restrictions.add(criteriaBuilder.equal(root.get(ResourceLock_.application), application))

        if (resource != null) {
            restrictions.add(criteriaBuilder.equal(root.get(ResourceLock_.resource), resource))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Find lock by resource
     *
     * @param resource resource
     */
    fun findLockByResource(resource: Resource): ResourceLock? {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(ResourceLock::class.java)
        val root = criteria.from(ResourceLock::class.java)
        criteria.select(root)

        val restrictions = ArrayList<Predicate>()
        restrictions.add(criteriaBuilder.equal(root.get(ResourceLock_.resource), resource))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Updates resource lock
     *
     * @param resourceLock resource lock
     * @return updated resource lock
     */
    fun updateResourceLock(resourceLock: ResourceLock): ResourceLock {
        resourceLock.expiresAt = OffsetDateTime.now().plusMinutes(5)
        return persist(resourceLock)
    }

}