package fi.metatavu.oioi.cm.lock

import fi.metatavu.oioi.cm.persistence.dao.ResourceLockDao
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Resource
import fi.metatavu.oioi.cm.persistence.model.ResourceLock
import fi.metatavu.oioi.cm.resources.ResourceController
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

    @Inject
    lateinit var resourceController: ResourceController

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
     * Deletes all expired resource locks
     */
    fun deleteExpired() {
        return resourceLockDao.deleteExpired()
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

    /**
     * Checks if resource is locked for another user
     *
     * @param resource resource to check
     * @param loggedUserId current logged user ID
     * @return false if no lock is found or if found non-expired lock belongs to current user, otherwise true
     */
    fun isResourceLockedForAnotherUser(resource: Resource, loggedUserId: UUID): Boolean {
        val resourceLock = findResourceLockByResource(resource) ?: return false
        return resourceLock.userId != loggedUserId
    }

    /**
     * Checks if user is allowed to delete given resource
     *
     * @param resource resource to check
     * @param loggedUserId currently logged in user
     * @param application application
     * @return true if none of the child elements are not locked for other users, otherwise false
     */
    fun isUserAllowedToDeleteResource(resource: Resource, loggedUserId: UUID, application: Application): Boolean {
        val lockedResourcesForOtherUsers = list(application = application, resource = null, notExpired = false)
            .filter { it.userId != loggedUserId }
            .map{ lock -> lock.resource!! }

        if (lockedResourcesForOtherUsers.isNullOrEmpty()) {
            return true
        }

        val childResources = resourceController.listResourcesByParent(parent = resource, resourceType = null)
        return checkChildResourceLocks(childResources = childResources, lockedResources = lockedResourcesForOtherUsers)
    }

    /**
     * Recursive function that checks child resource locks
     *
     * @param childResources list of child resources
     * @param lockedResources all locked resources for given application
     * @return true if none of the child elements are not locked for other users, otherwise false
     */
    private fun checkChildResourceLocks(childResources: List<Resource>, lockedResources: List<Resource>): Boolean {
        for (resource in childResources) {
            if (lockedResources.contains(resource)) {
                return false
            }

            val list = resourceController.listResourcesByParent(parent = resource, resourceType = null)
            if (list.isNotEmpty()) {
                return checkChildResourceLocks(childResources = list, lockedResources = lockedResources)
            }
        }

        return true
    }
}