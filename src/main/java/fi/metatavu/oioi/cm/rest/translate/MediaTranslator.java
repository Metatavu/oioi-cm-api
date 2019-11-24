package fi.metatavu.oioi.cm.rest.translate;

/**
 * Translator for Media REST entity 
 * 
 * @author Antti Leppä
 */
public class MediaTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Media, fi.metatavu.oioi.cm.model.Media> {

  @Override
  public fi.metatavu.oioi.cm.model.Media translate(fi.metatavu.oioi.cm.persistence.model.Media entity) {
    if (entity == null) {
      return null;
    }

    fi.metatavu.oioi.cm.model.Media result = new fi.metatavu.oioi.cm.model.Media();    
    result.setId(entity.getId());
    result.setContentType(entity.getContentType());
    result.setType(entity.getType());
    result.setUrl(entity.getUrl());
    result.setCreatorId(entity.getCreatorId());
    result.setLastModifierId(entity.getLastModifierId());
    result.setCreatedAt(entity.getCreatedAt());
    result.setModifiedAt(entity.getModifiedAt());
    return result;
  }
  
}
