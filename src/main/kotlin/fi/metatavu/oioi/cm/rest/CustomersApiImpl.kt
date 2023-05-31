package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.CustomersApi
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

/**
 * REST - endpoints for customers
 *
 * @author Antti Leppä
 */
@RequestScoped
@Transactional
@Consumes("application/json;charset=utf-8")
@Produces("application/json;charset=utf-8")
class CustomersApiImpl : AbstractApi(), CustomersApi {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var customerTranslator: CustomerTranslator

    @Inject
    lateinit var resourceController: ResourceController

    override fun createCustomer(customer: Customer): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val result = customerController.createCustomer(customer.imageUrl, customer.name, loggedUserId)

        return createOk(customerTranslator.translate(result))
    }

    override fun listCustomers(): Response {
        return createOk(customerController
            .listAllCustomers().filter { isAdminOrHasCustomerGroup(it.name) }
            .map (customerTranslator::translate)
        )
    }

    override fun findCustomer(customerId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else {
            createOk(customerTranslator.translate(customer))
        }
    }

    override fun updateCustomer(customerId: UUID, customer: Customer): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val loggedUserId = loggedUserId!!
        val foundCustomer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        customerController.updateCustomer(foundCustomer, customer.imageUrl, customer.name, loggedUserId)
        return createOk(customerTranslator.translate(foundCustomer))
    }

    override fun deleteCustomer(customerId: UUID): Response {
        if (!hasRealmRole(ADMIN_ROLE)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        customerController.deleteCustomer(authzClient, customer)
        return createNoContent()
    }

}