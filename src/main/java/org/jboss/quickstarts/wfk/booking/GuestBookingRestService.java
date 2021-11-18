package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.GuestBooking;
import org.jboss.quickstarts.wfk.booking.service.BookingService;
import org.jboss.quickstarts.wfk.booking.service.CustomerService;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/guestBookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/guestBookings")
@Stateless
@TransactionManagement(value= TransactionManagementType.BEAN)
public class GuestBookingRestService {
    @Inject
    BookingService bookingService;
    @Inject
    CustomerService customerService;
    @Resource
    UserTransaction transaction;
    /**
     * <p>Creates a new booking and customer from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param guestBooking The GuestBooking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link BookingService#create(Booking)}
     * {@link CustomerService#create(Customer)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @ApiOperation(value = "Add a new Booking and Customer to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking and Customer created successfully."),
            @ApiResponse(code = 400, message = "Invalid Data supplied in request body"),
            @ApiResponse(code = 409, message = "Data supplied in request body conflicts with an existing Contact"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createGuestBooking(
            @ApiParam(value = "JSON representation of GuestBooking object to be added to the database", required = true)
                    GuestBooking guestBooking) throws RestServiceException, SystemException {
        if (guestBooking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        Booking booking = guestBooking.getBooking();
        Customer customer = guestBooking.getCustomer();
        try {
            transaction.begin();
            Customer cust = customerService.findByEmail(customer.getEmail());
            if (cust == null) {
                //add the new Customer.
                cust = customerService.create(customer);
            }
            guestBooking.setCustomer(cust);
            //associate customer to booking
            booking.setCustomerId(cust.getId());

            // add the new Booking.
            bookingService.create(booking);
            transaction.commit();
        }  catch (ConstraintViolationException ce) {
            transaction.rollback();
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (ValidationException ce) {
            transaction.rollback();
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<String, String>() {{
                put("BAD_REQ", ce.getMessage());
            }};
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (Exception e) {
            transaction.rollback();
            // Handle generic exceptions
            throw new RestServiceException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
        builder = Response.status(Response.Status.CREATED).entity(guestBooking);
        return builder.build();
    }
}
