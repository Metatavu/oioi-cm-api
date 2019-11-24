package fi.metatavu.oioi.cm.test.functional.builder.file;

import java.io.File;
import java.nio.file.Files;

import fi.metatavu.oioi.cm.files.OutputFile;
import fi.metatavu.oioi.cm.test.functional.builder.CloseableResource;

/**
 * Describes closeable file resource
 * 
 * @author Antti Leppä
 */
public class CloseableFileResource extends CloseableResource<OutputFile> {
  
  /**
   * Constructor
   * 
   * @param resource file resource
   */
  public CloseableFileResource(OutputFile resource) {
    super(resource);
  }

  @Override
  public void close() throws Exception {
    File file = new File(getResource().getUri());
    if (file.exists()) {
      Files.delete(file.toPath());
    }
  }

}
