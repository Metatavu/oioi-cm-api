package fi.metatavu.oioi.cm.customers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.dao.CustomerDAO;

/**
 * Controller for Customer
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class CustomerController {

  @Inject
  private CustomerDAO customerDAO;

 /**
   * Create customer
   *
   * @param imageUrl imageUrl
   * @param name name
   * @param creatorId creator id
   * @return created customer
   */
  public Customer createCustomer(Customer customer, String imageUrl, String name, UUID creatorId) {
    return customerDAO.create(UUID.randomUUID(), imageUrl, name, creatorId, creatorId);
  }

  /**
   * Update customer
   *
   * @param imageUrl imageUrl
   * @param name name
   * @param lastModifierId last modifier id
   * @return updated customer
   */
  public Customer updateCustomer(Customer customer, String imageUrl, String name, UUID lastModifierId) {
    customerDAO.updateImageUrl(customer, imageUrl, lastModifierId);
    customerDAO.updateName(customer, name, lastModifierId);
    return customer;
  }
}
