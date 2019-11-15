package fi.metatavu.oioi.cm.authz;

/**
 * Enumeration for authz resource types
 * 
 * @author Antti Leppä
 */
public enum ResourceType {
  
  RESOURCE ("resource");
  
  private String type;
  
  private ResourceType(String type) {
    this.type = type;
  }
  
  public String getType() {
    return type;
  }
  
}
