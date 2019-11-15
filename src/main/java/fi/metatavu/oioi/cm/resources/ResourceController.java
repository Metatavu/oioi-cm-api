package fi.metatavu.oioi.cm.resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.slf4j.Logger;

import fi.metatavu.oioi.cm.authz.ResourceScope;
import fi.metatavu.oioi.cm.model.KeyValueProperty;
import fi.metatavu.oioi.cm.model.ResourceType;
import fi.metatavu.oioi.cm.persistence.dao.ResourceDAO;
import fi.metatavu.oioi.cm.persistence.dao.ResourcePropertyDAO;
import fi.metatavu.oioi.cm.persistence.dao.ResourceStyleDAO;
import fi.metatavu.oioi.cm.persistence.model.Application;
import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty;
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle;

/**
 * Controller for Resource
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceController {

  @Inject
  private Logger logger;

  @Inject
  private ResourceDAO resourceDAO;
  
  @Inject
  private ResourcePropertyDAO resourcePropertyDAO;

  @Inject
  private ResourceStyleDAO resourceStyleDAO;
  
  /**
   * Create resource
   * 
   * @param authzClient authzClient
   * @param customer customer
   * @param device device
   * @param application application
   * @param parent parent
   * @param data data
   * @param name name
   * @param slug slug
   * @param type type
   * @param properties properties
   * @param styles styles
   * @param creatorId creator id
   * @return created resource
   */
  @SuppressWarnings ("squid:S00107")
  public Resource createResource(AuthzClient authzClient, fi.metatavu.oioi.cm.persistence.model.Customer customer, fi.metatavu.oioi.cm.persistence.model.Device device, fi.metatavu.oioi.cm.persistence.model.Application application, Resource parent, String data, String name, String slug, ResourceType type, List<KeyValueProperty> properties, List<KeyValueProperty> styles, UUID creatorId) {
    UUID resourceId = UUID.randomUUID();
    ResourceRepresentation keycloakResource = createProtectedResource(authzClient, customer.getId(), device.getId(), application.getId(), resourceId, creatorId);
    Resource resource = resourceDAO.create(resourceId, data, UUID.fromString(keycloakResource.getId()), name, parent, slug, type, creatorId, creatorId);
    setResourceProperties(resource, properties, creatorId);
    setResourceStyles(resource, styles, creatorId);
    return resource;
  }
  
  /**
   * Create resource
   * 
   * @param authzClient authzClient
   * @param customer customer
   * @param device device
   * @param applicationId application id
   * @param parent parent
   * @param data data
   * @param name name
   * @param slug slug
   * @param type type
   * @param creatorId creator id
   * @return created resource
   */
  @SuppressWarnings ("squid:S00107")
  public Resource createResource(AuthzClient authzClient, fi.metatavu.oioi.cm.persistence.model.Customer customer, fi.metatavu.oioi.cm.persistence.model.Device device, UUID applicationId, Resource parent, String data, String name, String slug, ResourceType type, UUID creatorId) {
    UUID resourceId = UUID.randomUUID();
    ResourceRepresentation keycloakResource = createProtectedResource(authzClient, customer.getId(), device.getId(), applicationId, resourceId, creatorId);
    return resourceDAO.create(resourceId, data, UUID.fromString(keycloakResource.getId()), name, parent, slug, type, creatorId, creatorId);
  }
  
  /**
   * Find resource by id
   * 
   * @param id resource id
   * @return found resource or null if not found
   */
  public Resource findResourceById(UUID id) {
    return resourceDAO.findById(id);
  }
  
  /**
   * Lists resources by parent
   * 
   * @param parent parent
   * @return resources
   */
  public List<Resource> listResourcesByParent(Resource parent) {
    return resourceDAO.listByParent(parent);
  }

  /**
   * Update resource
   *
   * @param resource resource
   * @param data data
   * @param name name
   * @param parent parent
   * @param slug slug
   * @param type type
   * @param lastModifierId last modifier id
   * @return updated resource
   */
  public Resource updateResource(Resource resource, String data, String name, Resource parent, String slug, ResourceType type, UUID lastModifierId) {
    resourceDAO.updateData(resource, data, lastModifierId);
    resourceDAO.updateName(resource, name, lastModifierId);
    resourceDAO.updateParent(resource, parent, lastModifierId);
    resourceDAO.updateSlug(resource, slug, lastModifierId);
    resourceDAO.updateType(resource, type, lastModifierId);
    return resource;
  }

  /**
   * Lists resource styles
   * 
   * @param resource resource
   * @return resource styles
   */
  public List<ResourceStyle> listStyles(Resource resource) {
    return resourceStyleDAO.listByResource(resource);
  }

  /**
   * Lists resource properties
   * 
   * @param resource resource
   * @return resource properties
   */
  public List<ResourceProperty> listProperties(Resource resource) {
    return resourcePropertyDAO.listByResource(resource);
  }
  
  /**
   * Deletes an resource
   * 
   * @param authzClient authzClient
   * @param resource resource to be deleted
   */
  public void delete(AuthzClient authzClient, Resource resource) {
    listResourcesByParent(resource).forEach(child -> delete(authzClient, child));

    listProperties(resource).forEach(this::deleteProperty);
    listStyles(resource).forEach(this::deleteStyle);
    
    UUID keycloakResorceId = resource.getKeycloakResorceId();
    
    try {
      authzClient.protection().resource().delete(keycloakResorceId.toString());
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(String.format("Failed to remove Keycloak resource %s ", resource.getKeycloakResorceId()), e);
      }
    }
    
    resourceDAO.delete(resource);
  }
    
  /**
   * Sets resource styles
   * 
   * @param resource resource
   * @param styles styles
   * @param lastModifierId modifier
   */
  public void setResourceStyles(Resource resource, List<KeyValueProperty> styles, UUID lastModifierId) {
    Map<String, ResourceStyle> existingStyles = new HashMap<>(listStyles(resource).stream().collect(Collectors.toMap(ResourceStyle::getKey, self -> self)));
    
    for (KeyValueProperty meta : styles) {
      ResourceStyle existingStyle = existingStyles.remove(meta.getKey());
      if (existingStyle == null) {
        createResourceStyle(resource, meta.getKey(), meta.getValue(), lastModifierId);
      } else {
        updateResourceStyle(existingStyle, meta.getKey(), meta.getValue(), lastModifierId);
      }
    }
    
    existingStyles.values().forEach(this::deleteStyle);
  }
    
  /**
   * Sets resource properties
   * 
   * @param resource resource
   * @param properties properties
   * @param lastModifierId modifier
   */
  public void setResourceProperties(Resource resource, List<KeyValueProperty> properties, UUID lastModifierId) {
    Map<String, ResourceProperty> existingProperties = new HashMap<>(listProperties(resource).stream().collect(Collectors.toMap(ResourceProperty::getKey, self -> self)));
    
    for (KeyValueProperty meta : properties) {
      ResourceProperty existingProperty = existingProperties.remove(meta.getKey());
      if (existingProperty == null) {
        createResourceProperty(resource, meta.getKey(), meta.getValue(), lastModifierId);
      } else {
        updateResourceProperty(existingProperty, meta.getKey(), meta.getValue(), lastModifierId);
      }
    }
    
    existingProperties.values().forEach(this::deleteProperty);
  }

  /**
   * Returns whether this resource belongs to given application
   * 
   * @param application application
   * @param resource resource
   * @return whether this resource belongs to given application
   */
  public boolean isApplicationResource(Application application, Resource resource) {
    if (resource == null) {
      return false;
    }
    
    if (application.getRootResource().getId().equals(resource.getId())) {
      return true;
    }
    
    if (resource.getParent() == null) {
      return false;
    }
    
    return isApplicationResource(application, resource.getParent());
  }
  
  /**
   * Create resource property
   *
   * @param resource resource
   * @param key key
   * @param value value
   * @param creatorId creator id
   * @return created resourceProperty
   */
  private ResourceProperty createResourceProperty(Resource resource, String key, String value, UUID creatorId) {
    return resourcePropertyDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId);
  }

  /**
   * Update resource property
   *
   * @param resourceProperty property
   * @param key key
   * @param value value
   * @param lastModifierId last modifier id
   * @return updated resourceProperty
   */
  private ResourceProperty updateResourceProperty(ResourceProperty resourceProperty, String key, String value, UUID lastModifierId) {
    resourcePropertyDAO.updateKey(resourceProperty, key, lastModifierId);
    resourcePropertyDAO.updateValue(resourceProperty, value, lastModifierId);
    return resourceProperty;
  }
  
  /**
   * Create resource style
   *
   * @param resource resource
   * @param key key
   * @param value value
   * @param creatorId creator id
   * @return created resourceStyle
   */
  private ResourceStyle createResourceStyle(Resource resource, String key, String value, UUID creatorId) {
    return resourceStyleDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId);
  }

  /**
   * Update resource style
   *
   * @param resourceStyle resource style
   * @param key key
   * @param value value
   * @param lastModifierId last modifier id
   * @return updated resourceStyle
   */
  private ResourceStyle updateResourceStyle(ResourceStyle resourceStyle, String key, String value, UUID lastModifierId) {
    resourceStyleDAO.updateKey(resourceStyle, key, lastModifierId);
    resourceStyleDAO.updateValue(resourceStyle, value, lastModifierId);
    return resourceStyle;
  }
  
  /**
   * Deletes a resource style
   * 
   * @param resourceStyle resource style
   */
  private void deleteStyle(ResourceStyle resourceStyle) {
    resourceStyleDAO.delete(resourceStyle);
  }
  
  /**
   * Deletes a resource property
   * 
   * @param resourceProperty property style
   */
  private void deleteProperty(ResourceProperty resourceProperty) {
    resourcePropertyDAO.delete(resourceProperty);
  }

  /**
   * Creates protected resource to Keycloak
   * 
   * @param authzClient authz client
   * @param customer customer
   * @param device device
   * @param application application
   * @param resource resource
   * @param userId userId
   * 
   * @return created resource
   */
  protected ResourceRepresentation createProtectedResource(AuthzClient authzClient, fi.metatavu.oioi.cm.persistence.model.Customer customer, fi.metatavu.oioi.cm.persistence.model.Device device, fi.metatavu.oioi.cm.persistence.model.Application application, fi.metatavu.oioi.cm.persistence.model.Resource resource, UUID userId) {
    return createProtectedResource(authzClient, customer.getId(), device.getId(), application.getId(), resource.getId(), userId);
  }
  
  /**
   * Creates protected resource to Keycloak
   * 
   * @param authzClient authz client
   * @param customerId customer id
   * @param deviceId device id
   * @param applicationId application id
   * @param resourceId resource id
   * @param userId userId
   * 
   * @return created resource
   */
  protected ResourceRepresentation createProtectedResource(AuthzClient authzClient, UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId, UUID userId) {
    Set<ScopeRepresentation> scopes = Arrays.stream(ResourceScope.values())
      .map(ResourceScope::getScope)
      .map(ScopeRepresentation::new)
      .collect(Collectors.toSet());
    
    String resourceUri = String.format("/v1/%s/devices/%s/applications/%s/resources/%s", customerId, deviceId, applicationId, resourceId);
    
    ResourceRepresentation keycloakResource = new ResourceRepresentation(resourceId.toString(), scopes, resourceUri, fi.metatavu.oioi.cm.authz.ResourceType.RESOURCE.getType());
    keycloakResource.setOwner(userId.toString());
    keycloakResource.setOwnerManagedAccess(true);

    ResourceRepresentation result = authzClient.protection().resource().create(keycloakResource);
    if (result != null) {
      return result;
    }
    
    List<ResourceRepresentation> resources = authzClient.protection().resource().findByUri(resourceUri);
    if (!resources.isEmpty()) {
      return resources.get(0);
    }
    
    return null;
  }
}