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
 * DAO class for ResourceProperty
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourcePropertyDAO extends AbstractDAO<ResourceProperty> {

  /**
   * Creates new ResourceProperty
   * 
   * @param id id
   * @param key key
   * @param value value
   * @param resource resource
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created resourceProperty
   */
  public ResourceProperty create(UUID id, String key, String value, Resource resource, UUID creatorId, UUID lastModifierId) {
    ResourceProperty resourceProperty = new ResourceProperty();
    resourceProperty.setKey(key);
    resourceProperty.setValue(value);
    resourceProperty.setResource(resource);
    resourceProperty.setId(id);
    resourceProperty.setCreatorId(creatorId);
    resourceProperty.setLastModifierId(lastModifierId);
    return persist(resourceProperty);
  }

  /**
   * Updates key
   *
   * @param key key
   * @param lastModifierId last modifier's id
   * @return updated resourceProperty
   */
  public ResourceProperty updateKey(ResourceProperty resourceProperty, String key, UUID lastModifierId) {
    resourceProperty.setLastModifierId(lastModifierId);
    resourceProperty.setKey(key);
    return persist(resourceProperty);
  }

  /**
   * Updates value
   *
   * @param value value
   * @param lastModifierId last modifier's id
   * @return updated resourceProperty
   */
  public ResourceProperty updateValue(ResourceProperty resourceProperty, String value, UUID lastModifierId) {
    resourceProperty.setLastModifierId(lastModifierId);
    resourceProperty.setValue(value);
    return persist(resourceProperty);
  }

  /**
   * Updates resource
   *
   * @param resource resource
   * @param lastModifierId last modifier's id
   * @return updated resourceProperty
   */
  public ResourceProperty updateResource(ResourceProperty resourceProperty, Resource resource, UUID lastModifierId) {
    resourceProperty.setLastModifierId(lastModifierId);
    resourceProperty.setResource(resource);
    return persist(resourceProperty);
  }

  /**
   * Lists resource properties by resource
   * 
   * @param resource resource
   * @return List of resource properties
   */
  public List<ResourceProperty> listByResource(Resource resource) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ResourceProperty> criteria = criteriaBuilder.createQuery(ResourceProperty.class);
    Root<ResourceProperty> root = criteria.from(ResourceProperty.class);
   
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ResourceProperty_.resource), resource));
    
    return entityManager.createQuery(criteria).getResultList();
  }
}
