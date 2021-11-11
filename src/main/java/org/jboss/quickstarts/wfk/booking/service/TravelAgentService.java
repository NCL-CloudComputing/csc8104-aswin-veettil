package org.jboss.quickstarts.wfk.booking.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.model.TravelAgent;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.repository.CustomerRepository;
import org.jboss.quickstarts.wfk.booking.repository.TaxiRepository;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelAgentService {
    @Inject
    private BookingService bookingSvc;
    @Inject
    private BookingRepository bookingCrud;

    private ResteasyClient client;

    private static final String HOTEL_BASE_URL = "http://csc8104-build-stream-aswinkvncl-dev.apps.sandbox.x8i5.p1.openshiftapps.com";
    private static final String FLIGHT_BASE_URL = "http://csc8104-build-stream-aswinkvncl-dev.apps.sandbox.x8i5.p1.openshiftapps.com";

    public TravelAgentService() {
        // Create client service instance to make REST requests to upstream service
        client = new ResteasyClientBuilder().build();
    }
    /**
     * <p>Returns a List of all persisted {@link TravelAgent} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of TravelAgent objects
     */
    public List<Booking> findAll() {
        //TODO: Change to booking svc
        Map<String, Object> fieldNameToVal = new HashMap<String, Object>() {{
            put("travelAgentId", TravelAgent.TRAVEL_AGENT_ID);
        }};
        return bookingCrud.findAllByCriteria(fieldNameToVal);
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
        bookingSvc.create(booking);
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
