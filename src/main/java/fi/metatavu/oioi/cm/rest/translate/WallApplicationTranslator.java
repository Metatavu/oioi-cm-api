package fi.metatavu.oioi.cm.rest.translate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.oioi.cm.wall.WallApplication;
import fi.metatavu.oioi.cm.wall.WallResource;

/**
 * Translator for WallApplication
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class WallApplicationTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Application, WallApplication> {
  
  @Inject
  private WallResourceTranslator wallResourceTranslator;

  @Override
  public WallApplication translate(fi.metatavu.oioi.cm.persistence.model.Application entity) {
    if (entity == null) {
      return null;
    }
    
    WallResource root = wallResourceTranslator.translate(entity.getRootResource());

    WallApplication result = new WallApplication();
    result.setModifiedAt(getModifiedAt(root));
    result.setRoot(root);
    
    return result;
  }

  /**
   * Returns most recent wall resource modification time
   * 
   * @param root root resource
   * @return most recent wall resource modification time
   */
  private OffsetDateTime getModifiedAt(WallResource root) {
    List<OffsetDateTime> modificationTimes = new ArrayList<>();
    getModificationTimes(root, modificationTimes);
    Collections.sort(modificationTimes, (a, b) -> b.compareTo(a));    
    return modificationTimes.get(0);
  }
  
  /**
   * Recursively collects modification times from wall resources
   * 
   * @param resource resource
   * @param result collected times
   */
  private void getModificationTimes(WallResource resource, List<OffsetDateTime> result) {
    result.add(resource.getModifiedAt());
    resource.getChildren().forEach(child -> getModificationTimes(child, result));
  }

  
}
