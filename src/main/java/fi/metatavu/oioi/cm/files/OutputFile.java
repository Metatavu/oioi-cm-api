package fi.metatavu.oioi.cm.files;

import java.net.URI;

/**
 * Class representing a persisted file
 * 
 * @author Antti Leppä
 */
public class OutputFile {

  private FileMeta meta;
  private URI uri;

  /**
   * Zero argument constructor
   */
  public OutputFile() {
    // Empty
  }
  
  /**
   * Constructor
   * 
   * @param folder folder
   * @param meta file meta
   * @param uri file uri
   */
  public OutputFile(FileMeta meta, URI uri) {
    super();
    this.meta = meta;
    this.uri = uri;
  }
  
  /**
   * Returns uri
   * 
   * @return uri
   */
  public URI getUri() {
    return uri;
  }
  
  /**
   * Sets URI
   * 
   * @param uri URI
   */
  public void setUri(URI uri) {
    this.uri = uri;
  }

  /**
   * Returns meta
   * 
   * @return meta
   */
  public FileMeta getMeta() {
    return meta;
  }
  
  /**
   * Sets meta
   * 
   * @param meta meta
   */
  public void setMeta(FileMeta meta) {
    this.meta = meta;
  }

}
