package fi.metatavu.oioi.cm.persistence.model

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.UUID
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * JPA entity representing device
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class Device {

    /**
     * id
     */
    @Id
    var id: UUID? = null

    /**
     * device customer
     */
    @ManyToOne(optional = false)
    var customer: Customer? = null

    /**
     * name
     */
    @NotNull
    @NotEmpty
    var name: String? = null

    /**
     * API key
     */
    var apiKey: String? = null

    /**
     * an image URL
     */
    var imageUrl: String? = null

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