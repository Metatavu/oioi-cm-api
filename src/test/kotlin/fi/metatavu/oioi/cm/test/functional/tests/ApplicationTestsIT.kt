package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.Application;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Application functional tests
 * 
 * @author Heikki Kurhinen
 *
 */
public class ApplicationTestsIT extends AbstractFunctionalTest {

  @Test
  public void testApplication() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      assertNotNull(builder.admin().applications().create(customer, device, "test application"));
      Customer anotherCustomer = builder.admin().customers().create();
      builder.admin().applications().assertCreateFailStatus(400, anotherCustomer, device, "fail application");
    }
  }
  
  @Test
  public void testFindApplication() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application createdApplication = builder.admin().applications().create(customer, device);
      
      builder.admin().applications().assertFindFailStatus(404, customer, device, UUID.randomUUID());
      builder.admin().applications().assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
      
      Application foundApplication = builder.admin().applications().findApplication(customer, device, createdApplication.getId());
      builder.admin().applications().assertFindFailStatus(404, UUID.randomUUID(), device.getId(), foundApplication.getId());
      builder.admin().applications().assertFindFailStatus(404, customer.getId(), UUID.randomUUID(), foundApplication.getId());
      
      builder.admin().applications().assertApplicationsEqual(createdApplication, foundApplication);

      Customer anotherCustomer = builder.admin().customers().create();
      builder.admin().applications().assertFindFailStatus(400, anotherCustomer.getId(), device.getId(), foundApplication.getId());
      Device anotherDevice = builder.admin().devices().create(anotherCustomer);
      builder.admin().applications().assertFindFailStatus(400, anotherCustomer.getId(), anotherDevice.getId(), foundApplication.getId());
    }
  }
  
  @Test
  public void testListApplications() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application createdApplication = builder.admin().applications().create(customer, device);
      List<Application> foundApplications = builder.admin().applications().listApplications(customer, device);
      assertEquals(1, foundApplications.size());
      builder.admin().applications().assertApplicationsEqual(createdApplication, foundApplications.get(0));

      Customer anotherCustomer = builder.admin().customers().create();
      builder.admin().applications().assertListFailStatus(400, anotherCustomer, device);
    }
  }
  
  @Test
  public void testUpdateApplication() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application createdApplication = builder.admin().applications().create(customer, device, "test application");

      Application updateApplication = builder.admin().applications().findApplication(customer, device, createdApplication.getId());
      updateApplication.setName("updated application");
      
      Application updatedApplication = builder.admin().applications().updateApplication(customer, device, updateApplication);
      assertEquals(createdApplication.getId(), updatedApplication.getId());
      assertNotEquals(createdApplication.getName(), updatedApplication.getName());

      Application foundApplication = builder.admin().applications().findApplication(customer, device, createdApplication.getId());
      assertEquals(createdApplication.getId(), foundApplication.getId());
      assertEquals(updateApplication.getName(), foundApplication.getName());

      UUID randomCustomerId = UUID.randomUUID();
      UUID randomDeviceId = UUID.randomUUID();

      builder.admin().applications().assertUpdateFailStatus(404, randomCustomerId, randomDeviceId, foundApplication);
      builder.admin().applications().assertUpdateFailStatus(404, randomCustomerId, device.getId(), foundApplication);
      builder.admin().applications().assertUpdateFailStatus(404, customer.getId(), randomDeviceId, foundApplication);
      
      Customer anotherCustomer = builder.admin().customers().create();
      Device anotherDevice = builder.admin().devices().create(anotherCustomer);
      builder.admin().applications().assertUpdateFailStatus(400, anotherCustomer, device, foundApplication);
      builder.admin().applications().assertUpdateFailStatus(400, anotherCustomer, anotherDevice, foundApplication);
    }
  }

  @Test
  public void testDeleteApplication() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application createdApplication = builder.admin().applications().create(customer, device);
      Application foundApplication = builder.admin().applications().findApplication(customer, device, createdApplication.getId());
      assertEquals(createdApplication.getId(), foundApplication.getId());

      UUID randomCustomerId = UUID.randomUUID();
      UUID randomDeviceId = UUID.randomUUID();
      UUID randomApplicationId = UUID.randomUUID();

      builder.admin().applications().assertDeleteFailStatus(404, randomCustomerId, randomDeviceId, randomApplicationId);
      builder.admin().applications().assertDeleteFailStatus(404, randomCustomerId, device.getId(), foundApplication.getId());
      builder.admin().applications().assertDeleteFailStatus(404, customer.getId(), randomDeviceId, foundApplication.getId());

      Customer anotherCustomer = builder.admin().customers().create();
      Device anotherDevice = builder.admin().devices().create(anotherCustomer);

      builder.admin().applications().assertDeleteFailStatus(400, anotherCustomer, device, foundApplication);
      builder.admin().applications().assertDeleteFailStatus(400, anotherCustomer, anotherDevice, foundApplication);

      builder.admin().applications().delete(customer, device, createdApplication);
      builder.admin().applications().assertDeleteFailStatus(404, customer, device, createdApplication);
    }
  }
  
}
