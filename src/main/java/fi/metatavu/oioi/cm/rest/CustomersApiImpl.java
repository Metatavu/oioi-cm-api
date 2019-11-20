package fi.metatavu.oioi.cm.rest;

import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.oioi.cm.CustomersApi;
import fi.metatavu.oioi.cm.applications.ApplicationController;
import fi.metatavu.oioi.cm.customers.CustomerController;
import fi.metatavu.oioi.cm.devices.DeviceController;
import fi.metatavu.oioi.cm.medias.MediaController;
import fi.metatavu.oioi.cm.model.Application;
import fi.metatavu.oioi.cm.model.Customer;
import fi.metatavu.oioi.cm.model.Device;
import fi.metatavu.oioi.cm.model.Media;
import fi.metatavu.oioi.cm.model.MediaType;
import fi.metatavu.oioi.cm.model.Resource;
import fi.metatavu.oioi.cm.resources.ResourceController;
import fi.metatavu.oioi.cm.rest.translate.ApplicationTranslator;
import fi.metatavu.oioi.cm.rest.translate.CustomerTranslator;
import fi.metatavu.oioi.cm.rest.translate.DeviceTranslator;
import fi.metatavu.oioi.cm.rest.translate.MediaTranslator;
import fi.metatavu.oioi.cm.rest.translate.ResourceTranslator;

/**
 * REST - endpoints for customers
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@SuppressWarnings ("common-java:DuplicatedBlocks")
public class CustomersApiImpl extends AbstractApi implements CustomersApi {
  
  private static final String INVALID_PARENT_ID = "Invalid parent_id";

  @Inject
  private CustomerController customerController;
  
  @Inject
  private CustomerTranslator customerTranslator;

  @Inject
  private DeviceController deviceController;

  @Inject
  private DeviceTranslator deviceTranslator;

  @Inject
  private ApplicationController applicationController;

  @Inject
  private ApplicationTranslator applicationTranslator;

  @Inject
  private ResourceController resourceController;

  @Inject
  private ResourceTranslator resourceTranslator;

  @Inject
  private MediaController mediaController;

  @Inject
  private MediaTranslator mediaTranslator;

  /** APPLICATIONS */

  @Override
  public Response createApplication(UUID customerId, UUID deviceId, @Valid Application payload) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE);
    }

    UUID loggerUserId = getLoggerUserId();
    
    return createOk(applicationTranslator.translate(applicationController.createApplication(getAuthzClient(), customer, device, payload.getName(), loggerUserId)));
  }

  @Override
  public Response listApplications(UUID customerId, UUID deviceId) {

    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE);
    }

    return createOk(applicationTranslator.translate(applicationController.listDeviceApplications(device)));
  }

  @Override
  public Response findApplication(UUID customerId, UUID deviceId, UUID applicationId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Application applicationEntity = applicationController.findApplicationById(applicationId);
    if (applicationEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!applicationEntity.getDevice().getId().equals(device.getId())) {
      return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE);
    }

    return createOk(applicationTranslator.translate(applicationEntity));
  }

  @Override
  public Response updateApplication(UUID customerId, UUID deviceId, UUID applicationId, @Valid Application application) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Application applicationEntity = applicationController.findApplicationById(applicationId);
    if (applicationEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!applicationEntity.getDevice().getId().equals(device.getId())) {
      return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE);
    }

    UUID loggerUserId = getLoggerUserId();

    fi.metatavu.oioi.cm.persistence.model.Application updatedApplicationEntity = applicationController.updateApplication(applicationEntity, application.getName(), loggerUserId);

    return createOk(applicationTranslator.translate(updatedApplicationEntity));
  }

  @Override
  public Response deleteApplication(UUID customerId, UUID deviceId, UUID applicationId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createBadRequest(CUSTOMER_DEVICE_MISMATCH_MESSAGE);
    }

    fi.metatavu.oioi.cm.persistence.model.Application applicationEntity = applicationController.findApplicationById(applicationId);
    if (applicationEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!applicationEntity.getDevice().getId().equals(device.getId())) {
      return createBadRequest(APPLICATION_DEVICE_MISMATCH_MESSAGE);
    }
    
    applicationController.deleteApplication(getAuthzClient(), applicationEntity);

    return createNoContent();
  }

  /** CUSTOMERS */

  @Override
  public Response createCustomer(@Valid Customer customer) {
    UUID loggerUserId = getLoggerUserId();
    fi.metatavu.oioi.cm.persistence.model.Customer result = customerController.createCustomer(customer.getImageUrl(), customer.getName(), loggerUserId);
    return createOk(customerTranslator.translate(result));
  }

  @Override
  public Response listCustomers() {
    return createOk(customerController.listAllCustomers().stream().map(customerTranslator::translate).collect(Collectors.toList()));
  }

  @Override
  public Response findCustomer(UUID customerId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(customerTranslator.translate(customer));
  }

  @Override
  public Response updateCustomer(UUID customerId, @Valid Customer payload) {
    UUID loggerUserId = getLoggerUserId();
    
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    customerController.updateCustomer(customer, payload.getImageUrl(), payload.getName(), loggerUserId);
    
    return createOk(customerTranslator.translate(customer));
  }

  @Override
  public Response deleteCustomer(UUID customerId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    customerController.deleteCustomer(getAuthzClient(), customer);
    
    return createNoContent();
  }

  /** DEVICES */

  @Override
  public Response createDevice(UUID customerId, @Valid Device payload) {
    UUID loggerUserId = getLoggerUserId();
    
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    String apiKey = payload.getApiKey();
    String name = payload.getName();
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.createDevice(customer, apiKey, name, loggerUserId);
    deviceController.setDeviceMetas(device, payload.getMetas(), loggerUserId);
    
    return createOk(deviceTranslator.translate(device));
  }

  @Override
  public Response listDevices(UUID customerId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(deviceController.listCustomerDevices(customer).stream().map(deviceTranslator::translate).collect(Collectors.toList()));
  }

  @Override
  public Response findDevice(UUID customerId, UUID deviceId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(deviceTranslator.translate(device));
  }

  @Override
  public Response updateDevice(UUID customerId, UUID deviceId, @Valid Device payload) {
    UUID loggerUserId = getLoggerUserId();
    
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    deviceController.updateDevice(device, customer, payload.getApiKey(), payload.getName(), loggerUserId);
    deviceController.setDeviceMetas(device, payload.getMetas(), loggerUserId);
    
    return createOk(deviceTranslator.translate(device));
  }

  @Override
  public Response deleteDevice(UUID customerId, UUID deviceId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    deviceController.deleteDevice(getAuthzClient(), device);

    return createNoContent();
  }

  /** RESOURCES */

  @Override
  public Response createResource(UUID customerId, UUID deviceId, UUID applicationId, @Valid Resource payload) {
    UUID loggerUserId = getLoggerUserId();

    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!application.getDevice().getId().equals(device.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    UUID parentId = payload.getParentId();
    fi.metatavu.oioi.cm.persistence.model.Resource parent = resourceController.findResourceById(parentId);
    if (parent == null) {
      return createBadRequest(INVALID_PARENT_ID);
    }
    
    // TODO: parent permission?
    
    String data = payload.getData();
    String name = payload.getName();
    String slug = payload.getSlug();
    Integer orderNumber = payload.getOrderNumber();    
    fi.metatavu.oioi.cm.model.ResourceType type = payload.getType();         
    
    return createOk(resourceTranslator.translate(resourceController.createResource(getAuthzClient(), customer, device, application, orderNumber, parent, data, name, slug, type, payload.getProperties(), payload.getStyles(), loggerUserId)));
  }

  @Override
  public Response listResources(UUID customerId, UUID deviceId, UUID applicationId, UUID parentId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!application.getDevice().getId().equals(device.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Resource parent = resourceController.findResourceById(parentId);
    if (parent == null) {
      return createBadRequest(INVALID_PARENT_ID);
    }
    
    return createOk(resourceTranslator.translate(resourceController.listResourcesByParent(parent)));
  }

  @Override
  public Response findResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!application.getDevice().getId().equals(device.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Resource resource = resourceController.findResourceById(resourceId);
    if (resource == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!resourceController.isApplicationResource(application, resource)) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(resourceTranslator.translate(resourceController.findResourceById(resourceId)));
  }

  @Override
  public Response updateResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId, @Valid Resource payload) {
    UUID loggerUserId = getLoggerUserId();

    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!application.getDevice().getId().equals(device.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Resource resource = resourceController.findResourceById(resourceId);
    if (resource == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!resourceController.isApplicationResource(application, resource)) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    UUID parentId = payload.getParentId();    
    fi.metatavu.oioi.cm.persistence.model.Resource parent = resourceController.findResourceById(parentId);
    if (parent == null) {
      return createBadRequest(INVALID_PARENT_ID);
    }
    
    // TODO: parent permission?
    
    String data = payload.getData();
    String name = payload.getName();
    String slug = payload.getSlug();
    fi.metatavu.oioi.cm.model.ResourceType type = payload.getType();
    Integer orderNumber = payload.getOrderNumber();
    
    resourceController.setResourceProperties(resource, payload.getProperties(), loggerUserId);
    resourceController.setResourceStyles(resource, payload.getStyles(), loggerUserId);
    
    return createOk(resourceTranslator.translate(resourceController.updateResource(resource, orderNumber, data, name, parent, slug, type, loggerUserId)));
  }

  @Override
  public Response deleteResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!device.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!application.getDevice().getId().equals(device.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Resource resource = resourceController.findResourceById(resourceId);
    if (resource == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    if (!resourceController.isApplicationResource(application, resource)) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    resourceController.delete(getAuthzClient(), resource);
    
    return createNoContent();
  }

  /** MEDIAS */

  @Override
  public Response createMedia(UUID customerId, @Valid Media media) {
    UUID loggerUserId = getLoggerUserId();
    
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(mediaTranslator.translate(mediaController.createMedia(customer, media.getContentType(), media.getType(), media.getUrl(), loggerUserId)));
  }

  @Override
  public Response listMedias(UUID customerId, MediaType type) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(mediaTranslator.translate(mediaController.listMedias(customer, type)));
  }

  @Override
  public Response findMedia(UUID customerId, UUID mediaId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Media media = mediaController.findMediaById(mediaId);
    if (media == null || !media.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(mediaTranslator.translate(media));
  }

  @Override
  public Response updateMedia(UUID customerId, UUID mediaId, @Valid Media payload) {
    UUID loggerUserId = getLoggerUserId();
    
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Media media = mediaController.findMediaById(mediaId);
    if (media == null || !media.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    return createOk(mediaTranslator.translate(mediaController.updateMedia(media, payload.getContentType(), payload.getType(), payload.getUrl(), loggerUserId)));
  }

  @Override
  public Response deleteMedia(UUID customerId, UUID mediaId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    fi.metatavu.oioi.cm.persistence.model.Media media = mediaController.findMediaById(mediaId);
    if (media == null || !media.getCustomer().getId().equals(customer.getId())) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    mediaController.deleteMedia(media);
    
    return createNoContent();
  }
}
