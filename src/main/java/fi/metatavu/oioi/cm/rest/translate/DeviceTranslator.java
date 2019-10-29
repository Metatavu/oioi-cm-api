package fi.metatavu.oioi.cm.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import fi.metatavu.oioi.cm.devices.DeviceController;
import fi.metatavu.oioi.cm.model.KeyValueProperty;
import fi.metatavu.oioi.cm.persistence.model.Device;

/**
 * Translator for Device REST entity 
 * 
 * @author Antti Leppä
 */
public class DeviceTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Device, fi.metatavu.oioi.cm.model.Device> {

  @Inject
  private DeviceController deviceController; 
  
  @Override
  public fi.metatavu.oioi.cm.model.Device translate(fi.metatavu.oioi.cm.persistence.model.Device entity) {
    fi.metatavu.oioi.cm.model.Device result = new fi.metatavu.oioi.cm.model.Device();    
    result.setId(entity.getId());
    result.setApiKey(entity.getApiKey());
    result.setName(entity.getName());
    result.setMetas(getMetas(entity));
    result.setCreatorId(entity.getCreatorId());
    result.setLastModifierId(entity.getLastModifierId());
    result.setCreatedAt(entity.getCreatedAt());
    result.setModifiedAt(entity.getModifiedAt());
    return result;
  }
  
  /**
   * Translates meta values to REST format
   * 
   * @param entity device
   * @return meta values as REST key value pairs
   */
  private List<KeyValueProperty> getMetas(Device entity) {
    return deviceController.listMetas(entity).stream().map(resourceProperty -> {
      KeyValueProperty result = new KeyValueProperty();
      result.setKey(resourceProperty.getKey());
      result.setValue(resourceProperty.getValue());
      return result;
    }).collect(Collectors.toList());
  }
  
}
