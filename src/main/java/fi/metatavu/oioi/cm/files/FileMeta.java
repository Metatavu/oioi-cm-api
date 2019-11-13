package fi.metatavu.oioi.cm.files;

/**
 * Class describing meta data of a uploaded file
 * 
 * @author Antti Leppä
 */
public class FileMeta {

  private String contentType;
  private String fileName;
  
  /**
   * Zero argument constructor
   */
  public FileMeta() {
    // Empty
  }
  
  /**
   * Constructor
   * 
   * @param contentType file's content type
   * @param fileName file's original name
   */
  public FileMeta(String contentType, String fileName) {
    super();
    this.contentType = contentType;
    this.fileName = fileName;
  }

  /**
   * Returns content type
   * 
   * @return content type
   */
  public String getContentType() {
    return contentType;
  }
  
  /**
   * Sets content type
   * 
   * @param contentType content type
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  
  /**
   * Returns file name
   * 
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }
  
  /**
   * Sets file name
   * 
   * @param fileName file name
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}

