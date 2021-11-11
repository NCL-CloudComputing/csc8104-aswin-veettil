package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.service.BookingService;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/bookings", description = "Operations about bookings")
@Stateless
public class BookingRestService {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private BookingService service;

    @GET
    @ApiOperation(value = "Fetch all bookings", notes = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings(@QueryParam("customerId") Long customerId) {
        //Create an empty collection to contain the intersection of bookings to be returned
        List<Booking> bookings;
        if(customerId != null) {
            bookings = service.findAllByCustomerId(customerId);
        } else {
            bookings = service.findAll();
        }
        return Response.ok(bookings).build();
    }
    /**
     * <p>Search for and return a Booking identified by id.</p>
     *
     * @param id The long parameter value provided as a Booking id
     * @return A Response containing a single Booking
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by id",
            notes = "Returns a JSON representation of the Booking object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking with id not found")
    })
    public Response retrieveTaxiById(
            @ApiParam(value = "Id of Booking to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
                    long id) {

        Booking booking = service.findById(id);
        if (booking == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Contact = " + booking.toString());

        return Response.ok(booking).build();
    }
    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link org.jboss.quickstarts.wfk.booking.service.BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
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
                    Booking booking) {


        if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (ValidationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<String, String>() {{
                put("BAD_REQ", ce.getMessage());
            }};
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }

        log.info("createBooking completed. Booking = " + booking.toString());
        return builder.build();
    }
    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a booking from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The booking has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid booking id supplied"),
            @ApiResponse(code = 404, message = "booking with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteBooking(
            @ApiParam(value = "Id of booking to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
                    long id) throws RestServiceException {

        Response.ResponseBuilder builder;

        try {
            service.delete(id);

            builder = Response.noContent();

        } catch (RestServiceException e) {
            // Handle generic exceptions
            throw new RestServiceException(e.getMessage(), Response.Status.NOT_FOUND);
        }
        return builder.build();
    }
}
