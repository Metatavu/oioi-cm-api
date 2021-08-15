package fi.metatavu.oioi.cm.wall;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * REST model for wall device export
 * 
 * @author Heikki Kurhinen <heikki.kurhinen@metatavu.fi>
 */
@RegisterForReflection
public class WallDeviceApplication {

  private UUID id;

  private String name;

  private Map<String, String> styles;

  private Map<String, String> properties;

  private OffsetDateTime modifiedAt;

  public WallDeviceApplication() {
    //Default constructor
  }

  public WallDeviceApplication(UUID id, String name, Map<String, String> styles, Map<String, String> properties,
      OffsetDateTime modifiedAt) {
    this.id = id;
    this.name = name;
    this.styles = styles;
    this.properties = properties;
    this.modifiedAt = modifiedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getStyles() {
    return styles;
  }

  public void setStyles(Map<String, String> styles) {
    this.styles = styles;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

}