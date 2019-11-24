package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for Application
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ApplicationDAO extends AbstractDAO<Application> {

  /**
   * Creates new Application
   * 
   * @param id id
   * @param name name
   * @param rootResource rootResource
   * @param device device
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created application
   */
  public Application create(UUID id, String name, Resource rootResource, Device device, UUID creatorId, UUID lastModifierId) {
    Application application = new Application();
    application.setName(name);
    application.setRootResource(rootResource);
    application.setDevice(device);
    application.setId(id);
    application.setCreatorId(creatorId);
    application.setLastModifierId(lastModifierId);
    return persist(application);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated application
   */
  public Application updateName(Application application, String name, UUID lastModifierId) {
    application.setLastModifierId(lastModifierId);
    application.setName(name);
    return persist(application);
  }

  /**
   * Updates rootResource
   *
   * @param rootResource rootResource
   * @param lastModifierId last modifier's id
   * @return updated application
   */
  public Application updateRootResource(Application application, Resource rootResource, UUID lastModifierId) {
    application.setLastModifierId(lastModifierId);
    application.setRootResource(rootResource);
    return persist(application);
  }

  /**
   * Lists applications by device
   * 
   * @param device device
   * @return List of applications
   */
  public List<Application> listByDevice(Device device) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Application> criteria = criteriaBuilder.createQuery(Application.class);
    Root<Application> root = criteria.from(Application.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Application_.device), device));
    
    return entityManager.createQuery(criteria).getResultList();
  }

}
