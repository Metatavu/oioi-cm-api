package fi.metatavu.oioi.cm.persistence.model

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.UUID
import java.time.OffsetDateTime
import javax.persistence.*

/**
 * JPA entity representing resource lock
 *
 * @author Jari Nykänen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Table(
    uniqueConstraints = [UniqueConstraint(name = "UN_RESOURCE_LOCK_RESOURCE_ID", columnNames = ["resource_id"])]
)
class ResourceLock {

    /**
     * ID
     */
    @Id
    var id: UUID? = null

    /**
     * Application
     */
    @ManyToOne(optional = false)
    var application: Application? = null

    /**
     * Resource
     */
    @ManyToOne(optional = false)
    var resource: Resource? = null

    /**
     * User
     */
    @Column(nullable = false)
    var userId: UUID? = null

    /**
     * Expires at timestamp
     */
    @Column(nullable = false)
    var expiresAt: OffsetDateTime? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        expiresAt = OffsetDateTime.now().plusMinutes(1)
    }
}