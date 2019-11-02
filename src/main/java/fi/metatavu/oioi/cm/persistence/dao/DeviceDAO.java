package fi.metatavu.oioi.cm.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.model.Device;
import fi.metatavu.oioi.cm.persistence.model.Device_;

/**
 * DAO class for Device
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DeviceDAO extends AbstractDAO<Device> {

  /**
   * Creates new Device
   * 
   * @param id id
   * @param customer customer
   * @param apiKey apiKey
   * @param name name
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created device
   */
  public Device create(UUID id, Customer customer, String apiKey, String name, UUID creatorId, UUID lastModifierId) {
    Device device = new Device();
    device.setCustomer(customer);
    device.setApiKey(apiKey);
    device.setName(name);
    device.setId(id);
    device.setCreatorId(creatorId);
    device.setLastModifierId(lastModifierId);
    return persist(device);
  }

  /**
   * Lists devices by customer
   * 
   * @param customer customer
   * @return List of devices
   */
  public List<Device> listByCustomer(Customer customer) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Device> criteria = criteriaBuilder.createQuery(Device.class);
    Root<Device> root = criteria.from(Device.class);
   
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Device_.customer), customer));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Updates customer
   *
   * @param customer customer
   * @param lastModifierId last modifier's id
   * @return updated device
   */
  public Device updateCustomer(Device device, Customer customer, UUID lastModifierId) {
    device.setLastModifierId(lastModifierId);
    device.setCustomer(customer);
    return persist(device);
  }

  /**
   * Updates apiKey
   *
   * @param apiKey apiKey
   * @param lastModifierId last modifier's id
   * @return updated device
   */
  public Device updateApiKey(Device device, String apiKey, UUID lastModifierId) {
    device.setLastModifierId(lastModifierId);
    device.setApiKey(apiKey);
    return persist(device);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated device
   */
  public Device updateName(Device device, String name, UUID lastModifierId) {
    device.setLastModifierId(lastModifierId);
    device.setName(name);
    return persist(device);
  }

}
