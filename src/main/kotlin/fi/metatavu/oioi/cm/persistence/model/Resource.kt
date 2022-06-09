package fi.metatavu.oioi.cm.persistence.model

import fi.metatavu.oioi.cm.model.ResourceType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.UUID
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * JPA entity representing reource
 *
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class Resource {
    /**
     * id
     */
    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var orderNumber: Int? = null

    @Column(nullable = false)
    var keycloakResorceId: UUID? = null

    /**
     * parent
     */
    @ManyToOne
    var parent: Resource? = null

    /**
     * type
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: ResourceType? = null

    /**
     * name
     */
    @Column(nullable = false)
    var name: @NotNull @NotEmpty String? = null

    @Column(nullable = false)
    @Lob
    var data: String? = null

    /**
     * slug
     */
    @Column(nullable = false)
    var slug: @NotNull @NotEmpty String? = null

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