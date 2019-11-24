package fi.metatavu.oioi.cm.medias;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.UUID;

import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.model.Media;
import fi.metatavu.oioi.cm.model.MediaType;
import fi.metatavu.oioi.cm.persistence.dao.MediaDAO;

/**
 * Controller for Media
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MediaController {

  @Inject
  private MediaDAO mediaDAO;

 /**
   * Create media
   *
   * @param contentType contentType
   * @param type type
   * @param url url
   * @param creatorId creator id
   * @return created media
   */
  public Media createMedia(Customer customer, String contentType, MediaType type, String url, UUID creatorId) {
    return mediaDAO.create(UUID.randomUUID(), customer, contentType, type, url, creatorId, creatorId);
  }

  /**
   * Find media by id
   * 
   * @param id media id
   * @return found media or null if not found
   */
  public Media findMediaById(UUID id) {
    return mediaDAO.findById(id);
  }

  /**
   * Lists medias
   * 
   * @param customer filter by customer. Ignored if null
   * @param mediaType filter by media type. Ignored if null
   * @return List of medias
   */
  public List<Media> listMedias(Customer customer, MediaType mediaType) {
    return mediaDAO.list(customer, mediaType);
  }

  /**
   * Update media
   *
   * @param contentType contentType
   * @param type type
   * @param url url
   * @param lastModifierId last modifier id
   * @return updated media
   */
  public Media updateMedia(Media media, String contentType, MediaType type, String url, UUID lastModifierId) {
    mediaDAO.updateContentType(media, contentType, lastModifierId);
    mediaDAO.updateType(media, type, lastModifierId);
    mediaDAO.updateUrl(media, url, lastModifierId);
    return media;
  }

  /**
   * Deletes media
   * 
   * @param media media
   */
  public void deleteMedia(Media media) {
    mediaDAO.delete(media);
  }
}