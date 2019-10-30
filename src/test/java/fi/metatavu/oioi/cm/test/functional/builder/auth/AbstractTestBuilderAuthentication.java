package fi.metatavu.oioi.cm.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.test.functional.builder.impl.CustomerTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication {

  private TestBuilder testBuilder;
  private CustomerTestBuilderResource customers;
  
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
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
}
