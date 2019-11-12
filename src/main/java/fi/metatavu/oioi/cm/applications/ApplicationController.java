package fi.metatavu.oioi.cm.applications;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.Application;
import fi.metatavu.oioi.cm.persistence.model.Device;
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
   * @param device device
   * @param creatorId creator id
   * @return created application
   */
  public Application createApplication(String name, Resource rootResource, Device device, UUID creatorId) {
    return applicationDAO.create(UUID.randomUUID(), name, rootResource, device, creatorId, creatorId);
  }

  /**
   * Find application by id
   * 
   * @param id application id
   * @return found application or null if not found
   */
  public Application findApplicationById(UUID id) {
    return applicationDAO.findById(id);
  }

  /**
   * Update application
   *
   * @param name name
   * @param lastModifierId last modifier id
   * @return updated application
   */
  public Application updateApplication(Application application, String name, UUID lastModifierId) {
    applicationDAO.updateName(application, name, lastModifierId);
    return application;
  }

  /**
   * Lists applications from device
   * 
   * @param device device to list the applications from
   * @return List of applications
   */
  public List<Application> listDeviceApplications(Device device) {
    return applicationDAO.listByDevice(device);
  }

  /**
   * Delete application
   */
  public void deleteApplication(Application application) {
    applicationDAO.delete(application);
  }
}
