package fi.metatavu.oioi.cm.test.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import fi.metatavu.oioi.cm.files.OutputFile;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class FileTestsIT extends AbstractFunctionalTest {

  @Test
  public void testUploadFile() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      OutputFile uploadedFile = builder.admin().files().upload("test-image.jpg", "image/jpeg");
      
      File file = new File(uploadedFile.getUri());
      assertTrue(file.exists());
      
      try (FileInputStream fileInputStream = new FileInputStream(file)) {
        assertEquals(getResourceMd5("test-image.jpg"), DigestUtils.md5Hex(fileInputStream));
      }
    }
  }
  
}
