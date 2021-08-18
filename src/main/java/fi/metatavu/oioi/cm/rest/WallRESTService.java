package fi.metatavu.oioi.cm.rest;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.metatavu.oioi.cm.applications.ApplicationController;
import fi.metatavu.oioi.cm.persistence.model.Application;
import fi.metatavu.oioi.cm.rest.translate.WallApplicationTranslator;

/**
 * System REST Services
 * 
 * @author Antti Leppä
 */
@Path ("/application")
@Transactional
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class WallRESTService {
    
  @Inject
  private ApplicationController applicationController;

  @Inject
  private WallApplicationTranslator wallApplicationTranslator;
  
  /**
   * Returns pong
   * 
   * @return pong in plain text
   */
  @GET
  @Path ("/{applicationId}")
  @Produces (MediaType.APPLICATION_JSON)
  public Response getApplicationJson(@PathParam("applicationId") UUID applicationId) {
    Application application = applicationController.findApplicationById(applicationId);
    if (application == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    return Response.ok(wallApplicationTranslator.translate(application)).build();
  }
  
}
