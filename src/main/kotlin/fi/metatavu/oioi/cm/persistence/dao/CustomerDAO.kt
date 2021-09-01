package fi.metatavu.oioi.cm.persistence.dao

import java.util.UUID
import fi.metatavu.oioi.cm.persistence.model.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Customer
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class CustomerDAO : AbstractDAO<Customer>() {

    /**
     * Creates new Customer
     *
     * @param id id
     * @param imageUrl imageUrl
     * @param name name
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created customer
     */
    fun create(id: UUID?, imageUrl: String?, name: String?, creatorId: UUID?, lastModifierId: UUID?): Customer? {
        val customer = Customer()
        customer.imageUrl = imageUrl
        customer.name = name
        customer.id = id
        customer.creatorId = creatorId
        customer.lastModifierId = lastModifierId
        return persist(customer)
    }

    /**
     * Updates imageUrl
     *
     * @param imageUrl imageUrl
     * @param lastModifierId last modifier's id
     * @return updated customer
     */
    fun updateImageUrl(customer: Customer, imageUrl: String?, lastModifierId: UUID?): Customer? {
        customer.lastModifierId = lastModifierId
        customer.imageUrl = imageUrl
        return persist(customer)
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated customer
     */
    fun updateName(customer: Customer, name: String?, lastModifierId: UUID?): Customer? {
        customer.lastModifierId = lastModifierId
        customer.name = name
        return persist(customer)
    }

    /**
     * Lists customers by name in set of names
     *
     * @param names names to list customers by
     * @return list of customers
     */
    fun listCustomersByNameIn(names: Set<String?>?): List<Customer> {
        if (names == null || names.isEmpty()) {
            return emptyList()
        }
        val entityManager = entityManager
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(
            Customer::class.java
        )
        val root = criteria.from(
            Customer::class.java
        )
        criteria.select(root)
        criteria.where(root.get(Customer_.name).`in`(names))
        return entityManager.createQuery(criteria).resultList
    }
}