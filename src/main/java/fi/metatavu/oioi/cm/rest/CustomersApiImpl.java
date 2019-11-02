package fi.metatavu.oioi.cm.rest;

import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.oioi.cm.CustomersApi;
import fi.metatavu.oioi.cm.customers.CustomerController;
import fi.metatavu.oioi.cm.model.Application;
import fi.metatavu.oioi.cm.model.Customer;
import fi.metatavu.oioi.cm.model.Device;
import fi.metatavu.oioi.cm.model.Media;
import fi.metatavu.oioi.cm.model.MediaType;
import fi.metatavu.oioi.cm.model.Resource;
import fi.metatavu.oioi.cm.rest.translate.CustomerTranslator;

/**
 * REST - endpoints for customers
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class CustomersApiImpl extends AbstractApi implements CustomersApi {

  @Inject
  private CustomerController customerController;
  
  @Inject
  private CustomerTranslator customerTranslator;

  @Override
  public Response createApplication(UUID customerId, UUID deviceId, @Valid Application application) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createCustomer(@Valid Customer customer) {
    UUID loggerUserId = getLoggerUserId();
    fi.metatavu.oioi.cm.persistence.model.Customer result = customerController.createCustomer(customer.getImageUrl(), customer.getName(), loggerUserId);
    return createOk(customerTranslator.translate(result));
  }

  @Override
  public Response createDevice(UUID customerId, @Valid Device device) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createMedia(UUID customerId, @Valid Media media) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createResource(UUID customerId, UUID deviceId, UUID applicationId, @Valid Resource resource, UUID parentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteApplication(UUID customerId, UUID deviceId, UUID applicationId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteCustomer(UUID customerId) {
    fi.metatavu.oioi.cm.persistence.model.Customer customer = customerController.findCustomerById(customerId);
    if (customer == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }
    
    customerController.deleteCustomer(customer);
    
    return createNoContent();
  }

  @Override
  public Response deleteDevice(UUID customerId, UUID deviceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteMedia(UUID customerId, UUID mediaId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findApplication(UUID customerId, UUID deviceId, UUID applicationId) {
    // TODO Auto-generated method stub
    return null;
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
  public Response findDevice(UUID customerId, UUID deviceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findMedia(UUID customerId, UUID mediaId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listApplications(UUID customerId, UUID deviceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listCustomers() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listDevices(UUID customerId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listMedias(UUID customerId, MediaType type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listResources(UUID customerId, UUID deviceId, UUID applicationId, UUID parentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateApplication(UUID customerId, UUID deviceId, UUID applicationId, @Valid Application application) {
    // TODO Auto-generated method stub
    return null;
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
  public Response updateDevice(UUID customerId, UUID deviceId, @Valid Device device) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateMedia(UUID customerId, UUID mediaId, @Valid Media media) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateResource(UUID customerId, UUID deviceId, UUID applicationId, UUID resourceId, @Valid Resource resource) {
    // TODO Auto-generated method stub
    return null;
  }

}
