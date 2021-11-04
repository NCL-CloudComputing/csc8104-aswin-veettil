package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.util.RestServiceException;

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
    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return crud.findById(id);
    }
    /**
     * <p>Writes the provided Taxi object to the application database.<p/>
     *
     *
     * @param taxi The Taxi object to be written to the database using a {@link TaxiRepository} object
     * @return The Taxi object that has been successfully written to the application database
     * @throws Exception when proper data is not provided
     */
    public Taxi create(Taxi taxi) throws Exception {
        // Write the booking to the database.
        return crud.create(taxi);
    }

    public Taxi delete(Long taxiId) throws RestServiceException {
        Taxi taxi = crud.findById(taxiId);
        if(taxi != null && taxi.getId() != null) {
            crud.delete(taxi);
        } else {
            throw new RestServiceException("Invalid Taxi Id");
        }
        return taxi;
    }
}
