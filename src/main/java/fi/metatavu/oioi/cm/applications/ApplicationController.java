package fi.metatavu.oioi.cm.applications;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.Application;
import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.persistence.dao.ApplicationDAO;

/**
 * Controller for Application
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ApplicationController {

  @Inject
  private ApplicationDAO applicationDAO;

  /**
   * Create application
   *
   * @param name name
   * @param rootResource rootResource
   * @param creatorId creator id
   * @return created application
   */
  public Application createApplication(Application application, String name, Resource rootResource, UUID creatorId) {
    return applicationDAO.create(UUID.randomUUID(), name, rootResource, creatorId, creatorId);
  }

  /**
   * Update application
   *
   * @param name name
   * @param rootResource rootResource
   * @param lastModifierId last modifier id
   * @return updated application
   */
  public Application updateApplication(Application application, String name, Resource rootResource, UUID lastModifierId) {
    applicationDAO.updateName(application, name, lastModifierId);
    applicationDAO.updateRootResource(application, rootResource, lastModifierId);
    return application;
  }
}
