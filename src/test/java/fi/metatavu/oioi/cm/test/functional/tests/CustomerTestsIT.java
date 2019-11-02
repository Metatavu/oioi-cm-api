package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.openapitools.client.model.Customer;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 *
 */
public class CustomerTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateCustomer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().customers().create("test customer", "http://example.com/great-image.jpg"));
    }
  }
  
  @Test
  public void testFindCustomer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer createdCustomer = builder.admin().customers().create("test customer", "http://example.com/great-image.jpg");
      builder.admin().customers().assertFindFailStatus(404, UUID.randomUUID());
      Customer foundCustomer = builder.admin().customers().findCustomer(createdCustomer.getId());
      builder.admin().customers().assertCustomersEqual(createdCustomer, foundCustomer);
    }
  }
  
  @Test
  public void testListCustomers() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer createdCustomer = builder.admin().customers().create("test customer", "http://example.com/great-image.jpg");
      List<Customer> foundCustomers = builder.admin().customers().listCustomers();
      assertEquals(1, foundCustomers.size());
      builder.admin().customers().assertCustomersEqual(createdCustomer, foundCustomers.get(0));
    }
  }
  
  @Test
  public void testUpdateCustomer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer createdCustomer = builder.admin().customers().create("test customer", "http://example.com/great-image.jpg");
      builder.admin().customers().assertCustomersEqual(createdCustomer, builder.admin().customers().findCustomer(createdCustomer.getId()));

      Customer updateCustomer = builder.admin().customers().findCustomer(createdCustomer.getId());
      updateCustomer.setName("updated customer");
      updateCustomer.setImageUrl("http://example.com/greater-image.jpg");
      Customer updatedCustomer = builder.admin().customers().updateCustomer(updateCustomer);
      assertEquals(createdCustomer.getId(), updatedCustomer.getId());
      assertEquals(updateCustomer.getName(), updatedCustomer.getName());
      assertEquals(updateCustomer.getImageUrl(), updatedCustomer.getImageUrl());
      Customer foundCustomer = builder.admin().customers().findCustomer(createdCustomer.getId());
      assertEquals(createdCustomer.getId(), foundCustomer.getId());
      assertEquals(updateCustomer.getName(), foundCustomer.getName());
      assertEquals(updateCustomer.getImageUrl(), foundCustomer.getImageUrl());
    }
  }

  @Test
  public void testDeleteCustomer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer createdCustomer = builder.admin().customers().create("test customer", "http://example.com/great-image.jpg");
      Customer foundCustomer = builder.admin().customers().findCustomer(createdCustomer.getId());
      assertEquals(createdCustomer.getId(), foundCustomer.getId());
      builder.admin().customers().delete(createdCustomer);
      builder.admin().customers().assertDeleteFailStatus(404, createdCustomer);
    }
  }
  
}
