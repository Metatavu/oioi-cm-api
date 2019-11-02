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
import org.openapitools.client.api.DevicesApi;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.KeyValueProperty;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.client.ApiException;
import fi.metatavu.oioi.cm.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Test builder resource for devices
 * 
 * @author Antti Leppä
 */
public class DeviceTestBuilderResource extends AbstractTestBuilderResource<Device, DevicesApi> {
  
  private Map<UUID, UUID> customerDeviceIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public DeviceTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new device with default values
   * 
   * @param customer customer
   * 
   * @return created device
   * @throws ApiException 
   */
  public Device create(Customer customer) throws ApiException {
    return create(customer, "default name", "api-key", Collections.emptyList());
  }
  
  /**
   * Creates new device
   * 
   * @param customer customer
   * @param name name
   * @param apiKey API key
   * @param metas metas
   * @return created device
   * @throws ApiException 
   */
  public Device create(Customer customer, String name, String apiKey, List<KeyValueProperty> metas) throws ApiException {
    Device device = new Device();
    device.setName(name);
    device.setApiKey(apiKey);
    device.setMetas(metas);
    Device result = getApi().createDevice(customer.getId(), device);
    customerDeviceIds.put(result.getId(), customer.getId());
    return addClosable(result);
  }
  
  /**
   * Finds a device
   * 
   * @param customer customer
   * @param deviceId device id
   * @return found device
   * @throws ApiException 
   */
  public Device findDevice(Customer customer, UUID deviceId) throws ApiException {
    return getApi().findDevice(customer.getId(), deviceId);
  }
  
  /**
   * Lists devices
   * 
   * @param customer customer
   * @return found devices
   * @throws ApiException 
   */
  public List<Device> listDevices(Customer customer) throws ApiException {
    return getApi().listDevices(customer.getId());
  }

  /**
   * Updates a device into the API
   * 
   * @param customer customer
   * @param body body payload
   * @throws ApiException 
   */
  public Device updateDevice(Customer customer, Device body) throws ApiException {
    return getApi().updateDevice(customer.getId(), body.getId(), body);
  }
  
  /**
   * Deletes a device from the API
   * 
   * @param customer customer
   * @param device device to be deleted
   * @throws ApiException 
   */
  public void delete(Customer customer, Device device) throws ApiException {
    getApi().deleteDevice(customer.getId(), device.getId());  
    
    removeCloseable(closable -> {
      if (!(closable instanceof Device)) {
        return false;
      }

      Device closeableDevice = (Device) closable;
      return closeableDevice.getId().equals(device.getId());
    });
  }
  
  /**
   * Asserts device count within the system
   * 
   * @param expected expected count
   * @param customer customer
   * @throws ApiException 
   */
  public void assertCount(int expected, Customer customer) throws ApiException {
    assertEquals(expected, getApi().listDevices(customer.getId()).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param deviceId device id
   */
  public void assertFindFailStatus(int expectedStatus, Customer customer, UUID deviceId) {
    assertFindFailStatus(expectedStatus, customer.getId(), deviceId);
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param deviceId device id
   */
  public void assertFindFailStatus(int expectedStatus, UUID customerId, UUID deviceId) {
    try {
      getApi().findDevice(customerId, deviceId);
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
   * @param name name
   * @param apiKey API key
   * @param metas metas
   */
  public void assertCreateFailStatus(int expectedStatus, Customer customer, String name, String apiKey, List<KeyValueProperty> metas) {
    try {
      create(customer, name, apiKey, metas);
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
   */
  public void assertUpdateFailStatus(int expectedStatus, Customer customer, Device device) {
    try {
      updateDevice(customer, device);
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
   */
  public void assertDeleteFailStatus(int expectedStatus, Customer customer, Device device) {
    try {
      getApi().deleteDevice(customer.getId(), device.getId());
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
  public void assertListFailStatus(int expectedStatus, Customer customer) {
    try {
      listDevices(customer);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts that actual device equals expected device when both are serialized into JSON
   * 
   * @param expected expected device
   * @param actual actual device
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertDevicesEqual(Device expected, Device actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Device device) throws ApiException {
    UUID customerId = customerDeviceIds.remove(device.getId());
    getApi().deleteDevice(customerId, device.getId());  
  }

}
