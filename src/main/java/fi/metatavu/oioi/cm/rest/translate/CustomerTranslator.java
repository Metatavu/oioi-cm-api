package fi.metatavu.oioi.cm.rest.translate;

/**
 * Translator for Customer REST entity 
 * 
 * @author Antti Leppä
 */
public class CustomerTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Customer, fi.metatavu.oioi.cm.model.Customer> {

  @Override
  public fi.metatavu.oioi.cm.model.Customer translate(fi.metatavu.oioi.cm.persistence.model.Customer entity) {
    fi.metatavu.oioi.cm.model.Customer result = new fi.metatavu.oioi.cm.model.Customer();    
    result.setId(entity.getId());
    result.setImageUrl(entity.getImageUrl());
    result.setName(entity.getName());
    result.setCreatorId(entity.getCreatorId());
    result.setLastModifierId(entity.getLastModifierId());
    result.setCreatedAt(entity.getCreatedAt());
    result.setModifiedAt(entity.getModifiedAt());
    return result;
  }
  
}
