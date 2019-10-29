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


/**
 * JPA entity representing device meta
 *
 * @author Antti Leppä 
 * @author Heikki Kurhinen
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DeviceMeta {

  @Id
  private UUID id;
  
  @NotNull
  @NotEmpty
  private String key;
  
  @NotNull
  @NotEmpty
  @Lob
  private String value;

  @ManyToOne (optional = false)
  private Device device;
  
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
  
  /**
   * Returns device
   * 
   * @return device
   */
  public Device getDevice() {
    return device;
  }
  
  /**
   * Sets a device
   * 
   * @param device device
   */
  public void setDevice(Device device) {
    this.device = device;
  }
  
  /**
   * Returns a key
   * 
   * @return key
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Sets a key
   * 
   * @param key key
   */
  public void setKey(String key) {
    this.key = key;
  }
  
  /**
   * Returns value
   * 
   * @return value
   */
  public String getValue() {
    return value;
  }
  
  /**
   * Sets a value
   * 
   * @param value value
   */
  public void setValue(String value) {
    this.value = value;
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
   * Returns creation time of the entity
   * 
   * @return creation time of the entity
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
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