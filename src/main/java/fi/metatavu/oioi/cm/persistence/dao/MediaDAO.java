package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

import fi.metatavu.oioi.cm.model.MediaType;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for Media
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MediaDAO extends AbstractDAO<Media> {

  /**
   * Creates new Media
   * 
   * @param id id
   * @param contentType contentType
   * @param type type
   * @param url url
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created media
   */
  public Media create(UUID id, String contentType, MediaType type, String url, UUID creatorId, UUID lastModifierId) {
    Media media = new Media();
    media.setContentType(contentType);
    media.setType(type);
    media.setUrl(url);
    media.setId(id);
    media.setCreatorId(creatorId);
    media.setLastModifierId(lastModifierId);
    return persist(media);
  }

  /**
   * Updates contentType
   *
   * @param contentType contentType
   * @param lastModifierId last modifier's id
   * @return updated media
   */
  public Media updateContentType(Media media, String contentType, UUID lastModifierId) {
    media.setLastModifierId(lastModifierId);
    media.setContentType(contentType);
    return persist(media);
  }

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated media
   */
  public Media updateType(Media media, MediaType type, UUID lastModifierId) {
    media.setLastModifierId(lastModifierId);
    media.setType(type);
    return persist(media);
  }

  /**
   * Updates url
   *
   * @param url url
   * @param lastModifierId last modifier's id
   * @return updated media
   */
  public Media updateUrl(Media media, String url, UUID lastModifierId) {
    media.setLastModifierId(lastModifierId);
    media.setUrl(url);
    return persist(media);
  }

}
