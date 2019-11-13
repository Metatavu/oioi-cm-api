package fi.metatavu.oioi.cm.files.storage;

/**
 * Exception for file storage operation failures
 * 
 * @author Antti Leppä
 * @author Heiki Kurhinen
 */
public class FileStorageException extends Exception {

  private static final long serialVersionUID = 3617365538517872694L;
  
  /**
   * Constructor
   * 
   * @param reason exception reason
   */
  public FileStorageException(String reason) {
    super(reason);
  }

  /**
   * Constructor
   * 
   * @param cause exception cause
   */
  public FileStorageException(Throwable cause) {
    super(cause);
  }

}
