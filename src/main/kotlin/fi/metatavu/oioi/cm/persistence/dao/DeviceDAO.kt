package fi.metatavu.oioi.cm.persistence.dao

import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Device
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class DeviceDAO : AbstractDAO<Device>() {

    /**
     * Creates new Device
     *
     * @param id id
     * @param customer customer
     * @param apiKey apiKey
     * @param name name
     * @param imageUrl image URL
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @param imageUrl
     * @return created device
     */
    fun create(
        id: UUID?,
        customer: Customer?,
        apiKey: String?,
        name: String?,
        imageUrl: String?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): Device? {
        val device = Device()
        device.customer = customer
        device.apiKey = apiKey
        device.name = name
        device.imageUrl = imageUrl
        device.id = id
        device.creatorId = creatorId
        device.lastModifierId = lastModifierId
        return persist(device)
    }

    /**
     * Lists devices by customer
     *
     * @param customer customer
     * @return List of devices
     */
    fun listByCustomer(customer: Customer?): List<Device> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            Device::class.java
        )
        val root = criteria.from(
            Device::class.java
        )
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Device_.customer), customer))
        return entityManager.createQuery(criteria).resultList
    }

    /**
     * Updates customer
     *
     * @param customer customer
     * @param lastModifierId last modifier's id
     * @return updated device
     */
    fun updateCustomer(device: Device, customer: Customer?, lastModifierId: UUID?): Device? {
        device.lastModifierId = lastModifierId
        device.customer = customer
        return persist(device)
    }

    /**
     * Updates apiKey
     *
     * @param apiKey apiKey
     * @param lastModifierId last modifier's id
     * @return updated device
     */
    fun updateApiKey(device: Device, apiKey: String?, lastModifierId: UUID?): Device? {
        device.lastModifierId = lastModifierId
        device.apiKey = apiKey
        return persist(device)
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated device
     */
    fun updateName(device: Device, name: String?, lastModifierId: UUID?): Device? {
        device.lastModifierId = lastModifierId
        device.name = name
        return persist(device)
    }

    /**
     * Updates image URL
     *
     * @param imageUrl image URL
     * @param lastModifierId last modifier's id
     * @return updated device
     */
    fun updateImageUrl(device: Device, imageUrl: String?, lastModifierId: UUID?): Device? {
        device.lastModifierId = lastModifierId
        device.imageUrl = imageUrl
        return persist(device)
    }
}