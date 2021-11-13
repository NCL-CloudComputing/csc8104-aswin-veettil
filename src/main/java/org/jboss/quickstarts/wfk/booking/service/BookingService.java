package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.area.Area;
import org.jboss.quickstarts.wfk.area.AreaService;
import org.jboss.quickstarts.wfk.area.InvalidAreaCodeException;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.booking.validate.BookingValidator;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class BookingService {
    @Inject
    private BookingRepository crud;
    @Inject
    private BookingValidator validator;

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
    public Booking create(Booking booking) {
        validator.validateBooking(booking);
        // Write the booking to the database.
        crud.create(booking);
        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.<p/>
     *
     * <p>Validates the data in the provided Booking object using a BookingValidator object.<p/>
     *
     * @param booking The Booking object to be passed as an update to the application database
     * @return The Booking object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking update(Booking booking) {
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateBooking(booking);

        // Either update the booking or add it if it can't be found.
        return crud.update(booking);
    }

    public Booking delete(Long bookingId) throws RestServiceException {
        Booking booking = crud.findById(bookingId);
        if(booking != null && booking.getId() != null) {
            crud.delete(booking);
        } else {
            throw new RestServiceException("Invalid Booking Id");
        }
        return booking;
    }

    public List<Booking> findAllByCriteria(Map<String, Object> criteriaMap) {
        return crud.findAllByCriteria(criteriaMap);
    }
}
