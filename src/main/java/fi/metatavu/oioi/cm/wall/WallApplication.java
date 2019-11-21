package fi.metatavu.oioi.cm.wall;

import java.time.OffsetDateTime;

/**
 * REST model for wall application export
 * 
 * @author Antti Leppä
 */
public class WallApplication {

  private WallResource root;
  private OffsetDateTime modifiedAt;

  /**
   * Zero-argument constructor
   */
  public WallApplication() {
    // Empty
  }

  /**
   * Constructor
   * 
   * @param root wall root resource
   * @param modifiedAt last resource modification time
   */
  public WallApplication(WallResource root, OffsetDateTime modifiedAt) {
    super();
    this.modifiedAt = modifiedAt;
    this.root = root;
  }
  
  /**
   * Returns resource modified at value
   * 
   * @return resource modified at value
   */
  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }
  
  /**
   * Sets resource modified at value
   * 
   * @param modifiedAt resource modified 
   */
  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
  
  /**
   * Returns wall root resource
   * 
   * @return wall root resource
   */
  public WallResource getRoot() {
    return root;
  }
  
  /**
   * Sets the wall root resource
   * 
   * @param root wall root resource
   */
  public void setRoot(WallResource root) {
    this.root = root;
  }

}
