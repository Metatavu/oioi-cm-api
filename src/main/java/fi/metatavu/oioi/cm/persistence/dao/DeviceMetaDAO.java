package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for DeviceMeta
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DeviceMetaDAO extends AbstractDAO<DeviceMeta> {

  /**
   * Creates new DeviceMeta
   * 
   * @param id id
   * @param device device
   * @param key key
   * @param value value
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created deviceMeta
   */
  public DeviceMeta create(UUID id, Device device, String key, String value, UUID creatorId, UUID lastModifierId) {
    DeviceMeta deviceMeta = new DeviceMeta();
    deviceMeta.setDevice(device);
    deviceMeta.setKey(key);
    deviceMeta.setValue(value);
    deviceMeta.setId(id);
    deviceMeta.setCreatorId(creatorId);
    deviceMeta.setLastModifierId(lastModifierId);
    return persist(deviceMeta);
  }

  /**
   * Updates device
   *
   * @param device device
   * @param lastModifierId last modifier's id
   * @return updated deviceMeta
   */
  public DeviceMeta updateDevice(DeviceMeta deviceMeta, Device device, UUID lastModifierId) {
    deviceMeta.setLastModifierId(lastModifierId);
    deviceMeta.setDevice(device);
    return persist(deviceMeta);
  }

  /**
   * Updates key
   *
   * @param key key
   * @param lastModifierId last modifier's id
   * @return updated deviceMeta
   */
  public DeviceMeta updateKey(DeviceMeta deviceMeta, String key, UUID lastModifierId) {
    deviceMeta.setLastModifierId(lastModifierId);
    deviceMeta.setKey(key);
    return persist(deviceMeta);
  }

  /**
   * Updates value
   *
   * @param value value
   * @param lastModifierId last modifier's id
   * @return updated deviceMeta
   */
  public DeviceMeta updateValue(DeviceMeta deviceMeta, String value, UUID lastModifierId) {
    deviceMeta.setLastModifierId(lastModifierId);
    deviceMeta.setValue(value);
    return persist(deviceMeta);
  }

  /**
   * Lists device meta values by device
   * 
   * @param device device
   * @return List of device meta values
   */
  public List<DeviceMeta> listByDevice(Device device) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DeviceMeta> criteria = criteriaBuilder.createQuery(DeviceMeta.class);
    Root<DeviceMeta> root = criteria.from(DeviceMeta.class);
   
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DeviceMeta_.device), device));
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
