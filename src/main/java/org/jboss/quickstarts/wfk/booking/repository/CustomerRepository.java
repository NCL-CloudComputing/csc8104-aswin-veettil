package org.jboss.quickstarts.wfk.booking.repository;

import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.contact.Contact;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

public class CustomerRepository {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Contact objects
     */
    public List<Customer> findAllOrderedByName() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Contact object, specified by a Long id.<p/>
     *
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    /**
     * <p>Returns a single Contact object, specified by a String email.</p>
     *
     * <p>If there is more than one Contact with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Contact to be returned
     * @return The first Contact with the specified email
     */
    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
        if(query.getResultList().size() == 0) {
            return null;
        }
        return query.getSingleResult();
    }

    /**
     * <p>Returns a list of customer objects, specified by a String firstName.<p/>
     *
     * @param firstName The firstName field of the customers to be returned
     * @return The customers with the specified firstName
     */
    public List<Customer> findAllByFirstName(String firstName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(contact).where(cb.equal(contact.get(Contact_.firstName), firstName));
        criteria.select(customer).where(cb.equal(customer.get("firstName"), firstName));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * <p>Returns a single customer object, specified by a String lastName.<p/>
     *
     * @param lastName The lastName field of the customers to be returned
     * @return The customer with the specified lastName
     */
    public List<Customer> findAllByLastName(String lastName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(contact).where(cb.equal(contact.get(Contact_.lastName), lastName));
        criteria.select(customer).where(cb.equal(customer.get("lastName"), lastName));
        return em.createQuery(criteria).getResultList();
    }

    public Customer create(Customer customer) {
        log.info("BookingRepository.create() - Creating ");

        // Write taxi to the database.
        em.persist(customer);

        return customer;
    }
    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param customer The Customer object to be merged with an existing Customer
     * @return The Customer that has been merged
     */
    public Customer update(Customer customer) {
        // Either update the customer or add it if it can't be found.
        em.merge(customer);
        return customer;
    }
    public Customer delete(Customer customer) {
        em.remove(em.merge(customer));
        return customer;
    }
}
