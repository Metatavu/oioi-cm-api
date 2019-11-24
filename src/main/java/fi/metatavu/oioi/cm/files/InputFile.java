package fi.metatavu.oioi.cm.files;

import java.io.InputStream;

/**
 * Class representing a file uploaded into the system but not yet persisted into the database
 * 
 * @author Antti Leppä
 */
public class InputFile implements AutoCloseable {

  private String folder;
  private FileMeta meta;
  private InputStream data;

  /**
   * Constructor
   * 
   * @param folder folder the file is stored in
   * @param meta file meta
   * @param data file data
   */
  public InputFile(String folder, FileMeta meta, InputStream data) {
    super();
    this.folder = folder;
    this.meta = meta;
    this.data = data;
  }
  
  /**
   * Returns folder the file is stored in
   * 
   * @return folder the file is stored in
   */
  public String getFolder() {
    return folder;
  }
  
  /**
   * Returns data
   * 
   * @return data
   */
  public InputStream getData() {
    return data;
  }

  /**
   * Returns meta
   * 
   * @return meta
   */
  public FileMeta getMeta() {
    return meta;
  }

  @Override
  public void close() throws Exception {
    if (data != null) {
      data.close();
    }
  }

}
