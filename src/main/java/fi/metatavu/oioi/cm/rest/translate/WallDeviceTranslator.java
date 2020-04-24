package fi.metatavu.oioi.cm.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.oioi.cm.applications.ApplicationController;
import fi.metatavu.oioi.cm.wall.WallDevice;
import fi.metatavu.oioi.cm.wall.WallDeviceApplication;

/**
 * Translator for WallDeviceApplication
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen <heikki.kurhinen@metatavu.fi>
 */
@ApplicationScoped
public class WallDeviceTranslator extends AbstractTranslator<fi.metatavu.oioi.cm.persistence.model.Device, WallDevice> {
  
  @Inject
  private WallDeviceApplicationTranslator wallDeviceApplicationTranslator;

  @Inject
  private ApplicationController applicationController;

  @Override
  public WallDevice translate(fi.metatavu.oioi.cm.persistence.model.Device entity) {
    if (entity == null) {
      return null;
    }
    
    List<WallDeviceApplication> applications = applicationController.listDeviceApplications(entity)
      .stream()
      .map(wallDeviceApplicationTranslator::translate)
      .collect(Collectors.toList());

    WallDevice result = new WallDevice();
    result.setModifiedAt(entity.getModifiedAt());
    result.setName(entity.getName());
    result.setApplications(applications);
    return result;
  }

}
