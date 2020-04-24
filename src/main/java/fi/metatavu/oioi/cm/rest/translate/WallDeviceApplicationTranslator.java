package fi.metatavu.oioi.cm.rest.translate;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.oioi.cm.persistence.model.Resource;
import fi.metatavu.oioi.cm.persistence.model.ResourceProperty;
import fi.metatavu.oioi.cm.persistence.model.ResourceStyle;
import fi.metatavu.oioi.cm.resources.ResourceController;
import fi.metatavu.oioi.cm.wall.WallDeviceApplication;

/**
 * Translator for wall device application
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen <heikki.kurhinen@metatavu.fi>
 */
@ApplicationScoped
public class WallDeviceApplicationTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Application, WallDeviceApplication> {
  
  @Inject
  private ResourceController resourceController;

  @Override
  public WallDeviceApplication translate(fi.metatavu.oioi.cm.persistence.model.Application entity) {
    if (entity == null) {
      return null;
    }

    WallDeviceApplication result = new WallDeviceApplication();
    result.setId(entity.getId());
    result.setName(entity.getName());

    Resource rootResource = entity.getRootResource();
    OffsetDateTime modifiedAt = entity.getModifiedAt();
    if (rootResource != null) {
      OffsetDateTime resourceModifiedAt = rootResource.getModifiedAt();
      if (resourceModifiedAt.isAfter(modifiedAt)) {
        modifiedAt = resourceModifiedAt;
      }
      result.setProperties(getProperties(rootResource));
      result.setStyles(getStyles(rootResource));
    }

    result.setModifiedAt(modifiedAt);
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
