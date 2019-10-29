package fi.metatavu.oioi.cm.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import fi.metatavu.oioi.cm.model.KeyValueProperty;
import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.resources.ResourceController;

/**
 * Translator for Resource REST entity 
 * 
 * @author Antti Leppä
 */
public class ResourceTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Resource, fi.metatavu.oioi.cm.model.Resource> {
  
  @Inject
  private ResourceController resourceController;

  @Override
  public fi.metatavu.oioi.cm.model.Resource translate(fi.metatavu.oioi.cm.persistence.model.Resource entity) {
    fi.metatavu.oioi.cm.model.Resource result = new fi.metatavu.oioi.cm.model.Resource();    
    result.setId(entity.getId());
    result.setData(entity.getData());
    result.setName(entity.getName());
    result.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
    result.setSlug(entity.getSlug());
    result.setType(entity.getType());
    result.setProperties(getProperties(entity));
    result.setStyles(getStyles(entity));
    result.setCreatedAt(entity.getCreatedAt());
    result.setCreatorId(entity.getCreatorId());
    result.setLastModifierId(entity.getLastModifierId());
    result.setModifiedAt(entity.getModifiedAt());
    return result;
  }

  /**
   * Translates styles to REST format
   * 
   * @param entity resource
   * @return styles as REST key value pairs
   */
  private List<KeyValueProperty> getStyles(Resource entity) {
    return resourceController.listStyles(entity).stream().map(resourceStyle -> {
      KeyValueProperty result = new KeyValueProperty();
      result.setKey(resourceStyle.getKey());
      result.setValue(resourceStyle.getValue());
      return result;
    }).collect(Collectors.toList());
  }

  /**
   * Translates properties to REST format
   * 
   * @param entity resource
   * @return properties as REST key value pairs
   */
  private List<KeyValueProperty> getProperties(Resource entity) {
    return resourceController.listProperties(entity).stream().map(resourceProperty -> {
      KeyValueProperty result = new KeyValueProperty();
      result.setKey(resourceProperty.getKey());
      result.setValue(resourceProperty.getValue());
      return result;
    }).collect(Collectors.toList());
  }
  
}
