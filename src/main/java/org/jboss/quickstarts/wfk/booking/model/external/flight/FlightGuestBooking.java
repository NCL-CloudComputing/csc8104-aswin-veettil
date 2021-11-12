package org.jboss.quickstarts.wfk.booking.model.external.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightGuestBooking implements Serializable {
    private FlightBooking booking;
    private FlightCustomer customer;

    public FlightBooking getBooking() {
        return booking;
    }

    public void setBooking(FlightBooking booking) {
        this.booking = booking;
    }

    public FlightCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(FlightCustomer customer) {
        this.customer = customer;
    }
}
