package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.persistence.model.Media
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for Media REST entity
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class MediaTranslator : AbstractTranslator<Media?, fi.metatavu.oioi.cm.model.Media?>() {
    override fun translate(entity: Media?): fi.metatavu.oioi.cm.model.Media? {
        if (entity == null) {
            return null
        }

        return fi.metatavu.oioi.cm.model.Media(
            id = entity.id,
            contentType = entity.contentType!!,
            type = entity.type!!,
            url = entity.url!!,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }
}