package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.TravelAgent;
import org.jboss.quickstarts.wfk.booking.model.external.flight.Flight;
import org.jboss.quickstarts.wfk.booking.model.external.flight.FlightBooking;
import org.jboss.quickstarts.wfk.booking.model.external.flight.FlightCustomer;
import org.jboss.quickstarts.wfk.booking.model.external.flight.FlightGuestBooking;
import org.jboss.quickstarts.wfk.booking.model.external.hotel.Hotel;
import org.jboss.quickstarts.wfk.booking.model.external.hotel.HotelGuestBooking;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;
import org.jboss.quickstarts.wfk.booking.service.external.GuestBookingService;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelAgentService {
    @Inject
    private BookingService bookingSvc;

    private ResteasyClient client;

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
        Map<String, Object> fieldNameToVal = new HashMap<String, Object>() {{
            put("travelAgentId", TravelAgent.TRAVEL_AGENT_ID);
        }};
        return bookingSvc.findAllByCriteria(fieldNameToVal);
    }

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     *
     * @param travelAgent The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws RestServiceException when service throws error
     */
    public TravelAgent create(TravelAgent travelAgent) throws RestServiceException {
        Booking booking = travelAgent.getBooking();

        ResteasyWebTarget hotelTarget = client.target(Hotel.HOTEL_BASE_URL);
        GuestBookingService hotelService = hotelTarget.proxy(GuestBookingService.class);

        ResteasyWebTarget flightTarget = client.target(Flight.FLIGHT_BASE_URL);
        GuestBookingService flightService = flightTarget.proxy(GuestBookingService.class);

        if(booking.getHotelId() != null) {
            createHotelBooking(booking, hotelService);
        }
        if(booking.getFlightId() != null) {
            createFlightBooking(booking, hotelService, flightService);
        }

        booking.setTravelAgentId(TravelAgent.TRAVEL_AGENT_ID);
        try {
            bookingSvc.create(booking);
        } catch (Exception e) {
            if(booking.getHotelId() != null) {
                hotelService.deleteHotelBooking(booking.getHotelBookingId());
            }
            if(booking.getFlightId() != null) {
                flightService.deleteFlightBooking(booking.getHotelBookingId());
            }
            throw new RestServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
        return travelAgent;
    }

    private void createFlightBooking(Booking booking, GuestBookingService hotelService, GuestBookingService flightService) {
        try {
            FlightGuestBooking fGb = createFlightGuestBookingPayload(booking);
            FlightBooking flightResponse = flightService.createBooking(fGb);
            booking.setFlightBookingId(flightResponse.getId());
        } catch (Exception e) {
            if(booking.getHotelId() != null) {
                hotelService.deleteHotelBooking(booking.getHotelBookingId());
            }
            throw new RestServiceException("Error during creation of flight booking. Transaction cancelled.", Response.Status.BAD_REQUEST);
        }
    }

    private void createHotelBooking(Booking booking, GuestBookingService hotelService) {
        try {
            HotelGuestBooking hGb = createHotelGuestBookingPayload(booking);
            HotelGuestBooking hotelResponse = hotelService.createBooking(TravelAgent.EXT_TRAVEL_AGENT_EMAIL, TravelAgent.EXT_TRAVEL_AGENT_PHNO,
                    TravelAgent.EXT_TRAVEL_AGENT_FIRSTNAME, TravelAgent.EXT_TRAVEL_AGENT_LASTNAME, hGb);
            booking.setHotelBookingId(hotelResponse.getId());
        } catch (Exception e) {
            throw new RestServiceException("Error during creation of hotel booking. Transaction cancelled.", Response.Status.BAD_REQUEST);
        }
    }

    private HotelGuestBooking createHotelGuestBookingPayload(Booking booking) {
        HotelGuestBooking hGb = new HotelGuestBooking();
        hGb.setHotelId(String.valueOf(booking.getHotelId()));
        hGb.setStartDate(booking.getBookingDate());
        return hGb;
    }

    private FlightGuestBooking createFlightGuestBookingPayload(Booking booking) {
        FlightCustomer travelAgentCust = new FlightCustomer();
        travelAgentCust.setName(TravelAgent.EXT_TRAVEL_AGENT_FIRSTNAME + " " + TravelAgent.EXT_TRAVEL_AGENT_LASTNAME);
        travelAgentCust.setEmail(TravelAgent.EXT_TRAVEL_AGENT_EMAIL);
        travelAgentCust.setPhoneNumber(TravelAgent.EXT_TRAVEL_AGENT_PHNO);
        FlightBooking fBooking = new FlightBooking();
        fBooking.setFlightId(booking.getFlightId());
        fBooking.setDate(booking.getBookingDate());
        FlightGuestBooking fGb = new FlightGuestBooking();
        fGb.setBooking(fBooking);
        fGb.setCustomer(travelAgentCust);
        return fGb;
    }
}
