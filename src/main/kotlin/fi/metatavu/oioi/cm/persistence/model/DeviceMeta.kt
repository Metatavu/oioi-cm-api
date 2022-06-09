package fi.metatavu.oioi.cm.persistence.model

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.UUID
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * JPA entity representing device meta
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class DeviceMeta {
    /**
     * id
     */
    @Id
    var id: UUID? = null

    /**
     * a key
     */
    @Column(name = "metakey")
    @NotNull
    @NotEmpty
    var key: String? = null

    /**
     * value
     */
    @Lob
    @NotNull
    @NotEmpty
    var value: String? = null

    /**
     * device
     */
    @ManyToOne(optional = false)
    var device: Device? = null

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