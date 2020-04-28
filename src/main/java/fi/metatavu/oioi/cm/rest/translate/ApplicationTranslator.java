package fi.metatavu.oioi.cm.rest.translate;

import javax.enterprise.context.ApplicationScoped;

/**
 * Translator for Application REST entity 
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class ApplicationTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Application, fi.metatavu.oioi.cm.model.Application> {

  @Override
  public fi.metatavu.oioi.cm.model.Application translate(fi.metatavu.oioi.cm.persistence.model.Application entity) {
    if (entity == null) {
      return null;
    }

    fi.metatavu.oioi.cm.model.Application result = new fi.metatavu.oioi.cm.model.Application();
    result.setId(entity.getId());
    result.setName(entity.getName());
    result.setRootResourceId(entity.getRootResource() != null ? entity.getRootResource().getId() : null);
    result.setCreatedAt(entity.getCreatedAt());
    result.setModifiedAt(entity.getModifiedAt());
    result.setCreatorId(entity.getCreatorId());
    result.setLastModifierId(entity.getLastModifierId());
    return result;
  }
  
}
