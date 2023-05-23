package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.keycloak.KeycloakController
import fi.metatavu.oioi.cm.persistence.model.ResourceLock
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for resource lock REST entity
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ResourceLockTranslator: AbstractTranslator<ResourceLock, fi.metatavu.oioi.cm.model.ResourceLock>() {

    @Inject
    lateinit var keycloakController: KeycloakController
    
    override fun translate(entity: ResourceLock): fi.metatavu.oioi.cm.model.ResourceLock {
        return fi.metatavu.oioi.cm.model.ResourceLock(
            userId = entity.userId,
            expiresAt = entity.expiresAt,
            userDisplayName = keycloakController.getDisplayName(entity.userId!!)
        )
    }

    /**
     * Translates list of resource locks to resource ID list
     *
     * @param entities list of resource locks
     * @return list of resource IDs
     */
    fun translateLockedResourceIds(entities: List<ResourceLock>): List<UUID?> {
        return entities.map{ lock -> lock.resource?.id }
    }

}