package fi.metatavu.oioi.cm.applications;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.authorization.client.AuthzClient;

import fi.metatavu.oioi.cm.model.ResourceType;
import fi.metatavu.oioi.cm.persistence.dao.ApplicationDAO;
import fi.metatavu.oioi.cm.persistence.model.Application;
import fi.metatavu.oioi.cm.persistence.model.Device;
import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.resources.ResourceController;

/**
 * Controller for Application
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ApplicationController {

  @Inject
  private ResourceController resourceController;

  @Inject
  private ApplicationDAO applicationDAO;

  /**
   * Create application
   * 
   * @param authzClient authz client
   * @param customer customer
   * @param device device
   * @param name name
   * @param creatorId creator id
   * @return created application
   */
  public Application createApplication(AuthzClient authzClient, fi.metatavu.oioi.cm.persistence.model.Customer customer, fi.metatavu.oioi.cm.persistence.model.Device device, String name, UUID creatorId) {
    UUID applicationId = UUID.randomUUID();
    Resource rootResource = resourceController.createResource(authzClient, customer, device, applicationId, 0, null, null, name, "[root]", ResourceType.ROOT, creatorId);
    return applicationDAO.create(applicationId, name, rootResource, device, creatorId, creatorId);
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
   * 
   * @param authzClient authz client
   * @param application application
   */
  public void deleteApplication(AuthzClient authzClient, Application application) {
    Resource rootResource = application.getRootResource();
    applicationDAO.delete(application);
    resourceController.delete(authzClient, rootResource);
  }
}
