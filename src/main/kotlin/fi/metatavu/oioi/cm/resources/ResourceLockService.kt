package fi.metatavu.oioi.cm.resources

import fi.metatavu.oioi.cm.lock.ResourceLockController
import io.quarkus.scheduler.Scheduled
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

/**
 * Service for deleting expired resource locks
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 */
@ApplicationScoped
@Suppress ("UNUSED")
class ResourceLockService {

    @Inject
    lateinit var resourceLockController: ResourceLockController

    /**
     * Clear the database and request and analyze new orders data
     */
    @Scheduled(every = "1m")
    @Transactional
    fun clearResourceLocks() {
        resourceLockController.deleteExpired()
    }
}