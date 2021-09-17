package fi.metatavu.oioi.cm.lock

import fi.metatavu.oioi.cm.persistence.dao.ResourceLockDao
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.persistence.model.ResourceLock
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for resource lock
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ResourceLockController {

    @Inject
    lateinit var resourceLockDao: ResourceLockDao

    /**
     * Creates resource lock
     *
     * @param application application
     * @param resource resource
     * @param userId user ID
     * @return created resource lock
     */
    fun createResourceLock(application: Application, resource: Resource, userId: UUID): ResourceLock {
        return resourceLockDao.create(
            id = UUID.randomUUID(),
            application = application,
            resource = resource,
            userId = userId
        )
    }

    /**
     * Lists active resource locks for application
     *
     * @param application application
     * @param resource filter by resource
     * @param notExpired filter by not expired
     * @return found resource locks
     */
    fun list(application: Application, resource: Resource?, notExpired: Boolean): List<ResourceLock> {
        return resourceLockDao.list(application = application, resource = resource, notExpired = notExpired)
    }

    /**
     * Lists all expired resource locks
     *
     * @return found resource locks
     */
    fun listExpired(): List<ResourceLock> {
        return resourceLockDao.listExpired()
    }

    /**
     * Finds resource lock for single resource
     *
     * @param resource resource
     * @return found resource or null
     */
    fun findResourceLockByResource(resource: Resource): ResourceLock? {
        return resourceLockDao.findLockByResource(resource)
    }

    /**
     * Updates resource lock
     *
     * @param resourceLock resource lock
     * @return updated resource lock
     */
    fun updateResourceLock(resourceLock: ResourceLock): ResourceLock {
        return resourceLockDao.updateExpiresAt(resourceLock, expiresAt = OffsetDateTime.now().plusMinutes(1))
    }

    /**
     * Deletes resource lock
     */
    fun deleteResourceLock(resourceLock: ResourceLock) {
        resourceLockDao.delete(resourceLock)
    }

    /**
     * Checks if lock is expired
     *
     * @param resourceLock resource lock to check
     * @return is lock expired or not
     */
    fun isExpired(resourceLock: ResourceLock): Boolean {
        resourceLock.expiresAt ?: return false
        return resourceLock.expiresAt!!.toInstant().isBefore(OffsetDateTime.now().toInstant())
    }
}