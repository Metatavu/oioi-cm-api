package fi.metatavu.oioi.cm.authz;

/**
 * Enumeration for authz resource scopes
 * 
 * @author Antti Leppä
 */
public enum ResourceScope {
  
  ACCESS ("resource:access"),
  MODIFY ("resource:modify"),
  DELETE ("resource:delete"),
  CREATE_RESOURCE ("resource:create-resource"),
  CREATE_FOLDER ("resource:create-folder");
  
  private String scope;
  
  private ResourceScope(String scope) {
    this.scope = scope;
  }
  
  public String getScope() {
    return scope;
  }
  
}
