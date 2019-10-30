package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

public class CustomerTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateCustomer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().customers().create("test customer", "http://example.com/great-image.jpg"));
    }
  }

}
