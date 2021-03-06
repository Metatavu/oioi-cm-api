package fi.metatavu.oioi.cm.files.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import fi.metatavu.oioi.cm.files.FileMeta;
import fi.metatavu.oioi.cm.files.InputFile;
import fi.metatavu.oioi.cm.files.OutputFile;

/**
 * File storage provider for storing files locally. 
 * 
 * Mainly used in tests
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class LocalFileStorageProvider implements FileStorageProvider {
  
  private File folder;
  
  @Override
  public void init() throws FileStorageException {
    String path = System.getenv("LOCAL_FILE_STORAGE_PATH");
    if (StringUtils.isBlank(path)) {
      throw new FileStorageException("LOCAL_FILE_STORAGE_PATH is not set"); 
    }
    
    folder = new File(path);
    
    if (!folder.exists() || !folder.isDirectory()) {
      throw new FileStorageException("LOCAL_FILE_STORAGE_PATH is not folder");
    }
    
    if (!folder.canRead() || !folder.canWrite()) {
      throw new FileStorageException("LOCAL_FILE_STORAGE_PATH is not writeable");
    }
  }

  @Override
  public OutputFile store(InputFile inputFile) throws FileStorageException {
    File parent = new File(folder, inputFile.getFolder());
    if (!parent.exists()) {
      parent.mkdirs();
    }
    
    FileMeta meta = inputFile.getMeta();
    String extension = FilenameUtils.getExtension(meta.getFileName());
    StringBuilder fileNameBuilder = new StringBuilder();
    fileNameBuilder.append(UUID.randomUUID().toString());
    if (StringUtils.isNotBlank(extension)) {
      fileNameBuilder
        .append(".")
        .append(extension);
    }
    String outputFileName = fileNameBuilder.toString();
    File file = new File(parent, outputFileName);
    
    try (FileOutputStream fileStream = new FileOutputStream(file)) {
      IOUtils.copy(inputFile.getData(), fileStream);
    } catch (IOException e) {
      throw new FileStorageException(e);
    }
    
    return new OutputFile(inputFile.getMeta(), file.toURI());
  }
  
  @Override
  public String getId() {
    return "LOCAL";
  }
  
}
