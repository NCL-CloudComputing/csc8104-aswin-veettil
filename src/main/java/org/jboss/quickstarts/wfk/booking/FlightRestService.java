package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jboss.quickstarts.wfk.booking.model.external.flight.Flight;
import org.jboss.quickstarts.wfk.booking.service.external.FlightService;
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

@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/flights")
@Stateless
public class FlightRestService {
    private ResteasyClient client;

    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public FlightRestService() {
        // Create client service instance to make REST requests to upstream service
        client = new ResteasyClientBuilder().build();
    }
    /**
     * <p>Return all the FLights.</p>
     * @return A Response containing a list of Flights
     */
    @GET
    @ApiOperation(value = "Fetch all Flights", notes = "Returns a JSON array of all stored Flight objects.")
    public Response findAll() {
        //Create client service instance to make REST requests to upstream service
        ResteasyWebTarget target = client.target(Flight.FLIGHT_BASE_URL);
        FlightService service = target.proxy(FlightService.class);
        List<Flight> hotels = service.getFlights();
        return Response.ok(hotels).build();
    }
}
