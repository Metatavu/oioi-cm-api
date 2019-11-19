package fi.metatavu.oioi.cm.rest.translate;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty;
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle;
import fi.metatavu.oioi.cm.resources.ResourceController;
import fi.metatavu.oioi.cm.wall.WallResource;

/**
 * Translator for WallResource 
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class WallResourceTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Resource, WallResource> {
  
  @Inject
  private ResourceController resourceController;

  @Override
  public WallResource translate(fi.metatavu.oioi.cm.persistence.model.Resource entity) {
    if (entity == null) {
      return null;
    }

    WallResource result = new WallResource();
    result.setSlug(entity.getSlug());
    result.setChildren(translate(resourceController.listResourcesByParent(entity)));
    result.setData(entity.getData());
    result.setName(entity.getName());
    result.setProperties(getProperties(entity));
    result.setStyles(getStyles(entity));
    result.setType(entity.getType());
    
    return result;
  }

  /**
   * Translates styles
   * 
   * @param entity resource
   * @return styles as key value pairs
   */
  private Map<String, String> getStyles(Resource entity) {
    return resourceController.listStyles(entity).stream().collect(Collectors.toMap(ResourceStyle::getKey, ResourceStyle::getValue));
  }

  /**
   * Translates styles
   * 
   * @param entity resource
   * @return styles as key value pairs
   */
  private Map<String, String> getProperties(Resource entity) {
    return resourceController.listProperties(entity).stream().collect(Collectors.toMap(ResourceProperty::getKey, ResourceProperty::getValue));
  }
  
}
