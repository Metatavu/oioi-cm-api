package fi.metatavu.oioi.cm.files.storage;

import fi.metatavu.oioi.cm.files.InputFile;
import fi.metatavu.oioi.cm.files.OutputFile;

/**
 * Interface for describing a single file storage
 * 
 * @author Antti Leppä
 */
public interface FileStorageProvider {

  /**
   * Returns file storage id
   * 
   * @return file storage id
   */
  public String getId();
  
  /**
   * Initializes the file storage provider
   * 
   * @throws FileStorageException thrown when initialization fails
   */
  public void init() throws FileStorageException;

  /**
   * Stores a file
   * 
   * @param inputFile input file data
   * @return stored file
   * @throws FileStorageException thrown when storaging the file fails
   */
  public OutputFile store(InputFile inputFile) throws FileStorageException;
  
  
}
