package org.jboss.quickstarts.wfk.booking;

import io.swagger.annotations.*;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.service.CustomerService;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/customer", description = "Operations about customer")
@Stateless
public class CustomerRestService {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private CustomerService service;

    /**
     * <p>Return all the Customers.  They are sorted alphabetically by name.</p>
     *
     * <p>The url may optionally include query parameters specifying a Customer's name</p>
     *
     * <p>Examples: <pre>GET api/contacts?firstname=John</pre>, <pre>GET api/customers?firstname=John&lastname=Smith</pre></p>
     *
     * @return A Response containing a list of Customers
     */
    @GET
    @ApiOperation(value = "Fetch all Customers", notes = "Returns a JSON array of all stored Customer objects.")
    public Response retrieveAllCustomers(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        //Create an empty collection to contain the intersection of Contacts to be returned
        List<Customer> customers;

        if(firstname == null && lastname == null) {
            customers = service.findAllOrderedByName();
        } else if(lastname == null) {
            customers = service.findAllByFirstName(firstname);
        } else if(firstname == null) {
            customers = service.findAllByLastName(lastname);
        } else {
            customers = service.findAllByFirstName(firstname);
            customers.retainAll(service.findAllByLastName(lastname));
        }

        return Response.ok(customers).build();
    }

    /**
     * <p>Search for and return a Customer identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Customer's email
     * @return A Response containing a single Customer
     */
    @GET
    @Cache
    @Path("/email/{email:.+[%40|@].+}")
    @ApiOperation(
            value = "Fetch a Customer by Email",
            notes = "Returns a JSON representation of the Customer object with the provided email."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Customer found"),
            @ApiResponse(code = 404, message = "Customer with email not found")
    })
    public Response retrieveContactsByEmail(
            @ApiParam(value = "Email of Customer to be fetched", required = true)
            @PathParam("email")
                    String email) {

        Customer customer;
        try {
            customer = service.findByEmail(email);
        } catch (NoResultException e) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the email " + email + " was found!", Response.Status.NOT_FOUND);
        }
        return Response.ok(customer).build();
    }

    /**
     * <p>Search for and return a customer identified by id.</p>
     *
     * @param id The long parameter value provided as a customer's id
     * @return A Response containing a single customer
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Contact by id",
            notes = "Returns a JSON representation of the Contact object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Contact found"),
            @ApiResponse(code = 404, message = "Contact with id not found")
    })
    public Response retrieveContactById(
            @ApiParam(value = "Id of Contact to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
                    long id) {

        Customer customer = service.findById(id);
        if (customer == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Contact = " + customer.toString());

        return Response.ok(customer).build();
    }

    /**
     * <p>Creates a new customer from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param customer The customer object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link CustomerService#create(Customer)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @ApiOperation(value = "Add a new v to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "customer created successfully."),
            @ApiResponse(code = 400, message = "Invalid customer supplied in request body"),
            @ApiResponse(code = 409, message = "customer supplied in request body conflicts with an existing Contact"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createContact(
            @ApiParam(value = "JSON representation of customer object to be added to the database", required = true)
                    Customer customer) {


        if (customer == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new customer.
            service.create(customer);

            // Create a "Resource Created" 201 Response and pass the customer back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(customer);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createContact completed. Contact = " + customer.toString());
        return builder.build();
    }
}
