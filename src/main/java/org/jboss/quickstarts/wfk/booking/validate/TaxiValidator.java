package org.jboss.quickstarts.wfk.booking.validate;

import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.util.UniqueRegNoException;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;
/**
 * <p>This class provides methods to check Contact objects against arbitrary requirements.</p>
 *
 * @see Taxi
 * @see TaxiRepository
 * @see javax.validation.Validator
 */
public class TaxiValidator {
    @Inject
    private Validator validator;

    @Inject
    private TaxiRepository crud;

    /**
     * <p>Validates the given Taxi object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing taxi with the same regNo is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param taxi The Taxi object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If taxi with the same regNo already exists
     */
    public void validateTaxi(Taxi taxi) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if(taxi.getNoOfSeats() < 2 || taxi.getNoOfSeats() > 20) {
            throw new ValidationException("Taxi with invalid number of seats");
        }
        // Check the uniqueness of the email address
        if (taxiRegNoAlreadyExists(taxi.getVehicleRegNo(), taxi.getId())) {
            throw new UniqueRegNoException("Taxi with reg no already exists");
        }
    }

    private boolean taxiRegNoAlreadyExists(String regNo, Long id) {
        Taxi taxi = null;
        Taxi taxiWithId = null;
        try {
            taxi = crud.findByRegNo(regNo);
        } catch (NoResultException e) {
            // ignore
        }

        if (taxi != null && id != null) {
            try {
                taxiWithId = crud.findById(id);
                if (taxiWithId != null && taxiWithId.getVehicleRegNo().equals(regNo)) {
                    taxi = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return taxi != null;
    }
}
