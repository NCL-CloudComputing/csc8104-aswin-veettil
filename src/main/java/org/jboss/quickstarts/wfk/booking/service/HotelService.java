package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Hotel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
@Path("/hotels")
@Produces(MediaType.APPLICATION_JSON)
public interface HotelService {
    @GET
    List<Hotel> getHotels();

    @GET
    @Path("/{id:[0-9]+}")
    Hotel getHotelById(@PathParam("id") int id);
}
