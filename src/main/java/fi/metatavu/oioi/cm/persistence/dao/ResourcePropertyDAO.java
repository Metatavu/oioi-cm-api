package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
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

}
