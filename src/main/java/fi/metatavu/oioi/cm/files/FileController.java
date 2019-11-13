package fi.metatavu.oioi.cm.files;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.oioi.cm.files.storage.FileStorageException;
import fi.metatavu.oioi.cm.files.storage.FileStorageProvider;

/**
 * Controller for file functions
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
@Singleton
public class FileController {
  
  @Inject
  @Any
  private Instance<FileStorageProvider> fileStorageProviders;
    
  private FileStorageProvider fileStorageProvider;
  
  /**
   * Bean post construct method
   * 
   * @throws FileStorageException thrown on file storage configuration issues
   */
  @PostConstruct
  public void init() throws FileStorageException {
    String fileStorageProviderId = System.getenv("FILE_STORAGE_PROVIDER");
    
    if (StringUtils.isEmpty(fileStorageProviderId)) {
      throw new FileStorageException("FILE_STORAGE_PROVIDER is not defined");
    }
    
    fileStorageProvider = fileStorageProviders.stream()
      .filter(fileStorageProvider -> fileStorageProviderId.equals(fileStorageProvider.getId()))
      .findFirst()
      .orElseThrow(() -> new FileStorageException("Invalid file storage provider configured"));
    
    fileStorageProvider.init();
  }
  
  /**
   * Stores file and returns reference id
   * 
   * @param inputFile input file
   * @return output file
   */
  public OutputFile storeFile(InputFile inputFile) throws FileStorageException {
    return fileStorageProvider.store(inputFile);
  }

}