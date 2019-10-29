package fi.metatavu.oioi.cm.medias;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;
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
  public Media createMedia(Media media, String contentType, MediaType type, String url, UUID creatorId) {
    return mediaDAO.create(UUID.randomUUID(), contentType, type, url, creatorId, creatorId);
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
}