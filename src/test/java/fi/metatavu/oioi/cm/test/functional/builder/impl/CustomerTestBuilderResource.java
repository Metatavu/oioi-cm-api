package fi.metatavu.oioi.cm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.openapitools.client.api.CustomersApi;
import org.openapitools.client.model.Customer;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.client.ApiException;
import fi.metatavu.oioi.cm.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Test builder resource for customers
 * 
 * @author Antti Leppä
 */
public class CustomerTestBuilderResource extends AbstractTestBuilderResource<Customer, CustomersApi> {
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public CustomerTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new customer
   * 
   * @param name name
   * @param imageUrl image URL
   * @return created customer
   * @throws ApiException 
   */
  public Customer create(String name, String imageUrl) throws ApiException {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setImageUrl(imageUrl);
    return addClosable(getApi().createCustomer(customer));
  }
  
  /**
   * Finds a customer
   * 
   * @param customerId customer id
   * @return found customer
   * @throws ApiException 
   */
  public Customer findCustomer(UUID customerId) throws ApiException {
    return getApi().findCustomer(customerId);
  }

  /**
   * Updates a customer into the API
   * 
   * @param body body payload
   * @throws ApiException 
   */
  public Customer updateCustomer(Customer body) throws ApiException {
    return getApi().updateCustomer(body.getId(), body);
  }
  
  /**
   * Deletes a customer from the API
   * 
   * @param customer customer to be deleted
   * @throws ApiException 
   */
  public void delete(Customer customer) throws ApiException {
    getApi().deleteCustomer(customer.getId());  
    
    removeCloseable(closable -> {
      if (closable instanceof Customer) {
        return !((Customer) closable).getId().equals(customer.getId());
      }
      
      return false;
    });
  }
  
  /**
   * Asserts customer count within the system
   * 
   * @param expected expected count
   * @throws ApiException 
   */
  public void assertCount(int expected) throws ApiException {
    assertEquals(expected, getApi().listCustomers().size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   */
  public void assertFindFailStatus(int expectedStatus, UUID customerId) {
    try {
      getApi().findCustomer(customerId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param name name
   * @param imageUrl image URL
   */
  public void assertCreateFailStatus(int expectedStatus, String name, String imageUrl) {
    try {
      create(name, imageUrl);
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
   */
  public void assertUpdateFailStatus(int expectedStatus, Customer customer) {
    try {
      updateCustomer(customer);
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
   */
  public void assertDeleteFailStatus(int expectedStatus, Customer customer) {
    try {
      getApi().deleteCustomer(customer.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listCustomers();
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts that actual customer equals expected customer when both are serialized into JSON
   * 
   * @param expected expected customer
   * @param actual actual customer
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertCustomersEqual(Customer expected, Customer actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Customer customer) throws ApiException {
    getApi().deleteCustomer(customer.getId());  
  }

}
