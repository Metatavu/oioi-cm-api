package fi.metatavu.oioi.cm.rest;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.slf4j.Logger;

import fi.metatavu.oioi.cm.model.ErrorResponse;

/**
 * Abstract base class for all API services
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public abstract class AbstractApi {
  
  protected static final String NOT_FOUND_MESSAGE = "Not found";
  protected static final String CUSTOMER_DEVICE_MISMATCH_MESSAGE = "Device does not belong to this customer";
  protected static final String APPLICATION_DEVICE_MISMATCH_MESSAGE = "Application does not belong to this device";
  protected static final String FORBIDDEN_MESSAGE = "Forbidden";

  protected static final String ADMIN_ROLE = "admin";
  
  @Inject
  private Logger logger;
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @return response
   */
  protected Response createOk(Object entity) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .build();
  }

  /**
   * Constructs ok or not found response if entity is null
   * 
   * @param entity payload
   * @return response
   */
  protected Response createOkOrNotFound(Object entity) {
    if (entity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(entity);
  }
  
  /**
   * Constructs not found response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotFound(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.NOT_FOUND)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs forbidden response
   * 
   * @param message message
   * @return response
   */
  protected Response createForbidden(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.FORBIDDEN)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs bad request response
   * 
   * @param message message
   * @return response
   */
  protected Response createBadRequest(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.BAD_REQUEST)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs internal server error response
   * 
   * @param message message
   * @return response
   */
  protected Response createInternalServerError(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(entity)
      .build();
  }
  
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @param totalHits total hits
   * @return response
   */
  protected Response createOk(Object entity, Long totalHits) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .header("Total-Results", totalHits)
      .build();
  }
  
  /**
   * Constructs no content response
   * 
   * @return response
   */
  protected Response createNoContent() {
    return Response
      .status(Response.Status.NO_CONTENT)
      .build();
  }
  
  /**
   * Returns logged user id
   * 
   * @return logged user id
   */
  protected UUID getLoggerUserId() {
    HttpServletRequest httpServletRequest = getHttpServletRequest();
    String remoteUser = httpServletRequest.getRemoteUser();
    if (remoteUser == null) {
      return null;
    }
    
    return UUID.fromString(remoteUser);
  }
  
  /**
   * Returns request locale
   * 
   * @return request locale
   */
  protected Locale getLocale() {
    return getHttpServletRequest().getLocale();
  }
  
  /**
   * Return current HttpServletRequest
   * 
   * @return current http servlet request
   */
  protected HttpServletRequest getHttpServletRequest() {
    return ResteasyProviderFactory.getContextData(HttpServletRequest.class);
  }

  /**
   * Return Keycloak authorization client
   */
  protected AuthzClient getAuthzClient() {
    ClientAuthorizationContext clientAuthorizationContext = getAuthorizationContext();
    if (clientAuthorizationContext == null) {
      logger.error("Failed to retrieve Keycloak authorization client");
      return null;
    }

    return clientAuthorizationContext.getClient();
  }

  /**
   * Returns whether logged user has role
   * 
   * @param role role
   * @return whether logged user has specified role or not
   */
  protected Boolean hasRealmRole(String roleName) {
    KeycloakSecurityContext keycloakSecurityContext = getKeycloakSecurityContext();
    if (keycloakSecurityContext == null) {
      System.out.println("Cannot get security context");
      return false;
    }

    AccessToken token = keycloakSecurityContext.getToken();
    if (token == null) {
      System.out.println("Cannot get token");
      return false;
    }

    Access realmAccess = token.getRealmAccess();
    if (realmAccess == null) {
      System.out.println("Cannot get realm access");
      return false;
    }

    System.out.println("User roles: ");
    realmAccess.getRoles().stream().forEach(r -> System.out.println(r));
    return realmAccess.isUserInRole(roleName);
  }
  
  /**
   * Returns group ids where logged user is a member of
   * 
   * @return group ids where logged user is a member of
   */
  protected Set<String> getLoggedUserGroups() {
    KeycloakSecurityContext context = getKeycloakSecurityContext();
    if (context == null) {
      return Collections.emptySet();
    }

    AccessToken token = context.getToken();
    if (token == null) {
      return Collections.emptySet();
    }
    
    Map<String, Object> otherClaims = context.getToken() .getOtherClaims();
    if (otherClaims == null) {
      return Collections.emptySet();
    }
    
    @SuppressWarnings("unchecked") 
    Collection<String> groupIds = (Collection<String>) otherClaims.get("groups");
    if (groupIds == null) {
      return Collections.emptySet();
    }
    
    return groupIds.stream().map(group -> group).collect(Collectors.toSet());    
  }

  protected boolean isAdminOrHasCustomerGroup(String customerName) {
    Boolean isAdmin = hasRealmRole(ADMIN_ROLE);
    if (isAdmin) {
      return true;
    }

    Set<String> groups = getLoggedUserGroups();
    return groups.contains(customerName);
  }

  /**
   * Return keycloak authorization client context or null if not available 
   */
  private ClientAuthorizationContext getAuthorizationContext() {
    KeycloakSecurityContext keycloakSecurityContext = getKeycloakSecurityContext();
    if (keycloakSecurityContext == null) {
      logger.error("Failed to retrieve KeycloakSecurityContext");
      return null;
    }

    return (ClientAuthorizationContext) keycloakSecurityContext.getAuthorizationContext();
  }

  /**
   * Returns keycloak security context from request or null if not available
   */
  private KeycloakSecurityContext getKeycloakSecurityContext() {
    HttpServletRequest request = getHttpServletRequest();
    Principal userPrincipal = request.getUserPrincipal();
    KeycloakPrincipal<?> kcPrincipal = (KeycloakPrincipal<?>) userPrincipal;
    if (kcPrincipal == null) {
      return null;
    }
    
    return kcPrincipal.getKeycloakSecurityContext();
  }
}
