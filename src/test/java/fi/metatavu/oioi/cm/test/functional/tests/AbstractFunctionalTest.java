package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
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
  
  /**
   * Calculates contents md5 from a resource
   * 
   * @param resourceName resource name
   * @return resource contents md5
   * @throws IOException thrown when file reading fails
   */
  protected String getResourceMd5(String resourceName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    try (InputStream fileStream = classLoader.getResourceAsStream(resourceName)) {
      return DigestUtils.md5Hex(fileStream);
    }    
  }
  
}
