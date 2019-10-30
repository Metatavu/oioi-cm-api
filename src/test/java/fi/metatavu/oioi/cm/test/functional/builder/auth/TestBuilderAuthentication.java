package fi.metatavu.oioi.cm.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.oioi.cm.client.ApiClient;
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder;
import fi.metatavu.oioi.cm.test.functional.settings.TestSettings;

/**
 * Default implementation of test builder authentication provider
 * 
 * @author Antti Leppä
 */
public class TestBuilderAuthentication extends AbstractTestBuilderAuthentication {
  
  
  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor
   * 
   * @param testBuilder testBuilder
   * @param accessTokenProvider access token builder
   */
  public TestBuilderAuthentication(TestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    super(testBuilder);
    this.accessTokenProvider = accessTokenProvider;
  }
  
  /** 
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient() throws IOException {
    String accessToken = accessTokenProvider != null ? accessTokenProvider.getAccessToken() : null;
    ApiClient result = new ApiClient();
    
    result.setBasePath("/v1");
    result.setHost(TestSettings.getHost());
    result.setPort(TestSettings.getPort());
    
    if (accessToken != null) {
      result.setRequestInterceptor(builder -> {
        builder.header("Authorization", String.format("Bearer %s", accessToken));
      });
    }
    
    return result;
  }
  
}
