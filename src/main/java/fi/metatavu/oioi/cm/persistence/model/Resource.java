package fi.metatavu.oioi.cm.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.oioi.cm.model.ResourceType;

/**
 * JPA entity representing reource
 *
 * @author Antti Leppä 
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Resource   {

  @Id
  private UUID id;
  
  @Column (nullable = false)
  private UUID keycloakResorceId;
  
  @ManyToOne
  private Resource parent;
  
  @Column (nullable = false)
  private ResourceType type;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String name;
  
  @Column (nullable = false)
  @Lob
  private String data;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String slug;
  
  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @Column (nullable = false)
  private OffsetDateTime modifiedAt;

  @Column (nullable = false)
  private UUID creatorId;
  
  @Column (nullable = false)
  private UUID lastModifierId;

  /**
   * Returns id
   * 
   * @return id
   */
  public UUID getId() {
    return id;
  }
  
  /**
   * Sets id
   * 
   * @param id id
   */
  public void setId(UUID id) {
    this.id = id;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public UUID getKeycloakResorceId() {
    return keycloakResorceId;
  }
  
  public void setKeycloakResorceId(UUID keycloakResorceId) {
    this.keycloakResorceId = keycloakResorceId;
  }
  
  /**
   * Returns name
   * 
   * @return name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets name
   * 
   * @param name name
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Returns parent
   * 
   * @return parent
   */
  public Resource getParent() {
    return parent;
  }
  
  /**
   * Sets parent
   * 
   * @param parent parent
   */
  public void setParent(Resource parent) {
    this.parent = parent;
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
   * Sets slug
   * 
   * @param slug slug
   */
  public void setSlug(String slug) {
    this.slug = slug;
  }
  
  /**
   * Returns type
   * 
   * @return type
   */
  public ResourceType getType() {
    return type;
  }
  
  /**
   * Sets type
   * 
   * @param type type
   */
  public void setType(ResourceType type) {
    this.type = type;
  }
  
  /**
   * Returns creation time of the entity
   * 
   * @return creation time of the entity
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
  
  /**
   * Sets creator id
   * 
   * @param creatorId creator id
   */
  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }
  
  /**
   * Returns creator id
   * 
   * @return creator id
   */
  public UUID getCreatorId() {
    return creatorId;
  }
  
  /**
   * Sets last modifier id
   * 
   * @param lastModifierId last modifier id
   */
  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }
  
  /**
   * Returns last modifier id
   * 
   * @return last modifier id
   */
  public UUID getLastModifierId() {
    return lastModifierId;
  }
  
  /**
   * Setter for createdAt property. 
   * 
   * Method is primarily intended to be used by JPA events
   * 
   * @param createdAt created at
   */
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
  
  /**
   * Returns last modification time of the entity
   * 
   * @return last modification time of the entity
   */
  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  /**
   * Setter for modifiedAt property. 
   * 
   * Method is primarily intended to be used by JPA events
   * 
   * @param modifiedAt modified at
   */
  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  /**
   * JPA pre-persist event handler
   */
  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
    setModifiedAt(OffsetDateTime.now());
  }

  /**
   * JPA pre-update event handler
   */
  @PreUpdate
  public void onUpdate() {
    setModifiedAt(OffsetDateTime.now());
  }
  

}