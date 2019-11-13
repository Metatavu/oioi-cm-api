package fi.metatavu.oioi.cm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.openapitools.client.api.ApplicationsApi;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.Application;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.client.ApiException;
import fi.metatavu.oioi.cm.test.functional.builder.AbstractApiTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Test builder resource for applications
 * 
 * @author Heikki Kurhinen
 */
public class ApplicationTestBuilderResource extends AbstractApiTestBuilderResource<Application, ApplicationsApi> {
  
  private Map<UUID, UUID> customerApplicationIds = new HashMap<>();

  private Map<UUID, UUID> deviceApplicationIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public ApplicationTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new application with default values
   * 
   * @param customer customer
   * @param device device
   * 
   * @return created application
   * @throws ApiException 
   */
  public Application create(Customer customer, Device device) throws ApiException {
    return create(customer, device, "default name");
  }
  
  /**
   * Creates new application
   * 
   * @param customer customer
   * @param device device
   * @param name name
   * @return created application
   * @throws ApiException 
   */
  public Application create(Customer customer, Device device, String name ) throws ApiException {
    Application application = new Application();
    application.setName(name);
    Application result = getApi().createApplication(customer.getId(), device.getId(), application);
    customerApplicationIds.put(result.getId(), customer.getId());
    deviceApplicationIds.put(result.getId(), device.getId());
    return addClosable(result);
  }
  
  /**
   * Finds a application
   * 
   * @param customer customer
   * @param device device
   * @param applicationId application id
   * @return found application
   * @throws ApiException 
   */
  public Application findApplication(Customer customer, Device device, UUID applicationId) throws ApiException {
    return getApi().findApplication(customer.getId(), device.getId(), applicationId);
  }
  
  /**
   * Lists applications
   * 
   * @param customer customer
   * @param device device
   * @return found applications
   * @throws ApiException 
   */
  public List<Application> listApplications(Customer customer, Device device) throws ApiException {
    return getApi().listApplications(customer.getId(), device.getId());
  }

  /**
   * Updates a application into the API
   * 
   * @param customer customer
   * @param device device
   * @param body body payload
   * @throws ApiException 
   */
  public Application updateApplication(Customer customer, Device device, Application body) throws ApiException {
    return getApi().updateApplication(customer.getId(), device.getId(), body.getId(), body);
  }
  
  /**
   * Deletes a application from the API
   * 
   * @param customer customer
   * @param device device
   * @param application application to be deleted
   * @throws ApiException 
   */
  public void delete(Customer customer, Device device, Application application) throws ApiException {
    getApi().deleteApplication(customer.getId(), device.getId(), application.getId());  
    
    removeCloseable(closable -> {
      if (!(closable instanceof Application)) {
        return false;
      }

      Application closeableApplication = (Application) closable;
      return closeableApplication.getId().equals(application.getId());
    });
  }
  
  /**
   * Asserts application count within the system
   * 
   * @param expected expected count
   * @param customer customer
   * @param device device
   * @throws ApiException 
   */
  public void assertCount(int expected, Customer customer, Device device) throws ApiException {
    assertEquals(expected, getApi().listApplications(customer.getId(), device.getId()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param device device
   * @param applicationId application id
   */
  public void assertFindFailStatus(int expectedStatus, Customer customer, Device device, UUID applicationId) {
    assertFindFailStatus(expectedStatus, customer.getId(), device.getId(), applicationId);
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param deviceId device id
   * @param applicationId application id
   */
  public void assertFindFailStatus(int expectedStatus, UUID customerId, UUID deviceId, UUID applicationId) {
    try {
      getApi().findApplication(customerId, deviceId, applicationId);
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
   * @param name name
   * @param apiKey API key
   * @param metas metas
   */
  public void assertCreateFailStatus(int expectedStatus, Customer customer, Device device, String name) {
    try {
      create(customer, device, name);
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
   * @param device device
   * @param application application
   */
  public void assertUpdateFailStatus(int expectedStatus, Customer customer, Device device, Application application) {
    try {
      updateApplication(customer, device, application);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param deviceId device id
   * @param application application
   */
  public void assertUpdateFailStatus(int expectedStatus, UUID customerId, UUID deviceId, Application application) {
    try {
      getApi().updateApplication(customerId, deviceId, application.getId(), application);
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
   * @param device device
   * @param application application
   */
  public void assertDeleteFailStatus(int expectedStatus, Customer customer, Device device, Application application) {
    try {
      getApi().deleteApplication(customer.getId(), device.getId(), application.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param deviceId device id
   * @param applicationId application id
   */
  public void assertDeleteFailStatus(int expectedStatus, UUID customerId, UUID deviceId, UUID applicationId) {
    try {
      getApi().deleteApplication(customerId, deviceId, applicationId);
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
   * @param device device
   */
  public void assertListFailStatus(int expectedStatus, Customer customer, Device device) {
    try {
      listApplications(customer, device);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts that actual application equals expected application when both are serialized into JSON
   * 
   * @param expected expected application
   * @param actual actual application
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertApplicationsEqual(Application expected, Application actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Application application) throws ApiException {
    UUID customerId = customerApplicationIds.remove(application.getId());
    UUID deviceId = deviceApplicationIds.remove(application.getId());
    getApi().deleteApplication(customerId, deviceId, application.getId());  
  }

}
