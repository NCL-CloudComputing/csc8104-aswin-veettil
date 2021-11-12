package org.jboss.quickstarts.wfk.booking.service.external;

import org.jboss.quickstarts.wfk.booking.model.external.flight.Flight;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
public interface FlightService {
    @GET
    List<Flight> getFlights();

    @GET
    @Path("/{id:[0-9]+}")
    Flight getFlightById(@PathParam("id") int id);
}
