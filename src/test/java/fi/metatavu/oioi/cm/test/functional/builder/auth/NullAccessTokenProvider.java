package fi.metatavu.oioi.cm.test.functional.builder.auth;

import java.io.IOException;

/**
 * Access token provider that does not provider access tokens
 * 
 * @author Antti Leppä
 */
public class NullAccessTokenProvider implements AccessTokenProvider {

  @Override
  public String getAccessToken() throws IOException {
    return null;
  }

}
