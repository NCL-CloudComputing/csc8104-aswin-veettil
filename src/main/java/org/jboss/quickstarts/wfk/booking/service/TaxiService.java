package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.contact.Contact;

import javax.inject.Inject;
import java.util.List;

public class TaxiService {
    @Inject
    private TaxiRepository crud;

    public TaxiService() {

    }

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Taxi objects
     */
    public List<Taxi> findAll() {
        return crud.findAll();
    }

    public Taxi create(Taxi taxi) throws Exception {
        // Write the booking to the database.
        return crud.create(taxi);
    }
}
