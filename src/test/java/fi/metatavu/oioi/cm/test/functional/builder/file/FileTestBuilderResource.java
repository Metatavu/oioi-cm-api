package fi.metatavu.oioi.cm.test.functional.builder.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.oioi.cm.files.OutputFile;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilderResource;

/**
 * Test builder resource for uploaded files
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public class FileTestBuilderResource implements TestBuilderResource<OutputFile> {
  
  private TestBuilder testBuilder;
  
  public FileTestBuilderResource(TestBuilder testBuilder) {
    this.testBuilder = testBuilder;
  }

  /**
   * Uploads resource into file store
   * 
   * @param resourceName resource name
   * @return upload response
   * @throws IOException thrown on upload failure
   */
  public OutputFile upload(String resourceName, String contentType) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    
    try (InputStream fileStream = classLoader.getResourceAsStream(resourceName)) {
      HttpClientBuilder clientBuilder = HttpClientBuilder.create();
      try (CloseableHttpClient client = clientBuilder.build()) {
        HttpPost post = new HttpPost(String.format("%s/files", getBasePath()));
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        
        multipartEntityBuilder.addBinaryBody("file", fileStream, ContentType.create(contentType), resourceName);
        
        post.setEntity(multipartEntityBuilder.build());
        HttpResponse response = client.execute(post);

        assertEquals(200, response.getStatusLine().getStatusCode());
        
        HttpEntity httpEntity = response.getEntity();

        ObjectMapper objectMapper = new ObjectMapper();
        OutputFile result = objectMapper.readValue(httpEntity.getContent(), OutputFile.class);

        assertNotNull(result);
        assertNotNull(result.getUri());
        
        return result;
      }
    }
  }

  @Override
  public OutputFile addClosable(OutputFile t) {
    testBuilder.addClosable(new CloseableFileResource(t));
    return t;
  }

  @Override
  public void clean(OutputFile t) throws Exception {
    // File is cleaned in closeable file resource
  }
  
  /**
   * Returns API base path
   * 
   * @return API base path
   */
  protected String getBasePath() {
   return String.format("http://%s:%d", getHost(), getPort());
  }
  
  /**
   * Returns API host
   * 
   * @return API host
   */
  protected String getHost() {
    return System.getProperty("it.host");
  }
  
  /**
   * Returns API port
   * 
   * @return API port
   */
  protected Integer getPort() {
    return NumberUtils.createInteger(System.getProperty("it.port.http"));
  }
  
}
