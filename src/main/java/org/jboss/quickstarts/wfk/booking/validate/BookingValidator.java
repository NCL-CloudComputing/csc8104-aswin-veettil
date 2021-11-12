package org.jboss.quickstarts.wfk.booking.validate;

import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;


import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.*;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 *
 * @author Joshua Wilson
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;
    @Inject
    private BookingRepository crud;
    @Inject
    private TaxiRepository taxiCrud;
    @Inject
    private CustomerRepository custCrud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because of an existing booking with the same taxi on the same date, it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If contact with the same email already exists
     */
    public void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        validateDependencies(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (bookingAlreadyExists(booking.getTaxi().getId(), booking.getBookingDate(), booking.getId())) {
            throw new ValidationException("Booking already exists for the taxi on the given date");
        }
    }

    void validateDependencies(Booking booking) throws ValidationException {
        Taxi taxi = taxiCrud.findById(booking.getTaxi().getId());
        if (taxi == null) {
            throw new ValidationException("Taxi with the given id does not exist");
        }
        Customer customer = custCrud.findById(booking.getCustomer().getId());
        if (customer == null) {
            throw new ValidationException("Customer with the given id does not exist");
        }
        booking.setTaxi(taxi);
        booking.setCustomer(customer);
    }

    boolean bookingAlreadyExists(Long taxiId, Date bookingDate, Long bookingId) {
        Map<String, Object> fieldNameToVal = new HashMap<String, Object>() {{
            put("taxi", taxiId);
            put("bookingDate", bookingDate);
        }};
        List<Booking> bookings = crud.findAllByCriteria(fieldNameToVal);
        for (Booking bkng : bookings) {
            if((Objects.equals(bkng.getTaxi().getId(), taxiId) && bookingId == null)
               ||((Objects.equals(bkng.getTaxi().getId(), taxiId) && bookingId != null && !bookingId.equals(bkng.getId())))) {
                return true;
            }
        }
        return false;
    }
}
