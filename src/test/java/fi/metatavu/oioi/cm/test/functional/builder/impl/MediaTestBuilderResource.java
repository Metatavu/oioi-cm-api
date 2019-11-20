package fi.metatavu.oioi.cm.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.openapitools.client.api.MediasApi;
import org.openapitools.client.model.Customer;
import org.openapitools.client.model.Media;
import org.openapitools.client.model.MediaType;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.client.ApiException;
import fi.metatavu.oioi.cm.test.functional.builder.AbstractApiTestBuilderResource;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Test builder resource for medias
 * 
 * @author Antti Leppä
 */
public class MediaTestBuilderResource extends AbstractApiTestBuilderResource<Media, MediasApi> {
  
  private Map<UUID, UUID> customerMediaIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public MediaTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new media
   * 
   * @param customer customer
   * @param type type
   * @param contentType content type
   * @param url URL
   * @return media
   * @throws ApiException API Exception
   */
  public Media create(Customer customer) throws ApiException {
    return create(customer, MediaType.VIDEO, "video/mpeg", "http://example.com/video");
  }
  
  /**
   * Creates new media
   * 
   * @param customer customer
   * @param type type
   * @param contentType content type
   * @param url URL
   * @return media
   * @throws ApiException API Exception
   */
  public Media create(Customer customer, MediaType type, String contentType, String url) throws ApiException {
    Media media = new Media();
    media.setContentType(contentType);
    media.setType(type);
    media.setUrl(url);
    Media result = getApi().createMedia(customer.getId(), media);
    customerMediaIds.put(result.getId(), customer.getId());
    return addClosable(result);
  }
  
  /**
   * Finds a media
   * 
   * @param customer customer
   * @param mediaId media id
   * @return found media
   * @throws ApiException 
   */
  public Media findMedia(Customer customer, UUID mediaId) throws ApiException {
    return getApi().findMedia(customer.getId(), mediaId);
  }
  
  /**
   * Lists medias
   * 
   * @param customer customer
   * @return found medias
   * @throws ApiException 
   */
  public List<Media> listMedias(Customer customer, MediaType mediaType) throws ApiException {
    return getApi().listMedias(customer.getId(), mediaType);
  }

  /**
   * Updates a media into the API
   * 
   * @param customer customer
   * @param body body payload
   * @throws ApiException 
   */
  public Media updateMedia(Customer customer, Media body) throws ApiException {
    return getApi().updateMedia(customer.getId(), body.getId(), body);
  }
  
  /**
   * Deletes a media from the API
   * 
   * @param customer customer
   * @param media media to be deleted
   * @throws ApiException 
   */
  public void delete(Customer customer, Media media) throws ApiException {
    getApi().deleteMedia(customer.getId(), media.getId());  
    
    removeCloseable(closable -> {
      if (!(closable instanceof Media)) {
        return false;
      }

      Media closeableMedia = (Media) closable;
      return closeableMedia.getId().equals(media.getId());
    });
  }
  
  /**
   * Asserts media count within the system
   * 
   * @param expected expected count
   * @param customer customer
   * @param mediaType media type
   * @throws ApiException 
   */
  public void assertCount(int expected, Customer customer, MediaType mediaType) throws ApiException {
    assertEquals(expected, getApi().listMedias(customer.getId(), mediaType).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param mediaId media id
   */
  public void assertFindFailStatus(int expectedStatus, Customer customer, UUID mediaId) {
    assertFindFailStatus(expectedStatus, customer.getId(), mediaId);
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customerId customer id
   * @param mediaId media id
   */
  public void assertFindFailStatus(int expectedStatus, UUID customerId, UUID mediaId) {
    try {
      getApi().findMedia(customerId, mediaId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param name name
   * @param apiKey API key
   * @param metas metas
   */
  public void assertCreateFailStatus(int expectedStatus, Customer customer, MediaType type, String contentType, String url)  {
    try {
      create(customer, type, contentType, url);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param media media
   */
  public void assertUpdateFailStatus(int expectedStatus, Customer customer, Media media) {
    try {
      updateMedia(customer, media);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param media media
   */
  public void assertDeleteFailStatus(int expectedStatus, Customer customer, Media media) {
    try {
      getApi().deleteMedia(customer.getId(), media.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param customer customer
   * @param mediaType media type
   */
  public void assertListFailStatus(int expectedStatus, Customer customer, MediaType mediaType) {
    try {
      listMedias(customer, mediaType);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (ApiException e) {
      assertEquals(expectedStatus, e.getCode());
    }
  }

  /**
   * Asserts that actual media equals expected media when both are serialized into JSON
   * 
   * @param expected expected media
   * @param actual actual media
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertMediasEqual(Media expected, Media actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Media media) throws ApiException {
    UUID customerId = customerMediaIds.remove(media.getId());
    getApi().deleteMedia(customerId, media.getId());  
  }

}
