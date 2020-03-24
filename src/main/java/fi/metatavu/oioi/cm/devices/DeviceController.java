package fi.metatavu.oioi.cm.devices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.authorization.client.AuthzClient;

import fi.metatavu.oioi.cm.applications.ApplicationController;
import fi.metatavu.oioi.cm.model.KeyValueProperty;
import fi.metatavu.oioi.cm.persistence.dao.DeviceDAO;
import fi.metatavu.oioi.cm.persistence.dao.DeviceMetaDAO;
import fi.metatavu.oioi.cm.persistence.model.Customer;
import fi.metatavu.oioi.cm.persistence.model.Device;
import fi.metatavu.oioi.cm.persistence.model.DeviceMeta;

/**
 * Controller for Device
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DeviceController {

  @Inject
  private DeviceDAO deviceDAO;

  @Inject
  private DeviceMetaDAO deviceMetaDAO;

  @Inject
  private ApplicationController applicationController;
  
  /**
   * Create device
   *
   * @param customer customer
   * @param apiKey apiKey
   * @param name name
   * @param imageUrl device image URL
   * @param creatorId creator id
   * @return created device
   */
  public Device createDevice(Customer customer, String apiKey, String name, String imageUrl, UUID creatorId) {
    return deviceDAO.create(UUID.randomUUID(), customer, apiKey, name, imageUrl, creatorId, creatorId);
  }
  
  /**
   * Find device by id
   * 
   * @param id device id
   * @return found device or null if not found
   */
  public Device findDeviceById(UUID id) {
    return deviceDAO.findById(id);
  }

  /**
   * List devices by customer
   * 
   * @param customer customer
   * @return list of devices
   */
  public List<Device> listCustomerDevices(Customer customer) {
    return deviceDAO.listByCustomer(customer);
  }

  /**
   * Update device
   *
   * @param customer customer
   * @param apiKey apiKey
   * @param name name
   * @param lastModifierId last modifier id
   * @return updated device
   */
  public Device updateDevice(Device device, Customer customer, String apiKey, String name, String imageUrl, UUID lastModifierId) {
    deviceDAO.updateCustomer(device, customer, lastModifierId);
    deviceDAO.updateApiKey(device, apiKey, lastModifierId);
    deviceDAO.updateName(device, name, lastModifierId);
    deviceDAO.updateImageUrl(device, imageUrl, lastModifierId);
    return device;
  }

  /**
   * Delete device a device
   * 
   * @param device device
   * @param authzClient authzClient
   */
  public void deleteDevice(AuthzClient authzClient, Device device) {
    applicationController.listDeviceApplications(device).forEach(application -> applicationController.deleteApplication(authzClient, application));
    listMetas(device).forEach(this::deleteDeviceMeta);
    deviceDAO.delete(device);
  }
  
  /**
   * Sets device metas
   * 
   * @param device device 
   * @param metas metas
   * @param lastModifierId modifier
   */
  public void setDeviceMetas(Device device, List<KeyValueProperty> metas, UUID lastModifierId) {
    Map<String, DeviceMeta> existingMetas = new HashMap<>(listMetas(device).stream().collect(Collectors.toMap(DeviceMeta::getKey, self -> self)));
    
    for (KeyValueProperty meta : metas) {
      DeviceMeta existingMeta = existingMetas.remove(meta.getKey());
      if (existingMeta == null) {
        createDeviceMeta(device, meta.getKey(), meta.getValue(), lastModifierId);
      } else {
        updateDeviceMeta(existingMeta, meta.getKey(), meta.getValue(), lastModifierId);
      }
    }
    
    existingMetas.values().forEach(this::deleteDeviceMeta);
  }

   /**
    * Lists device meta values
    * 
    * @param device device
    * @return device meta values
    */
   public List<DeviceMeta> listMetas(Device device) {
     return deviceMetaDAO.listByDevice(device);
   }

   /**
     * Create deviceMeta
     *
     * @param device device
     * @param key key
     * @param value value
     * @param creatorId creator id
     * @return created deviceMeta
     */
    private DeviceMeta createDeviceMeta(Device device, String key, String value, UUID creatorId) {
      return deviceMetaDAO.create(UUID.randomUUID(), device, key, value, creatorId, creatorId);
    }
    
    /**
     * Update deviceMeta
     *
     * @param deviceMeta device meta
     * @param key key
     * @param value value
     * @param lastModifierId last modifier id
     * @return updated deviceMeta
     */
    private DeviceMeta updateDeviceMeta(DeviceMeta deviceMeta, String key, String value, UUID lastModifierId) {
      deviceMetaDAO.updateKey(deviceMeta, key, lastModifierId);
      deviceMetaDAO.updateValue(deviceMeta, value, lastModifierId);
      return deviceMeta;
    }
   
   /**
    * Deletes a device meta
    * 
    * @param deviceMeta device meta
    */
   private void deleteDeviceMeta(DeviceMeta deviceMeta) {
     deviceMetaDAO.delete(deviceMeta);
   }
}