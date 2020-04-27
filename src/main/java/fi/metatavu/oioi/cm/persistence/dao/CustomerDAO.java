package fi.metatavu.oioi.cm.persistence.dao;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.model.Customer_;

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

  /**
   * Lists customers by name in set of names
   * 
   * @param names names to list customers by
   * @return list of customers
   */
  public List<Customer> listCustomersByNameIn(Set<String> names) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Customer> criteria = criteriaBuilder.createQuery(Customer.class);
    Root<Customer> root = criteria.from(Customer.class);
    criteria.select(root);
    criteria.where(root.get(Customer_.name).in(names));
    return entityManager.createQuery(criteria).getResultList();
  }

}
