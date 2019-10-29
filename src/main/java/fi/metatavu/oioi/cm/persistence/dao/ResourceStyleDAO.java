package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for ResourceStyle
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceStyleDAO extends AbstractDAO<ResourceStyle> {

  /**
   * Creates new ResourceStyle
   * 
   * @param id id
   * @param key key
   * @param value value
   * @param resource resource
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created resourceStyle
   */
  public ResourceStyle create(UUID id, String key, String value, Resource resource, UUID creatorId, UUID lastModifierId) {
    ResourceStyle resourceStyle = new ResourceStyle();
    resourceStyle.setKey(key);
    resourceStyle.setValue(value);
    resourceStyle.setResource(resource);
    resourceStyle.setId(id);
    resourceStyle.setCreatorId(creatorId);
    resourceStyle.setLastModifierId(lastModifierId);
    return persist(resourceStyle);
  }

  /**
   * Updates key
   *
   * @param key key
   * @param lastModifierId last modifier's id
   * @return updated resourceStyle
   */
  public ResourceStyle updateKey(ResourceStyle resourceStyle, String key, UUID lastModifierId) {
    resourceStyle.setLastModifierId(lastModifierId);
    resourceStyle.setKey(key);
    return persist(resourceStyle);
  }

  /**
   * Updates value
   *
   * @param value value
   * @param lastModifierId last modifier's id
   * @return updated resourceStyle
   */
  public ResourceStyle updateValue(ResourceStyle resourceStyle, String value, UUID lastModifierId) {
    resourceStyle.setLastModifierId(lastModifierId);
    resourceStyle.setValue(value);
    return persist(resourceStyle);
  }

  /**
   * Updates resource
   *
   * @param resource resource
   * @param lastModifierId last modifier's id
   * @return updated resourceStyle
   */
  public ResourceStyle updateResource(ResourceStyle resourceStyle, Resource resource, UUID lastModifierId) {
    resourceStyle.setLastModifierId(lastModifierId);
    resourceStyle.setResource(resource);
    return persist(resourceStyle);
  }

}
