package org.jboss.quickstarts.wfk.booking.repository;

import org.jboss.quickstarts.wfk.booking.model.Taxi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

public class TaxiRepository {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Taxi objects
     */
    public List<Taxi> findAll() {
        TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_ALL, Taxi.class);
        return query.getResultList();
    }
    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return em.find(Taxi.class, id);
    }
    public Taxi create(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating ");

        // Write taxi to the database.
        em.persist(taxi);

        return taxi;
    }
}
