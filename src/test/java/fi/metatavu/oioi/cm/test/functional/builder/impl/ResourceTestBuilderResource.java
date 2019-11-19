package fi.metatavu.oioi.cm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.openapitools.client.api.ResourcesApi;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.Resource;
import org.openapitools.client.model.ResourceType;
import org.openapitools.client.model.KeyValueProperty;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.client.ApiException;
import fi.metatavu.oioi.cm.test.functional.builder.AbstractApiTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Test builder resource for resources
 * 
 * @author Antti Leppä
 */
public class ResourceTestBuilderResource extends AbstractApiTestBuilderResource<Resource, ResourcesApi> {

  private Map<UUID, UUID> customerResourceIds = new HashMap<>();
  private Map<UUID, UUID> deviceResourceIds = new HashMap<>();
  private Map<UUID, UUID> applicationResourceIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public ResourceTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  /**
   * Creates new resource
   * 
   * @param customer customer
   * @param device device
   * @param application application
   * @param orderNumber order
   * @param parentId parentId
   * @param data data
   * @param name name
   * @param slug slug
   * @param type slug
   * @return created resource
   * @throws ApiException 
   */
  public Resource create(Customer customer, Device device, Application application, Integer orderNumber, UUID parentId, String data, String name, String slug, ResourceType type) throws ApiException {
    return create(customer, device, application, orderNumber, parentId, data, name, slug, type, Collections.emptyList(), Collections.emptyList());
  }
  
  /**
   * Creates new resource
   * 
   * @param customer customer
   * @param device device
   * @param application application
   * @param orderNumber order
   * @param parentId parentId
   * @param data data
   * @param name name
   * @param slug slug
   * @param type slug
   * @param properties properties 
   * @param styles styles
   * @return created resource
   * @throws ApiException 
   */
  public Resource create(Customer customer, Device device, Application application, Integer orderNumber, UUID parentId, String data, String name, String slug, ResourceType type, List<KeyValueProperty> properties, List<KeyValueProperty> styles) throws ApiException {
    Resource resource = new Resource();
    resource.setData(data);
    resource.setName(name);
    resource.setProperties(properties);
    resource.setSlug(slug);
    resource.setStyles(styles);
    resource.setType(type);
    resource.setParentId(parentId);
    resource.setOrderNumber(orderNumber);
    
    Resource result = getApi().createResource(customer.getId(), device.getId(), application.getId(), resource);
    
    customerResourceIds.put(result.getId(), customer.getId());
    deviceResourceIds.put(result.getId(), device.getId());
    applicationResourceIds.put(result.getId(), application.getId());
    
    return addClosable(result);
  }
  
  /**
   * Finds a resource
   * 
   * @param customer customer
   * @param resourceId resource id
   * @return found resource
   * @throws ApiException 
   */
  public Resource findResource(Customer customer, Device device, Application application, UUID resourceId) throws ApiException {
    return getApi().findResource(customer.getId(), device.getId(), application.getId(), resourceId);
  }
  
  /**
   * Lists resources
   * 
   * @param customer customer
   * @return found resources
   * @throws ApiException 
   */
  public List<Resource> listResources(Customer customer, Device device, Application application, Resource parent) throws ApiException {
    return getApi().listResources(customer.getId(), device.getId(), application.getId(), parent != null ? parent.getId() : null);
  }

  /**
   * Updates a resource into the API
   * 
   * @param customer customer
   * @param body body payload
   * @throws ApiException 
   */
  public Resource updateResource(Customer customer, Device device, Application application, Resource body) throws ApiException {
    return getApi().updateResource(customer.getId(), device.getId(), application.getId(), body.getId(), body);
  }
  
  /**
   * Deletes a resource from the API
   * 
   * @param customer customer
   * @param resource resource to be deleted
   * @throws ApiException 
   */
  public void delete(Customer customer, Device device, Application application, Resource resource) throws ApiException {
    getApi().deleteResource(customer.getId(), device.getId(), application.getId(), resource.getId());  
    
    removeCloseable(closable -> {
      if (!(closable instanceof Resource)) {
        return false;
      }

      Resource closeableResource = (Resource) closable;
      return closeableResource.getId().equals(resource.getId());
    });
  }
  
  /**
   * Asserts resource count within the system
   * 
   * @param expected expected count
   * @param customer customer
   * @throws ApiException 
   */
  public void assertCount(int expected, Customer customer, Device device, Application application, Resource parent) throws ApiException {
    assertEquals(expected, listResources(customer, device, application, parent).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param resourceId resource id
   */
  public void assertFindFailStatus(int expectedStatus, Customer customer, Device device, Application application, UUID resourceId) {
    assertFindFailStatus(expectedStatus, customer.getId(), device.getId(), application.getId(), resourceId);
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param resourceId resource id
   */
  public void assertFindFailStatus(int expectedStatus, UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId) {
    try {
      getApi().findResource(customerId, deviceId, applicationId, resourceId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param device device
   * @param application application
   * @param orderNumber order
   * @param parentId parentId
   * @param data data
   * @param name name
   * @param slug slug
   * @param type type
   * @param properties properties
   * @param styles styles
   */
  public void assertCreateFailStatus(int expectedStatus, Customer customer, Device device, Application application, Integer orderNumber, UUID parentId, String data, String name, String slug, ResourceType type, List<KeyValueProperty> properties, List<KeyValueProperty> styles) {
    try {
      create(customer, device, application, orderNumber, parentId, data, name, slug, type, properties, styles);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param resource resource
   */
  public void assertUpdateFailStatus(int expectedStatus, Customer customer, Device device, Application application, Resource resource) {
    try {
      updateResource(customer, device, application, resource);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param resource resource
   */
  public void assertDeleteFailStatus(int expectedStatus, Customer customer, Device device, Application application, Resource resource) {
    try {
      getApi().deleteResource(customer.getId(), device.getId(), application.getId(), resource.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   */
  public void assertListFailStatus(int expectedStatus, Customer customer, Device device, Application application, Resource parent) {
    try {
      listResources(customer, device, application, parent);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts that actual resource equals expected resource when both are serialized into JSON
   * 
   * @param expected expected resource
   * @param actual actual resource
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertResourcesEqual(Resource expected, Resource actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Resource resource) throws ApiException {
    UUID customerId = customerResourceIds.remove(resource.getId());
    UUID deviceId = deviceResourceIds.remove(resource.getId());
    UUID applicationId = applicationResourceIds.remove(resource.getId());
    getApi().deleteResource(customerId, deviceId, applicationId, resource.getId());  
  }

}
