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

import fi.metatavu.oioi.cm.devices.DeviceController;
import fi.metatavu.oioi.cm.persistence.model.Device;
import fi.metatavu.oioi.cm.rest.translate.WallDeviceTranslator;

/**
 * Wall device rest service
 * 
 * @author Heikki Kurhinen <heikki.kurhinen@metatavu.fi>
 */
@Path ("/device")
@Transactional
@RequestScoped
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class WallDeviceRESTService {

  @Inject
  private DeviceController deviceController;

  @Inject
  private WallDeviceTranslator wallDeviceTranslator;

  /**
   * Returns pong
   * 
   * @return pong in plain text
   */
  @GET
  @Path ("/{deviceId}")
  @Produces (MediaType.APPLICATION_JSON)
  public Response getApplicationJson(@PathParam("deviceId") UUID deviceId) {
    Device device = deviceController.findDeviceById(deviceId);
    if (device == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    return Response.ok(wallDeviceTranslator.translate(device)).build();
  }
  
}
