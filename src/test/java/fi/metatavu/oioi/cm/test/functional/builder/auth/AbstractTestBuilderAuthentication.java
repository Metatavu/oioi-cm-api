package fi.metatavu.oioi.cm.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.test.functional.builder.file.FileTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.impl.ApplicationTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.impl.CustomerTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.impl.DeviceTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public abstract class AbstractTestBuilderAuthentication {

  private TestBuilder testBuilder;
  private CustomerTestBuilderResource customers;
  private DeviceTestBuilderResource devices;
  private FileTestBuilderResource files;
  private ApplicationTestBuilderResource applications;
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   */
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
   * Returns test builder resource for files
   * 
   * @return test builder resource for files
   * @throws IOException thrown when authentication fails
   */
  public FileTestBuilderResource files() throws IOException {
    if (files != null) {
      return files;
    }
    
    return new FileTestBuilderResource(testBuilder);
  }
  
  /**
   * Returns test builder resource for applications
   * 
   * @return test builder resource for applications
   * @throws IOException thrown when authentication fails
   */
  public ApplicationTestBuilderResource applications() throws IOException {
    if (applications != null) {
      return applications;
    }
    
    return new ApplicationTestBuilderResource(testBuilder, createClient());
  }

  /**
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
}
