package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.Api;
import org.jboss.quickstarts.wfk.booking.model.external.hotel.Hotel;
import org.jboss.quickstarts.wfk.booking.service.external.HotelService;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
@Path("/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/hotels")
@Stateless
public class HotelRestService {
    private ResteasyClient client;

    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public HotelRestService() {
        // Create client service instance to make REST requests to upstream service
        client = new ResteasyClientBuilder().build();
    }
    @GET
    public Response findAll() {
        //Create client service instance to make REST requests to upstream service
        ResteasyWebTarget target = client.target(Hotel.HOTEL_BASE_URL);
        HotelService service = target.proxy(HotelService.class);
        List<Hotel> hotels = service.getHotels();
        return Response.ok(hotels).build();
    }
}
