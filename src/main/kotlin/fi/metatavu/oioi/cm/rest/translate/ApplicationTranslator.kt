package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.persistence.model.Application
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for Application REST entity
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ApplicationTranslator: AbstractTranslator<Application, fi.metatavu.oioi.cm.model.Application>() {
    
    override fun translate(entity: Application): fi.metatavu.oioi.cm.model.Application {
        val result = fi.metatavu.oioi.cm.model.Application()
        result.id = entity.id
        result.name = entity.name
        result.rootResourceId = entity.rootResource?.id
        result.activeContentVersionResourceId = entity.activeContentVersionResource?.id
        result.createdAt = entity.createdAt
        result.modifiedAt = entity.modifiedAt
        result.creatorId = entity.creatorId
        result.lastModifierId = entity.lastModifierId
        return result
    }

}