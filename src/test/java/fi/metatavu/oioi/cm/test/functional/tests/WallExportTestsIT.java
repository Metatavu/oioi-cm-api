package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.wall.WallApplication;
import fi.metatavu.oioi.cm.wall.WallResource;

/**
 * Wall export functional tests
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
      
      Resource langFi = builder.admin().resources().create(customer, device, application, 0, application.getRootResourceId(), null, "fi", "fi", ResourceType.LANGUAGE, Arrays.asList(getKeyValue("description", "Finnish language page")), Arrays.asList(getKeyValue("background", "#fff"), getKeyValue("color", "#00f")));
      
      Resource intro = builder.admin().resources().create(customer, device, application,  0, langFi.getId(), null, "Intro", "intro", ResourceType.INTRO);      
      Resource introSlide = builder.admin().resources().create(customer, device, application, 0, intro.getId(), null, "Intro slideshow", "slideshow", ResourceType.SLIDESHOW);
      Resource introPage1 = builder.admin().resources().create(customer, device, application,  0, introSlide.getId(), null, "Intro slideshow page 1", "page-1", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, 0, introPage1.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e", "Intro PDF", "pdf", ResourceType.PDF);
      Resource introPage2 = builder.admin().resources().create(customer, device, application, 1, introSlide.getId(), null, "Intro slideshow page 1", "page-2", ResourceType.PAGE);
      Resource introPage2Image = builder.admin().resources().create(customer, device, application, 0, introPage2.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/bc55c04e-1d9e-4e71-a384-d1621c90162a", "Intro Image", "image", ResourceType.IMAGE);
      Resource introPage2Text = builder.admin().resources().create(customer, device, application, 1, introPage2.getId(), "Heippa maailma", "Intro text", "text", ResourceType.TEXT);
      
      Resource menu = builder.admin().resources().create(customer, device, application, 1, langFi.getId(), null, "Main Menu", "mainmenu", ResourceType.MENU);
      
      Resource menuPage1 = builder.admin().resources().create(customer, device, application, 0, menu.getId(), null, "Menu Page 1", "page-1", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, 0, menuPage1.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b", "Video", "video", ResourceType.VIDEO);
      
      
      Resource menuPage2 = builder.admin().resources().create(customer, device, application, 1, menu.getId(), null, "Menu Page 2", "page-2", ResourceType.PAGE);
      builder.admin().resources().create(customer, device, application, 0, menuPage2.getId(), "https://oioi-static.metatavu.io/0f57bd21-7bb1-4308-bf52-0ab6d40bd88e/71b700d7-1264-43f9-9686-a137780cef4b", "Video", "video", ResourceType.VIDEO);
      Resource menuPage2Video = builder.admin().resources().create(customer, device, application, 0, menuPage2.getId(), "Heippa taas", "Video text", "text", ResourceType.TEXT);
      
      WallApplication exportWallApplication = downloadApplicationJson(application.getId());
      
      assertNotNull(exportWallApplication);
      assertNotNull(exportWallApplication.getRoot());
      assertEquals(menuPage2Video.getModifiedAt(), exportWallApplication.getModifiedAt());
      
      List<WallResource> exportRootChildren = exportWallApplication.getRoot().getChildren();
      
      assertEquals(1, exportRootChildren.size());
      assertEquals(langFi.getSlug(), exportRootChildren.get(0).getSlug());
      
      WallResource exportIntro = exportRootChildren.get(0).getChildren().get(0);
      assertEquals(intro.getSlug(), exportIntro.getSlug());
      
      WallResource exportIntroSlide = exportIntro.getChildren().get(0);
      assertEquals(introSlide.getSlug(), exportIntroSlide.getSlug());
      
      WallResource exportIntroPage2 = exportIntroSlide.getChildren().get(1);
      assertEquals(introPage2.getSlug(), exportIntroPage2.getSlug());
      
      WallResource exportIntroPage2Image = exportIntroPage2.getChildren().get(0);
      assertEquals(exportIntroPage2Image.getSlug(), introPage2Image.getSlug());

      WallResource exportIntroPage2Text = exportIntroPage2.getChildren().get(1);
      assertEquals(exportIntroPage2Text.getSlug(), introPage2Text.getSlug());
    }
  }
  
  /**
   * Uploads resource into file store
   * 
   * @param applicationId application id
   * @return upload response
   * @throws IOException thrown on upload failure
   */
  private WallApplication downloadApplicationJson(UUID applicationId) throws IOException {
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    try (CloseableHttpClient client = clientBuilder.build()) {
      HttpGet get = new HttpGet(String.format("%s/v1/application/%s", getBasePath(), applicationId));
      HttpResponse response = client.execute(get);
      assertEquals(200, response.getStatusLine().getStatusCode());
      
      HttpEntity httpEntity = response.getEntity();          
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModules(new JavaTimeModule());

      WallApplication result = objectMapper.readValue(httpEntity.getContent(), WallApplication.class);
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
