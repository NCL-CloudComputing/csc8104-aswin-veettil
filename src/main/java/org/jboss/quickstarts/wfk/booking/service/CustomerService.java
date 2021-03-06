package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.external.flight.Flight;
import org.jboss.quickstarts.wfk.booking.model.external.hotel.Hotel;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.service.external.GuestBookingService;
import org.jboss.quickstarts.wfk.booking.validate.CustomerValidator;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

public class CustomerService {
    @Inject
    private CustomerRepository crud;
    @Inject
    private CustomerValidator validator;
    @Inject
    private @Named("logger") Logger log;

    private ResteasyClient client;

    public CustomerService() {
        // Create client service instance to make REST requests to upstream service
        client = new ResteasyClientBuilder().build();
    }

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Customer objects
     */
    public List<Customer> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    public Customer findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * <p>If there is more than one Customer with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Customer to be returned
     * @return The first Customer with the specified email
     */
    public Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a single Customer object, specified by a String firstName.<p/>
     *
     * @param firstName The firstName field of the Customer to be returned
     * @return The first Customer with the specified firstName
     */
    public List<Customer> findAllByFirstName(String firstName) {
        return crud.findAllByFirstName(firstName);
    }

    /**
     * <p>Returns a single Customer object, specified by a String lastName.<p/>
     *
     * @param lastName The lastName field of the Customer to be returned
     * @return The Customer with the specified lastName
     */
    public List<Customer> findAllByLastName(String lastName) {
        return crud.findAllByLastName(lastName);
    }

    /**
     * <p>Writes the provided Customer object to the application database.<p/>
     *
     *
     * @param customer The Customer object to be written to the database using a {@link CustomerRepository} object
     * @return The Customer object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public  Customer create(Customer customer) {
        log.info("CustomerService.create() - Creating " + customer.getFirstName() + " " + customer.getLastName());

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateCustomer(customer);

        // Write the contact to the database.
        return crud.create(customer);
    }
    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.<p/>
     *
     * <p>Validates the data in the provided Customer object using a CustomerValidator object.<p/>
     *
     * @param customer The Customer object to be passed as an update to the application database
     * @return The Customer object that has been successfully updated in the application database
     */
    public Customer update(Customer customer) {
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateCustomer(customer);
        // Either update the customer or add it if it can't be found.
        return crud.update(customer);
    }
    public Customer delete(Long customerId) throws RestServiceException {
        Customer customer = crud.findById(customerId);
        ResteasyWebTarget hotelTarget = client.target(Hotel.HOTEL_BASE_URL);
        GuestBookingService hotelService = hotelTarget.proxy(GuestBookingService.class);

        ResteasyWebTarget flightTarget = client.target(Flight.FLIGHT_BASE_URL);
        GuestBookingService flightService = flightTarget.proxy(GuestBookingService.class);

        for(Booking booking : customer.getBooking()) {
            if(booking.getHotelBookingId() != null) {
                try {
                    hotelService.deleteHotelBooking(booking.getHotelBookingId());
                } catch(Exception e) {
                    throw new RestServiceException("Could not delete hotel booking.", Response.Status.INTERNAL_SERVER_ERROR);
                }
            }

            if(booking.getFlightBookingId() != null) {
                try {
                    flightService.deleteFlightBooking(booking.getFlightBookingId());
                } catch(Exception e) {
                    throw new RestServiceException("Could not delete flight booking.", Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
        }
        if(customer != null && customer.getId() != null) {
            crud.delete(customer);
        } else {
            throw new RestServiceException("Invalid Customer Id");
        }
        return customer;
    }
}
