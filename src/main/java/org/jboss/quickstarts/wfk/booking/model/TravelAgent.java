package org.jboss.quickstarts.wfk.booking.model;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelAgent {
    public static final Long TRAVEL_AGENT_ID = 895265816L;
    public static final String EXT_TRAVEL_AGENT_EMAIL = "nclTravels@ncl.ac.uk";

    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        booking.setTravelAgentId(TRAVEL_AGENT_ID);
        this.booking = booking;
    }
}