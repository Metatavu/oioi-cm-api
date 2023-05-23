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
        return fi.metatavu.oioi.cm.model.Application(
            id = entity.id,
            name = entity.name!!,
            rootResourceId = entity.rootResource?.id,
            activeContentVersionResourceId = entity.activeContentVersionResource?.id,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId
        )
    }

}