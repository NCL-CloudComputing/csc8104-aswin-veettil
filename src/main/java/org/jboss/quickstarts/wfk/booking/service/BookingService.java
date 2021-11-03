package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.List;

public class BookingService {
    @Inject
    private BookingRepository crud;
    @Inject
    private TaxiRepository taxiCrud;
    @Inject
    private CustomerRepository custCrud;

    public BookingService() {

    }
    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Booking objects
     */
    public List<Booking> findAll() {
        return crud.findAll();
    }
    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return crud.findById(id);
    }
    /**
     * <p>Returns a list of Booking object, specified by a String customerId.<p/>
     *
     * @param customerId The Id of the customer
     * @return The List of bookings made by the customer
     */
    public List<Booking> findAllByCustomerId(Long customerId) {
        return crud.findAllByCustomerId(customerId);
    }
    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking create(Booking booking) throws Exception {
        // Write the booking to the database.
        Booking bkng = crud.create(booking);

        Taxi taxi = taxiCrud.findById(booking.getTaxi().getId());
        bkng.setTaxi(taxi);

        Customer customer = custCrud.findById(booking.getCustomer().getId());
        bkng.setCustomer(customer);

        return bkng;
    }
}
