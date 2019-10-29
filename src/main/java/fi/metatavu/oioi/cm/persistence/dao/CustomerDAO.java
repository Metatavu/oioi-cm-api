package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for Customer
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class CustomerDAO extends AbstractDAO<Customer> {

  /**
   * Creates new Customer
   * 
   * @param id id
   * @param imageUrl imageUrl
   * @param name name
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created customer
   */
  public Customer create(UUID id, String imageUrl, String name, UUID creatorId, UUID lastModifierId) {
    Customer customer = new Customer();
    customer.setImageUrl(imageUrl);
    customer.setName(name);
    customer.setId(id);
    customer.setCreatorId(creatorId);
    customer.setLastModifierId(lastModifierId);
    return persist(customer);
  }

  /**
   * Updates imageUrl
   *
   * @param imageUrl imageUrl
   * @param lastModifierId last modifier's id
   * @return updated customer
   */
  public Customer updateImageUrl(Customer customer, String imageUrl, UUID lastModifierId) {
    customer.setLastModifierId(lastModifierId);
    customer.setImageUrl(imageUrl);
    return persist(customer);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated customer
   */
  public Customer updateName(Customer customer, String name, UUID lastModifierId) {
    customer.setLastModifierId(lastModifierId);
    customer.setName(name);
    return persist(customer);
  }

}
