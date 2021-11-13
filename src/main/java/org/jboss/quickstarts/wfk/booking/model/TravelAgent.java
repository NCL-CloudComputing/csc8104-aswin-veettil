package org.jboss.quickstarts.wfk.booking.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelAgent implements Serializable {
    public static final Long TRAVEL_AGENT_ID = 895265816L;
    public static final String EXT_TRAVEL_AGENT_EMAIL = "ncltravels@ncl.ac.uk";
    public static final String EXT_TRAVEL_AGENT_PHNO = "06765767659";
    public static final String EXT_TRAVEL_AGENT_FIRSTNAME = "Travel";
    public static final String EXT_TRAVEL_AGENT_LASTNAME = "Agent";
    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        booking.setTravelAgentId(TRAVEL_AGENT_ID);
        this.booking = booking;
    }
}
