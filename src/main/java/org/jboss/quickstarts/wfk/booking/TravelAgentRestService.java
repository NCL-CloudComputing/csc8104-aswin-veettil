package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.TravelAgent;
import org.jboss.quickstarts.wfk.booking.service.TravelAgentService;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/travelAgents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/travelAgents")
@Stateless
public class TravelAgentRestService {
    @Inject
    private TravelAgentService service;

    @GET
    @ApiOperation(value = "Fetch all bookings by travel agent", notes = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings() {
        //Create an empty collection to contain the intersection of bookings to be returned
        List<Booking> bookings = service.findAll();
        return Response.ok(bookings).build();
    }
    @SuppressWarnings("unused")
    @POST
    @ApiOperation(value = "Add a new Booking to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Contact created successfully."),
            @ApiResponse(code = 400, message = "Invalid Contact supplied in request body"),
            @ApiResponse(code = 409, message = "Contact supplied in request body conflicts with an existing Contact"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
                    TravelAgent travelAgentBooking) throws Exception {

        if (travelAgentBooking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {
            service.create(travelAgentBooking);
            builder = Response.status(Response.Status.CREATED).entity(travelAgentBooking);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return builder.build();
    }
}
