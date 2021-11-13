package org.jboss.quickstarts.wfk.booking.repository;

import org.jboss.quickstarts.wfk.booking.model.Booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BookingRepository {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;
    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Booking objects
     */
    public List<Booking> findAll() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }
    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return em.find(Booking.class, id);
    }

    public List<Booking> findAllByCustomerId(Long customerId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(contact).where(cb.equal(contact.get(Contact_.firstName), firstName));
        criteria.select(booking).where(cb.equal(booking.get("customer"), customerId));
        return em.createQuery(criteria).getResultList();
    }

    public Booking create(Booking booking) {
        log.info("BookingRepository.create() - Creating ");

        // Write booking to the database.
        em.persist(booking);

        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     */
    public Booking update(Booking booking) {
        // Either update the booking or add it if it can't be found.
        em.merge(booking);
        return booking;
    }

    public Booking delete(Booking booking) {
        em.remove(em.merge(booking));
        return booking;
    }

    public List<Booking> findAllByCriteria(Map<String, Object> fieldNameToVal) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        for (String fieldName : fieldNameToVal.keySet()) {
            Object val = fieldNameToVal.get(fieldName);
            criteria.select(booking).where(cb.equal(booking.get(fieldName), val));
        }
        return em.createQuery(criteria).getResultList();
    }
}
