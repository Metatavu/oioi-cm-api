package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Device;
import org.openapitools.client.model.Resource;
import org.openapitools.client.model.ResourceType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.wall.WallResource;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class WallExportTestsIT extends AbstractFunctionalTest {

  @Test
  public void testUploadFile() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Device device = builder.admin().devices().create(customer);
      Application application = builder.admin().applications().create(customer, device);
      
      
      Resource langFi = builder.admin().resources().create(customer, device, application, application.getRootResourceId(), null, "fi", "fi", ResourceType.LANGUAGE, Arrays.asList(getKeyValue("description", "Finnish language page")), Arrays.asList(getKeyValue("background", "#fff"), getKeyValue("color", "#00f")));
      
      Resource intro = builder.admin().resources().create(customer, device, application, langFi.getId(), null, "Intro", "intro", ResourceType.INTRO);      
      Resource introSlide = builder.admin().resources().create(customer, device, application, intro.getId(), null, "Intro slideshow", "slideshow", ResourceType.SLIDESHOW);
      Resource introPage1 = builder.admin().resources().create(customer, device, application, introSlide.getId(), null, "Intro slideshow page 1", "page-1", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, introPage1.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e", "Intro PDF", "pdf", ResourceType.PDF);
      Resource introPage2 = builder.admin().resources().create(customer, device, application, introSlide.getId(), null, "Intro slideshow page 1", "page-2", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, introPage2.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a", "Intro Image", "image", ResourceType.IMAGE);
      builder.admin().resources().create(customer, device, application, introPage2.getId(), "Heippa maailma", "Intro text", "text", ResourceType.TEXT);
      
      Resource menu = builder.admin().resources().create(customer, device, application, langFi.getId(), null, "Main Menu", "mainmenu", ResourceType.MENU);
      
      Resource menuPage1 = builder.admin().resources().create(customer, device, application, menu.getId(), null, "Menu Page 1", "page-1", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, menuPage1.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b", "Video", "video", ResourceType.VIDEO);
      
      
      Resource menuPage2 = builder.admin().resources().create(customer, device, application, menu.getId(), null, "Menu Page 2", "page-2", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, menuPage2.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b", "Video", "video", ResourceType.VIDEO);
      builder.admin().resources().create(customer, device, application, menuPage2.getId(), "Heippa taas", "Video text", "text", ResourceType.TEXT);
      
      File testOut = new File("/tmp/wall.json");
      
      try (FileOutputStream fileOutputStream = new FileOutputStream(testOut)) {     
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(fileOutputStream, downloadApplicationJson(application.getId()));
      }
      
    }
  }
  
  /**
   * Uploads resource into file store
   * 
   * @param resourceName resource name
   * @return upload response
   * @throws IOException thrown on upload failure
   */
  private WallResource downloadApplicationJson(UUID applicationId) throws IOException {
    
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    try (CloseableHttpClient client = clientBuilder.build()) {
      HttpGet get = new HttpGet(String.format("%s/v1/wall/%s", getBasePath(), applicationId));
      HttpResponse response = client.execute(get);
      assertEquals(200, response.getStatusLine().getStatusCode());
      
      HttpEntity httpEntity = response.getEntity();          
      ObjectMapper objectMapper = new ObjectMapper();           
      WallResource result = objectMapper.readValue(httpEntity.getContent(), WallResource.class);
      assertNotNull(result);      
      return result;
    }
  }
  
  /**
   * Returns API base path
   * 
   * @return API base path
   */
  protected String getBasePath() {
   return String.format("http://%s:%d", getHost(), getPort());
  }
  
  /**
   * Returns API host
   * 
   * @return API host
   */
  protected String getHost() {
    return System.getProperty("it.host");
  }
  
  /**
   * Returns API port
   * 
   * @return API port
   */
  protected Integer getPort() {
    return NumberUtils.createInteger(System.getProperty("it.port.http"));
  }
  
}
