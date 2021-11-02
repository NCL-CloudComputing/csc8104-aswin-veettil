package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.service.BookingService;
import org.jboss.quickstarts.wfk.booking.service.TaxiService;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/taxi")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/taxi", description = "Operations about taxi")
@Stateless
public class TaxiRestService {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private TaxiService service;

    @GET
    @ApiOperation(value = "Fetch all taxi", notes = "Returns a JSON array of all stored taxi objects.")
    public Response retrieveAllTaxis() {
        //Create an empty collection to contain the intersection of taxis to be returned
        List<Taxi> taxis;
        taxis = service.findAll();
        return Response.ok(taxis).build();
    }

    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param taxi The Booking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link org.jboss.quickstarts.wfk.booking.service.TaxiService#create(Taxi)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @ApiOperation(value = "Add a new taxi to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Taxi created successfully."),
            @ApiResponse(code = 400, message = "Invalid Taxi supplied in request body"),
            @ApiResponse(code = 409, message = "Taxi supplied in request body conflicts with an existing Contact"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
                    Taxi taxi) throws Exception {


        if (taxi == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new taxi.
            service.create(taxi);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(taxi);


        } catch (Exception ce) {
            throw new Exception(ce.getMessage());
        }

        log.info("createTaxi completed. Booking = " + taxi.toString());
        return builder.build();
    }
}
