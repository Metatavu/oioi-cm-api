package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.UUID;

import fi.metatavu.oioi.cm.model.ResourceType;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for Resource
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceDAO extends AbstractDAO<Resource> {

  /**
   * Creates new Resource
   * 
   * @param id id
   * @param orderNumber orderNumber
   * @param data data
   * @param keycloakResorceId keycloakResorceId
   * @param name name
   * @param parent parent
   * @param slug slug
   * @param type type
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created resource
   */
  @SuppressWarnings ("squid:S00107")
  public Resource create(UUID id, Integer orderNumber, String data, UUID keycloakResorceId, String name, Resource parent, String slug, ResourceType type, UUID creatorId, UUID lastModifierId) {
    Resource resource = new Resource();
    resource.setData(data);
    resource.setKeycloakResorceId(keycloakResorceId);
    resource.setName(name);
    resource.setOrderNumber(orderNumber);
    resource.setParent(parent);
    resource.setSlug(slug);
    resource.setType(type);
    resource.setId(id);
    resource.setCreatorId(creatorId);
    resource.setLastModifierId(lastModifierId);
    return persist(resource);
  }

  /**
   * Lists resources by parent
   * 
   * @param parent parent
   * @return List of applications
   */
  public List<Resource> listByParent(Resource parent) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Resource_.parent), parent));
    criteria.orderBy(criteriaBuilder.asc(root.get(Resource_.orderNumber)));
    
    TypedQuery<Resource> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }

  /**
   * Updates order number
   *
   * @param orderNumber order number
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateOrderNumber(Resource resource, Integer orderNumber, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setOrderNumber(orderNumber);
    return persist(resource);
  }

  /**
   * Updates data
   *
   * @param data data
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateData(Resource resource, String data, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setData(data);
    return persist(resource);
  }

  /**
   * Updates keycloakResorceId
   *
   * @param keycloakResorceId keycloakResorceId
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateKeycloakResorceId(Resource resource, UUID keycloakResorceId, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setKeycloakResorceId(keycloakResorceId);
    return persist(resource);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateName(Resource resource, String name, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setName(name);
    return persist(resource);
  }

  /**
   * Updates parent
   *
   * @param parent parent
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateParent(Resource resource, Resource parent, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setParent(parent);
    return persist(resource);
  }

  /**
   * Updates slug
   *
   * @param slug slug
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateSlug(Resource resource, String slug, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setSlug(slug);
    return persist(resource);
  }

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated resource
   */
  public Resource updateType(Resource resource, ResourceType type, UUID lastModifierId) {
    resource.setLastModifierId(lastModifierId);
    resource.setType(type);
    return persist(resource);
  }

}
