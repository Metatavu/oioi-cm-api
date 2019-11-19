package fi.metatavu.oioi.cm.wall;

import java.util.List;
import java.util.Map;

import fi.metatavu.oioi.cm.model.ResourceType;

public class WallResource {
  
  private String slug;

  private ResourceType type;

  private String name;

  private String data;

  private List<WallResource> children;

  private Map<String, String> styles;

  private Map<String, String> properties;

  public WallResource() {
    // Empty
  }

  public WallResource(String slug, ResourceType type, String name, String data, List<WallResource> children, Map<String, String> styles,
      Map<String, String> properties) {
    super();
    this.slug = slug;
    this.type = type;
    this.name = name;
    this.data = data;
    this.children = children;
    this.styles = styles;
    this.properties = properties;
  }
  
  public String getSlug() {
    return slug;
  }
  
  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
  
  public List<WallResource> getChildren() {
    return children;
  }
  
  public void setChildren(List<WallResource> children) {
    this.children = children;
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

}
