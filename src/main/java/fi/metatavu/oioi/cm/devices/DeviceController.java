package fi.metatavu.oioi.cm.devices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.Device;
import fi.metatavu.oioi.cm.persistence.model.DeviceMeta;
import fi.metatavu.oioi.cm.persistence.dao.DeviceDAO;
import fi.metatavu.oioi.cm.persistence.dao.DeviceMetaDAO;

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
  
 /**
   * Create device
   *
   * @param apiKey apiKey
   * @param name name
   * @param creatorId creator id
   * @return created device
   */
  public Device createDevice(Device device, String apiKey, String name, UUID creatorId) {
    return deviceDAO.create(UUID.randomUUID(), apiKey, name, creatorId, creatorId);
  }

  /**
   * Update device
   *
   * @param apiKey apiKey
   * @param name name
   * @param lastModifierId last modifier id
   * @return updated device
   */
  public Device updateDevice(Device device, String apiKey, String name, UUID lastModifierId) {
    deviceDAO.updateApiKey(device, apiKey, lastModifierId);
    deviceDAO.updateName(device, name, lastModifierId);
    return device;
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
   public DeviceMeta createDeviceMeta(DeviceMeta deviceMeta, Device device, String key, String value, UUID creatorId) {
     return deviceMetaDAO.create(UUID.randomUUID(), device, key, value, creatorId, creatorId);
   }
   
   /**
    * Update deviceMeta
    *
    * @param device device
    * @param key key
    * @param value value
    * @param lastModifierId last modifier id
    * @return updated deviceMeta
    */
   public DeviceMeta updateDeviceMeta(DeviceMeta deviceMeta, Device device, String key, String value, UUID lastModifierId) {
     deviceMetaDAO.updateDevice(deviceMeta, device, lastModifierId);
     deviceMetaDAO.updateKey(deviceMeta, key, lastModifierId);
     deviceMetaDAO.updateValue(deviceMeta, value, lastModifierId);
     return deviceMeta;
   }
}