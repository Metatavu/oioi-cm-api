package fi.metatavu.oioi.cm.persistence.dao

import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Application_
import fi.metatavu.oioi.cm.persistence.model.Device
import fi.metatavu.oioi.cm.persistence.model.Resource
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Application
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 */
@ApplicationScoped
class ApplicationDAO: AbstractDAO<Application>() {

    /**
     * Creates new Application
     *
     * @param id id
     * @param name name
     * @param rootResource rootResource
     * @param activeContentVersionResource active content version resource
     * @param device device
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created application
     */
    fun create(
        id: UUID,
        name: String,
        rootResource: Resource,
        activeContentVersionResource: Resource,
        device: Device?,
        creatorId: UUID?,
        lastModifierId: UUID?
    ): Application {
        val application = Application()
        application.name = name
        application.rootResource = rootResource
        application.device = device
        application.id = id
        application.activeContentVersionResource = activeContentVersionResource
        application.creatorId = creatorId
        application.lastModifierId = lastModifierId
        return persist(application)
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated application
     */
    fun updateName(application: Application, name: String?, lastModifierId: UUID?): Application {
        application.lastModifierId = lastModifierId
        application.name = name
        return persist(application)
    }

    /**
     * Updates active content version resource
     *
     * @param application application
     * @param activeContentVersionResource active content version resource
     * @param lastModifierId last modifier's id
     * @return updated application
     */
    fun updateActiveContentVersionResource(application: Application, activeContentVersionResource: Resource?, lastModifierId: UUID?): Application {
        application.lastModifierId = lastModifierId
        application.activeContentVersionResource = activeContentVersionResource
        return persist(application)
    }

    /**
     * Lists applications by device
     *
     * @param device device
     * @return List of applications
     */
    fun listByDevice(device: Device?): List<Application> {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Application::class.java)
        val root = criteria.from(Application::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Application_.device), device))
        return entityManager.createQuery(criteria).resultList
    }

    /**
     * Finds application by root resource
     *
     * @param rootResource root resource
     * @return application or null if not found
     */
    fun findByRootResource(rootResource: Resource): Application? {
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Application::class.java)
        val root = criteria.from(Application::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Application_.rootResource), rootResource))
        return getSingleResult(entityManager.createQuery(criteria))
    }

}