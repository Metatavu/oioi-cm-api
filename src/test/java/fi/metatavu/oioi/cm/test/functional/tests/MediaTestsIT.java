package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Media;
import org.openapitools.client.model.MediaType;

import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 *
 */
public class MediaTestsIT extends AbstractFunctionalTest {

  @Test
  public void testMedia() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      assertNotNull(builder.admin().medias().create(customer, MediaType.IMAGE, "image/jpeg", "http://example.com/test.jpg"));
    }
  }
  
  @Test
  public void testFindMedia() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Media createdMedia = builder.admin().medias().create(customer);
      
      builder.admin().medias().assertFindFailStatus(404, customer, UUID.randomUUID());
      builder.admin().medias().assertFindFailStatus(404, UUID.randomUUID(), UUID.randomUUID());
      
      Media foundMedia = builder.admin().medias().findMedia(customer, createdMedia.getId());
      builder.admin().medias().assertFindFailStatus(404, UUID.randomUUID(), foundMedia.getId());
      
      builder.admin().medias().assertMediasEqual(createdMedia, foundMedia);
    }
  }
  
  @Test
  public void testListMedias() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Media createdMedia = builder.admin().medias().create(customer);
      List<Media> foundMedias = builder.admin().medias().listMedias(customer, null);
      assertEquals(1, foundMedias.size());
      builder.admin().medias().assertMediasEqual(createdMedia, foundMedias.get(0));
      
      assertEquals(1, builder.admin().medias().listMedias(customer, MediaType.VIDEO).size());
      assertEquals(0, builder.admin().medias().listMedias(customer, MediaType.PDF).size());

    }
  }
  
  @Test
  public void testUpdateMedia() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Media createdMedia = builder.admin().medias().create(customer);

      Media updateMedia = builder.admin().medias().findMedia(customer, createdMedia.getId());
      updateMedia.setContentType("image/changed");
      updateMedia.setType(MediaType.VIDEO);
      updateMedia.setUrl("http://www.example.com/changed");
      
      Media updatedMedia = builder.admin().medias().updateMedia(customer, updateMedia);
      assertEquals(createdMedia.getId(), updatedMedia.getId());
      assertEquals(updatedMedia.getContentType(), updatedMedia.getContentType());
      assertEquals(updatedMedia.getUrl(), updatedMedia.getUrl());
      
      Media foundMedia = builder.admin().medias().findMedia(customer, createdMedia.getId());
      assertEquals(createdMedia.getId(), foundMedia.getId());
      assertEquals(updatedMedia.getContentType(), foundMedia.getContentType());
      assertEquals(updatedMedia.getUrl(), foundMedia.getUrl());
    }
  }

  @Test
  public void testDeleteMedia() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Customer customer = builder.admin().customers().create();
      Media createdMedia = builder.admin().medias().create(customer);
      Media foundMedia = builder.admin().medias().findMedia(customer, createdMedia.getId());
      assertEquals(createdMedia.getId(), foundMedia.getId());
      builder.admin().medias().delete(customer, createdMedia);
      builder.admin().medias().assertDeleteFailStatus(404, customer, createdMedia);
    }
  }
  
}
