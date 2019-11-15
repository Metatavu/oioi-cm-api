package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.Resource;
import org.openapitools.client.model.ResourceType;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Resource functional tests
 * 
 * @author Antti Leppä
 *
 */
public class ResourceTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreate() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);
      assertNotNull(builder.admin().resources().create(customer, device, application, application.getRootResourceId(), "data", "name", "slug", ResourceType.MENU));
    }
  }
  
  @Test
  public void testFindResource() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);      
      
      Resource createdResource = builder.admin().resources().create(customer, device, application, application.getRootResourceId(), "data", "name", "slug", ResourceType.MENU);
      
      builder.admin().resources().assertFindFailStatus(404, customer, device, application, UUID.randomUUID());
      builder.admin().resources().assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
      
      Resource foundResource = builder.admin().resources().findResource(customer, device, application, createdResource.getId());
      builder.admin().resources().assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), foundResource.getId());

      builder.admin().resources().assertResourcesEqual(createdResource, foundResource);
    }
  }
  
  @Test
  public void testListResources() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);

      Resource createdResource = builder.admin().resources().create(customer, device, application, application.getRootResourceId(), "data", "name", "slug", ResourceType.MENU);
      Resource rootResource = builder.admin().resources().findResource(customer, device, application, application.getRootResourceId());
      List<Resource> foundResources = builder.admin().resources().listResources(customer, device, application, rootResource);
      assertEquals(1, foundResources.size());
      builder.admin().resources().assertResourcesEqual(createdResource, foundResources.get(0));
    }
  }
  
  @Test
  public void testUpdateResource() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);

      Resource createdResource = builder.admin().resources().create(customer, 
          device, application, application.getRootResourceId(), "data", "name", "slug", ResourceType.MENU,
          Arrays.asList(getKeyValue("prop-1", "value"), getKeyValue("prop-2", "value-2")),
          Arrays.asList(getKeyValue("style-1", "value"), getKeyValue("style-2", "value-2")));
  
      Resource updateResource = builder.admin().resources().findResource(customer, device, application, createdResource.getId());
      updateResource.setData("updated data");
      updateResource.setName("updated name");
      updateResource.setProperties(Arrays.asList(getKeyValue("prop-1", "value-1"), getKeyValue("prop-3", "value-3")));
      updateResource.setStyles(Arrays.asList(getKeyValue("style-1", "value-1"), getKeyValue("style-3", "value-3")));
      
      Resource updatedResource = builder.admin().resources().updateResource(customer, device, application, updateResource);

      builder.admin().resources().assertJsonsEqual(updateResource.getProperties(), updatedResource.getProperties());
      builder.admin().resources().assertJsonsEqual(updateResource.getStyles(), updatedResource.getStyles());

      assertEquals(updateResource.getData(), updatedResource.getData());
      assertEquals(updateResource.getName(), updatedResource.getName());
    }
  }

  @Test
  public void testDeleteResource() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);
      Resource createdResource = builder.admin().resources().create(customer, device, application, application.getRootResourceId(), "data", "name", "slug", ResourceType.MENU);
      Resource foundResource = builder.admin().resources().findResource(customer, device, application, createdResource.getId());
      assertEquals(createdResource.getId(), foundResource.getId());
      builder.admin().resources().delete(customer, device, application, createdResource);
      builder.admin().resources().assertDeleteFailStatus(404, customer, device, application, createdResource);
    }
  }
  
}
