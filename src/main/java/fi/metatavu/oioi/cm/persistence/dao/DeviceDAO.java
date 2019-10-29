package fi.metatavu.oioi.cm.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import fi.metatavu.oioi.cm.persistence.model.*;

/**
 * DAO class for Device
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class DeviceDAO extends AbstractDAO<Device> {

  /**
   * Creates new Device
   * 
   * @param id id
   * @param apiKey apiKey
   * @param name name
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created device
   */
  public Device create(UUID id, String apiKey, String name, UUID creatorId, UUID lastModifierId) {
    Device device = new Device();
    device.setApiKey(apiKey);
    device.setName(name);
    device.setId(id);
    device.setCreatorId(creatorId);
    device.setLastModifierId(lastModifierId);
    return persist(device);
  }

  /**
   * Updates apiKey
   *
   * @param apiKey apiKey
   * @param lastModifierId last modifier's id
   * @return updated device
   */
  public Device updateApiKey(Device device, String apiKey, UUID lastModifierId) {
    device.setLastModifierId(lastModifierId);
    device.setApiKey(apiKey);
    return persist(device);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated device
   */
  public Device updateName(Device device, String name, UUID lastModifierId) {
    device.setLastModifierId(lastModifierId);
    device.setName(name);
    return persist(device);
  }

}
