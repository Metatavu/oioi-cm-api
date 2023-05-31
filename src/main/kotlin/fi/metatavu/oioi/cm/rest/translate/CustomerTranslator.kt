package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.persistence.model.Customer
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for Customer REST entity
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class CustomerTranslator : AbstractTranslator<Customer?, fi.metatavu.oioi.cm.model.Customer?>() {

    override fun translate(entity: Customer?): fi.metatavu.oioi.cm.model.Customer? {
        if (entity == null) {
            return null
        }

        return fi.metatavu.oioi.cm.model.Customer(
            id = entity.id,
            imageUrl = entity.imageUrl,
            name = entity.name!!,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }
}