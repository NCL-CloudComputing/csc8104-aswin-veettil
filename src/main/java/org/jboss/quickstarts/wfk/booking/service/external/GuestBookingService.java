package org.jboss.quickstarts.wfk.booking.service.external;

import org.jboss.quickstarts.wfk.booking.model.external.flight.FlightBooking;
import org.jboss.quickstarts.wfk.booking.model.external.flight.FlightGuestBooking;
import org.jboss.quickstarts.wfk.booking.model.external.hotel.HotelGuestBooking;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GuestBookingService {
    @Path("/guestBookings")
    @POST
    HotelGuestBooking createBooking(@QueryParam("guest email") String email, @QueryParam("guest phone number") String phNo,
                                    @QueryParam("guest first name") String fName, @QueryParam("guest surname") String sName,
                                    HotelGuestBooking guestBooking);
    @Path("/bookings/{id:[0-9]+}")
    @DELETE
    void deleteHotelBooking(@PathParam("id") Long id);

    @Path("/guest-booking")
    @POST
    FlightBooking createBooking(FlightGuestBooking guestBooking);

    @Path("/bookings/{id:[0-9]+}")
    @DELETE
    void deleteFlightBooking(@PathParam("id") Long id);
}
