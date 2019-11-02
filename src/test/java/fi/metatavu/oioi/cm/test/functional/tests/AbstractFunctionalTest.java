package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.openapitools.client.model.KeyValueProperty;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Abstract base class for functional tests
 * 
 * @author Antti Leppä
 */
public abstract class AbstractFunctionalTest {
  
  @After
  public void assetCleanAfter() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertEquals(0, builder.admin().customers().listCustomers().size());
    }
  }
  
  /**
   * Creates key value object
   * 
   * @param key key
   * @param value value
   * @return key value object
   */
  protected KeyValueProperty getKeyValue(String key, String value) {
    KeyValueProperty result = new KeyValueProperty();
    result.setKey(key);
    result.setValue(value);
    return result;
  }
  
}
