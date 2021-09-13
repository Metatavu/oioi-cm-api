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
        val result = fi.metatavu.oioi.cm.model.ResourceLock()
        result.userId = entity.userId
        result.expiresAt = entity.expiresAt
        result.userDisplayName = keycloakController.getDisplayName(entity.userId!!)
        return result
    }

    /**
     * Translates list of resource locks to resource ID list
     *
     * @param entities list of resource locks
     * @return list of resource IDs
     */
    fun translateLockedResourceIds(entities: List<ResourceLock>): List<UUID> {
        return entities.map(ResourceLock::id)
    }

}