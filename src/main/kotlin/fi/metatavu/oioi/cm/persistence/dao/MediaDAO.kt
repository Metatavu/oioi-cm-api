package fi.metatavu.oioi.cm.persistence.dao

import fi.metatavu.oioi.cm.model.MediaType
import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import java.util.ArrayList
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for Media
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class MediaDAO : AbstractDAO<Media>() {

    /**
     * Creates new Media
     *
     * @param id id
     * @param customer customer
     * @param contentType contentType
     * @param type type
     * @param url url
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created media
     */
    fun create(
        id: UUID?,
        customer: Customer?,
        contentType: String?,
        type: MediaType?,
        url: String?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): Media? {
        val media = Media()
        media.customer = customer
        media.contentType = contentType
        media.type = type
        media.url = url
        media.id = id
        media.creatorId = creatorId
        media.lastModifierId = lastModifierId
        return persist(media)
    }

    /**
     * Lists medias
     *
     * @param customer filter by customer. Ignored if null
     * @param mediaType filter by media type. Ignored if null
     * @return List of medias
     */
    fun list(customer: Customer?, mediaType: MediaType?): List<Media> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            Media::class.java
        )
        val root = criteria.from(
            Media::class.java
        )
        val criterias: MutableList<Predicate> = ArrayList()
        if (customer != null) {
            criterias.add(criteriaBuilder.equal(root.get(Media_.customer), customer))
        }
        if (mediaType != null) {
            criterias.add(criteriaBuilder.equal(root.get(Media_.type), mediaType))
        }
        criteria.select(root)
        criteria.where(*criterias.toTypedArray())
        return entityManager.createQuery(criteria).resultList
    }

    /**
     * Updates contentType
     *
     * @param contentType contentType
     * @param lastModifierId last modifier's id
     * @return updated media
     */
    fun updateContentType(media: Media, contentType: String?, lastModifierId: UUID?): Media? {
        media.lastModifierId = lastModifierId
        media.contentType = contentType
        return persist(media)
    }

    /**
     * Updates type
     *
     * @param type type
     * @param lastModifierId last modifier's id
     * @return updated media
     */
    fun updateType(media: Media, type: MediaType?, lastModifierId: UUID?): Media? {
        media.lastModifierId = lastModifierId
        media.type = type
        return persist(media)
    }

    /**
     * Updates url
     *
     * @param url url
     * @param lastModifierId last modifier's id
     * @return updated media
     */
    fun updateUrl(media: Media, url: String?, lastModifierId: UUID?): Media? {
        media.lastModifierId = lastModifierId
        media.url = url
        return persist(media)
    }
}