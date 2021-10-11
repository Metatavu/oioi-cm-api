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
class ResourceLockDAO: AbstractDAO<ResourceLock>() {

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
     * @param notExpired filter by not expired
     * @return list of resource locks
     */
    fun list(application: Application, resource: Resource?, notExpired: Boolean): List<ResourceLock> {
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

        if (notExpired) {
            restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ResourceLock_.expiresAt), OffsetDateTime.now()))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Deletes expired resource locks
     */
    fun deleteExpired() {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createCriteriaDelete(ResourceLock::class.java)
        val root = criteria.from(ResourceLock::class.java)

        criteria.where(criteriaBuilder.lessThan(root.get(ResourceLock_.expiresAt), OffsetDateTime.now()))
        entityManager.createQuery(criteria).executeUpdate()

        flush()
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
        restrictions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ResourceLock_.expiresAt), OffsetDateTime.now()))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Updates resource lock expires at
     *
     * @param resourceLock resource lock
     * @param expiresAt expires at
     * @return updated resource lock
     */
    fun updateExpiresAt(resourceLock: ResourceLock, expiresAt: OffsetDateTime): ResourceLock {
        resourceLock.expiresAt = expiresAt
        return persist(resourceLock)
    }

}