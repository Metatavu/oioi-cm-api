package fi.metatavu.oioi.cm.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.oioi.cm.model.MediaType;
import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.model.Media;
import fi.metatavu.oioi.cm.persistence.model.Media_;

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
   * @param customer customer
   * @param contentType contentType
   * @param type type
   * @param url url
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created media
   */
  public Media create(UUID id, Customer customer, String contentType, MediaType type, String url, UUID creatorId, UUID lastModifierId) {
    Media media = new Media();
    media.setCustomer(customer);
    media.setContentType(contentType);
    media.setType(type);
    media.setUrl(url);
    media.setId(id);
    media.setCreatorId(creatorId);
    media.setLastModifierId(lastModifierId);
    return persist(media);
  }

  /**
   * Lists medias
   * 
   * @param customer filter by customer. Ignored if null
   * @param mediaType filter by media type. Ignored if null
   * @return List of medias
   */
  public List<Media> list(Customer customer, MediaType mediaType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Media> criteria = criteriaBuilder.createQuery(Media.class);
    Root<Media> root = criteria.from(Media.class);
    
    List<Predicate> criterias = new ArrayList<>();
    
    if (customer != null) {
      criterias.add(criteriaBuilder.equal(root.get(Media_.customer), customer));
    }

    if (mediaType != null) {
      criterias.add(criteriaBuilder.equal(root.get(Media_.type), mediaType));
    }
    
    criteria.select(root);
    criteria.where(criterias.toArray(new Predicate[0]));
    
    return entityManager.createQuery(criteria).getResultList();
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
