package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.model.TravelAgent;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.util.RestServiceException;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.List;

public class TravelAgentService {
    @Inject
    private BookingRepository bookingCrud;
    @Inject
    private TaxiRepository taxiCrud;
    @Inject
    private CustomerRepository custCrud;

    public TravelAgentService() {

    }
    /**
     * <p>Returns a List of all persisted {@link TravelAgent} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of TravelAgent objects
     */
    public List<Booking> findAll() {
        return bookingCrud.findAllByCriteria("travelAgentId", TravelAgent.TRAVEL_AGENT_ID);
    }

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     *
     * @param travelAgent The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public TravelAgent create(TravelAgent travelAgent) throws Exception {
        //TODO: add logic for flight and hotel
        Booking booking = travelAgent.getBooking();
        booking.setTravelAgentId(TravelAgent.TRAVEL_AGENT_ID);
        bookingCrud.create(booking);
        return travelAgent;
    }

//    public TravelAgent delete(Long bookingId) throws RestServiceException {
//        TravelAgent booking = crud.findById(bookingId);
//        if(booking != null && booking.getId() != null) {
//            crud.delete(booking);
//        } else {
//            throw new RestServiceException("Invalid Booking Id");
//        }
//        return booking;
//    }
}
