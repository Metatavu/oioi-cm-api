package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
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
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created application
   */
  public Application create(UUID id, String name, Resource rootResource, UUID creatorId, UUID lastModifierId) {
    Application application = new Application();
    application.setName(name);
    application.setRootResource(rootResource);
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

}
