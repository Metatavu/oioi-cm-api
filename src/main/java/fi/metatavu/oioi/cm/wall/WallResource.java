package fi.metatavu.oioi.cm.wall;

import java.util.Map;

import fi.metatavu.oioi.cm.model.ResourceType;

public class WallResource {

  private ResourceType type;

  private String name;

  private String data;

  private Map<String, WallResource> children;

  private Map<String, String> styles;

  private Map<String, String> properties;

  public WallResource() {
    // Empty
  }

  public WallResource(ResourceType type, String name, String data, Map<String, WallResource> children, Map<String, String> styles,
      Map<String, String> properties) {
    super();
    this.type = type;
    this.name = name;
    this.data = data;
    this.children = children;
    this.styles = styles;
    this.properties = properties;
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

  public Map<String, WallResource> getChildren() {
    return children;
  }

  public void setChildren(Map<String, WallResource> children) {
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
