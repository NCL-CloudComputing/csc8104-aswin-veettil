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
    /**
     * <p>Returns a single Taxi object, specified by a String email.</p>
     *
     * <p>If there is more than one Taxi with the specified Reg No, only the first encountered will be returned.<p/>
     *
     * @param regNo The regNo field of the taxi to be returned
     * @return The first Taxi with the specified regNo
     */
    public Taxi findByRegNo(String regNo) {
        TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_BY_REG_NO, Taxi.class).setParameter("regNo", regNo);
        if(query.getResultList().size() == 0) {
            return null;
        }
        return query.getSingleResult();
    }
    public Taxi create(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating ");

        // Write taxi to the database.
        em.persist(taxi);

        return taxi;
    }
    /**
     * <p>Updates an existing Taxi object in the application database with the provided Taxi object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param taxi The Taxi object to be merged with an existing Taxi
     * @return The Taxi that has been merged
     */
    public Taxi update(Taxi taxi) {
        // Either update the customer or add it if it can't be found.
        em.merge(taxi);

        return taxi;
    }
    public Taxi delete(Taxi taxi) {
        em.remove(em.merge(taxi));
        return taxi;
    }
}
