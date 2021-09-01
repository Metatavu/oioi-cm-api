package fi.metatavu.oioi.cm.persistence.model

import fi.metatavu.oioi.cm.model.MediaType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.validator.constraints.URL
import java.util.UUID
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * JPA entity representing media
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class Media {
    /**
     * id
     */
    @Id
    var id: UUID? = null

    /**
     * customer
     */
    @ManyToOne(optional = false)
    var customer: Customer? = null

    /**
     * URL
     */
    @NotNull
    @NotEmpty
    @URL
    var url: String? = null

    /**
     * type
     */
    @Column(nullable = false)
    var type: MediaType? = null

    /**
     * content type
     */
    @Column(nullable = false)
    @NotNull
    @NotEmpty
    var contentType: String? = null

    /**
     * creation time of the entity
     */
    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    /**
     * last modification time of the entity
     */
    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    /**
     * creator id
     */
    @Column(nullable = false)
    var creatorId: UUID? = null

    /**
     * last modifier id
     */
    @Column(nullable = false)
    var lastModifierId: UUID? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now()
        modifiedAt = OffsetDateTime.now()
    }

    /**
     * JPA pre-update event handler
     */
    @PreUpdate
    fun onUpdate() {
        modifiedAt = OffsetDateTime.now()
    }
}