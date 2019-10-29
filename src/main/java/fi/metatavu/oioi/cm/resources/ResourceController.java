package fi.metatavu.oioi.cm.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.model.ResourceType;
import fi.metatavu.oioi.cm.persistence.dao.ResourceDAO;
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty;
import fi.metatavu.oioi.cm.persistence.dao.ResourcePropertyDAO;
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle;
import fi.metatavu.oioi.cm.persistence.dao.ResourceStyleDAO;

/**
 * Controller for Resource
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceController {

  @Inject
  private ResourceDAO resourceDAO;
  
  @Inject
  private ResourcePropertyDAO resourcePropertyDAO;

  @Inject
  private ResourceStyleDAO resourceStyleDAO;
  
 /**
   * Create resource
   *
   * @param data data
   * @param keycloakResorceId keycloakResorceId
   * @param name name
   * @param parent parent
   * @param slug slug
   * @param type type
   * @param creatorId creator id
   * @return created resource
   */
  public Resource createResource(Resource resource, String data, UUID keycloakResorceId, String name, Resource parent, String slug, ResourceType type, UUID creatorId) {
    return resourceDAO.create(UUID.randomUUID(), data, keycloakResorceId, name, parent, slug, type, creatorId, creatorId);
  }

  /**
   * Update resource
   *
   * @param data data
   * @param keycloakResorceId keycloakResorceId
   * @param name name
   * @param parent parent
   * @param slug slug
   * @param type type
   * @param lastModifierId last modifier id
   * @return updated resource
   */
  public Resource updateResource(Resource resource, String data, UUID keycloakResorceId, String name, Resource parent, String slug, ResourceType type, UUID lastModifierId) {
    resourceDAO.updateData(resource, data, lastModifierId);
    resourceDAO.updateKeycloakResorceId(resource, keycloakResorceId, lastModifierId);
    resourceDAO.updateName(resource, name, lastModifierId);
    resourceDAO.updateParent(resource, parent, lastModifierId);
    resourceDAO.updateSlug(resource, slug, lastModifierId);
    resourceDAO.updateType(resource, type, lastModifierId);
    return resource;
  }
  
  /**
   * Create resourceProperty
   *
   * @param key key
   * @param value value
   * @param resource resource
   * @param creatorId creator id
   * @return created resourceProperty
   */
  public ResourceProperty createResourceProperty(ResourceProperty resourceProperty, String key, String value, Resource resource, UUID creatorId) {
    return resourcePropertyDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId);
  }

  /**
   * Update resourceProperty
   *
   * @param key key
   * @param value value
   * @param resource resource
   * @param lastModifierId last modifier id
   * @return updated resourceProperty
   */
  public ResourceProperty updateResourceProperty(ResourceProperty resourceProperty, String key, String value, Resource resource, UUID lastModifierId) {
    resourcePropertyDAO.updateKey(resourceProperty, key, lastModifierId);
    resourcePropertyDAO.updateValue(resourceProperty, value, lastModifierId);
    resourcePropertyDAO.updateResource(resourceProperty, resource, lastModifierId);
    return resourceProperty;
  }
  
  /**
   * Create resourceStyle
   *
   * @param key key
   * @param value value
   * @param resource resource
   * @param creatorId creator id
   * @return created resourceStyle
   */
  public ResourceStyle createResourceStyle(ResourceStyle resourceStyle, String key, String value, Resource resource, UUID creatorId) {
    return resourceStyleDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId);
  }

  /**
   * Update resourceStyle
   *
   * @param key key
   * @param value value
   * @param resource resource
   * @param lastModifierId last modifier id
   * @return updated resourceStyle
   */
  public ResourceStyle updateResourceStyle(ResourceStyle resourceStyle, String key, String value, Resource resource, UUID lastModifierId) {
    resourceStyleDAO.updateKey(resourceStyle, key, lastModifierId);
    resourceStyleDAO.updateValue(resourceStyle, value, lastModifierId);
    resourceStyleDAO.updateResource(resourceStyle, resource, lastModifierId);
    return resourceStyle;
  }
  
}