package fi.metatavu.oioi.cm.wall;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST model for wall device export
 * 
 * @author Heikki Kurhinen <heikki.kurhinen@metatavu.fi>
 */
@RegisterForReflection
public class WallDevice {

  private String name;

  private List<WallDeviceApplication> applications;

  private UUID activeApplication;

  private OffsetDateTime modifiedAt;

  public WallDevice() {
    //Default constructor
  }

  public WallDevice(String name, List<WallDeviceApplication> applications, UUID activeApplication,
      OffsetDateTime modifiedAt) {
    this.name = name;
    this.applications = applications;
    this.activeApplication = activeApplication;
    this.modifiedAt = modifiedAt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<WallDeviceApplication> getApplications() {
    return applications;
  }

  public void setApplications(List<WallDeviceApplication> applications) {
    this.applications = applications;
  }

  public UUID getActiveApplication() {
    return activeApplication;
  }

  public void setActiveApplication(UUID activeApplication) {
    this.activeApplication = activeApplication;
  }

  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

}