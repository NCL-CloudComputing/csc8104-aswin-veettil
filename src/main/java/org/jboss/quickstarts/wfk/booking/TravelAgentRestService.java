package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.TravelAgent;
import org.jboss.quickstarts.wfk.booking.service.BookingService;
import org.jboss.quickstarts.wfk.booking.service.TravelAgentService;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/travelAgent/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/travelAgent/bookings")
@Stateless
public class TravelAgentRestService {
    @Inject
    private TravelAgentService service;
    @Inject
    private BookingService bookingSvc;
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
            throw new RestServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
        return builder.build();
    }
    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     * <p>Also deletes corresponding booking from consuming services</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Booking from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The booking has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid booking id supplied"),
            @ApiResponse(code = 404, message = "booking with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteBooking(
            @ApiParam(value = "Id of booking to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
                    long id) {

        Response.ResponseBuilder builder;

        Booking booking = bookingSvc.findById(id);
        if (booking == null) {
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(booking);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        return builder.build();
    }
}
