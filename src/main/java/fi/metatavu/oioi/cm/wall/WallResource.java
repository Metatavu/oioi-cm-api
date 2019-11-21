package fi.metatavu.oioi.cm.wall;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import fi.metatavu.oioi.cm.model.ResourceType;

/**
 * REST model for wall resource export
 * 
 * @author Antti Leppä
 */
public class WallResource {
  
  private String slug;

  private ResourceType type;

  private String name;

  private String data;

  private List<WallResource> children;

  private Map<String, String> styles;

  private Map<String, String> properties;
  
  private OffsetDateTime modifiedAt;

  /**
   * Zero-argument constructor
   */
  public WallResource() {
    // Empty
  }

  /**
   * Constructor
   * 
   * @param slug slug
   * @param type type
   * @param name name
   * @param data data
   * @param children child resources
   * @param styles styles
   * @param properties properties
   * @param modifiedAt modified at
   */
  public WallResource(String slug, ResourceType type, String name, String data, List<WallResource> children, Map<String, String> styles,
      Map<String, String> properties, OffsetDateTime modifiedAt) {
    super();
    this.slug = slug;
    this.type = type;
    this.name = name;
    this.data = data;
    this.children = children;
    this.styles = styles;
    this.properties = properties;
    this.modifiedAt = modifiedAt;
  }
  
  /**
   * Returns slug
   * 
   * @return slug
   */
  public String getSlug() {
    return slug;
  }
  
  /**
   * Sets resource slug
   * 
   * @param slug resource slug
   */
  public void setSlug(String slug) {
    this.slug = slug;
  }

  /**
   * Returns resource type
   * 
   * @return resource type
   */
  public ResourceType getType() {
    return type;
  }

  /**
   * Sets resource type
   * 
   * @param type resource type
   */
  public void setType(ResourceType type) {
    this.type = type;
  }

  /**
   * Sets resource name
   * 
   * @return resource name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets resource name
   * 
   * @param name resource name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets resource data
   * 
   * @return resource data
   */
  public String getData() {
    return data;
  }

  /**
   * Returns resource data
   * 
   * @param data resource data
   */
  public void setData(String data) {
    this.data = data;
  }
  
  /**
   * Returns resource children
   * 
   * @return resource children
   */
  public List<WallResource> getChildren() {
    return children;
  }
  
  /**
   * Sets resource children
   * 
   * @param children resource children
   */
  public void setChildren(List<WallResource> children) {
    this.children = children;
  }

  /**
   * Returns resource styles
   * 
   * @return resource styles
   */
  public Map<String, String> getStyles() {
    return styles;
  }

  /**
   * Sets resource styles
   * 
   * @param styles resource styles
   */
  public void setStyles(Map<String, String> styles) {
    this.styles = styles;
  }

  /**
   * Returns resource properties
   * 
   * @return resource properties
   */
  public Map<String, String> getProperties() {
    return properties;
  }

  /**
   * Sets resource properties
   * 
   * @param properties resource properties
   */
  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }
  
  /**
   * Returns last modification time
   * 
   * @return last modification time
   */
  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }
  
  /**
   * Sets last modification time
   * 
   * @param modifiedAt last modification time
   */
  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

}
