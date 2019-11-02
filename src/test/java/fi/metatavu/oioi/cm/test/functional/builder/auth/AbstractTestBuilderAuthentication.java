package fi.metatavu.oioi.cm.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.test.functional.builder.impl.CustomerTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.impl.DeviceTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication {

  private TestBuilder testBuilder;
  private CustomerTestBuilderResource customers;
  private DeviceTestBuilderResource devices;
  
  protected AbstractTestBuilderAuthentication(TestBuilder testBuilder) {
    this.testBuilder = testBuilder;
  }
  
  /**
   * Returns test builder resource for customers
   * 
   * @return test builder resource for customers
   * @throws IOException thrown when authentication fails
   */
  public CustomerTestBuilderResource customers() throws IOException {
    if (customers != null) {
      return customers;
    }
    
    return new CustomerTestBuilderResource(testBuilder, createClient());
  }

  /**
   * Returns test builder resource for customers
   * 
   * @return test builder resource for customers
   * @throws IOException thrown when authentication fails
   */
  public DeviceTestBuilderResource devices() throws IOException {
    if (devices != null) {
      return devices;
    }
    
    return new DeviceTestBuilderResource(testBuilder, createClient());
  }
  
  /**
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
}
