package fi.metatavu.oioi.cm.persistence.dao

import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for DeviceMeta
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class DeviceMetaDAO : AbstractDAO<DeviceMeta>() {
    /**
     * Creates new DeviceMeta
     *
     *  @param id id
     * @param device device
     * @param key key
     * @param value value
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created deviceMeta
     */
    fun create(
        id: UUID?,
        device: Device?,
        key: String?,
        value: String?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): DeviceMeta? {
        val deviceMeta = DeviceMeta()
        deviceMeta.device = device
        deviceMeta.key = key
        deviceMeta.value = value
        deviceMeta.id = id
        deviceMeta.creatorId = creatorId
        deviceMeta.lastModifierId = lastModifierId
        return persist(deviceMeta)
    }

    /**
     * Updates device
     *
     * @param device device
     * @param lastModifierId last modifier's id
     * @return updated deviceMeta
     */
    fun updateDevice(deviceMeta: DeviceMeta, device: Device?, lastModifierId: UUID?): DeviceMeta? {
        deviceMeta.lastModifierId = lastModifierId
        deviceMeta.device = device
        return persist(deviceMeta)
    }

    /**
     * Updates key
     *
     * @param key key
     * @param lastModifierId last modifier's id
     * @return updated deviceMeta
     */
    fun updateKey(deviceMeta: DeviceMeta, key: String?, lastModifierId: UUID?): DeviceMeta? {
        deviceMeta.lastModifierId = lastModifierId
        deviceMeta.key = key
        return persist(deviceMeta)
    }

    /**
     * Updates value
     *
     * @param value value
     * @param lastModifierId last modifier's id
     * @return updated deviceMeta
     */
    fun updateValue(deviceMeta: DeviceMeta, value: String?, lastModifierId: UUID?): DeviceMeta? {
        deviceMeta.lastModifierId = lastModifierId
        deviceMeta.value = value
        return persist(deviceMeta)
    }

    /**
     * Lists device meta values by device
     *
     * @param device device
     * @return List of device meta values
     */
    fun listByDevice(device: Device?): List<DeviceMeta> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            DeviceMeta::class.java
        )
        val root = criteria.from(DeviceMeta::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(DeviceMeta_.device), device))
        return entityManager.createQuery(criteria).resultList
    }
}