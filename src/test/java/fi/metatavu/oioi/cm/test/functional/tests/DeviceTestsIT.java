package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 *
 */
public class DeviceTestsIT extends AbstractFunctionalTest {

  @Test
  public void testDevice() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      assertNotNull(builder.admin().devices().create(customer, "test customer", "api key", "http://www.example.com/image.png", Arrays.asList(getKeyValue("key-1", "value-1"))));
    }
  }
  
  @Test
  public void testFindDevice() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device createdDevice = builder.admin().devices().create(customer);
      
      builder.admin().devices().assertFindFailStatus(404, customer, UUID.randomUUID());
      builder.admin().devices().assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID());
      
      Device foundDevice = builder.admin().devices().findDevice(customer, createdDevice.getId());
      builder.admin().devices().assertFindFailStatus(404, UUID.randomUUID(), foundDevice.getId());
      
      builder.admin().devices().assertDevicesEqual(createdDevice, foundDevice);
    }
  }
  
  @Test
  public void testListDevices() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device createdDevice = builder.admin().devices().create(customer);
      List<Device> foundDevices = builder.admin().devices().listDevices(customer);
      assertEquals(1, foundDevices.size());
      builder.admin().devices().assertDevicesEqual(createdDevice, foundDevices.get(0));
    }
  }
  
  @Test
  public void testUpdateDevice() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device createdDevice = builder.admin().devices().create(customer, "test customer", "api key", "http://www.example.com/image.png", Arrays.asList(getKeyValue("key-1", "value-1"), getKeyValue("key-2", "value-2")));

      Device updateDevice = builder.admin().devices().findDevice(customer, createdDevice.getId());
      updateDevice.setName("updated customer");
      updateDevice.setApiKey("api key");
      updateDevice.setImageUrl("http://www.example.com/updated.png");
      updateDevice.setMetas(Arrays.asList(getKeyValue("key-1", "value-1"), getKeyValue("key-3", "value-3")));
      
      Device updatedDevice = builder.admin().devices().updateDevice(customer, updateDevice);
      assertEquals(createdDevice.getId(), updatedDevice.getId());
      assertEquals(updateDevice.getName(), updatedDevice.getName());
      assertEquals(updateDevice.getApiKey(), updatedDevice.getApiKey());
      assertEquals(updateDevice.getImageUrl(), updatedDevice.getImageUrl());
      builder.admin().devices().assertJsonsEqual(updateDevice.getMetas(), updatedDevice.getMetas());
      
      Device foundDevice = builder.admin().devices().findDevice(customer, createdDevice.getId());
      assertEquals(createdDevice.getId(), foundDevice.getId());
      assertEquals(updateDevice.getName(), foundDevice.getName());
      assertEquals(updateDevice.getApiKey(), foundDevice.getApiKey());
      assertEquals(updateDevice.getImageUrl(), foundDevice.getImageUrl());
      builder.admin().devices().assertJsonsEqual(updateDevice.getMetas(), foundDevice.getMetas());
    }
  }

  @Test
  public void testDeleteDevice() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device createdDevice = builder.admin().devices().create(customer);
      Device foundDevice = builder.admin().devices().findDevice(customer, createdDevice.getId());
      assertEquals(createdDevice.getId(), foundDevice.getId());
      builder.admin().devices().delete(customer, createdDevice);
      builder.admin().devices().assertDeleteFailStatus(404, customer, createdDevice);
    }
  }
  
}
