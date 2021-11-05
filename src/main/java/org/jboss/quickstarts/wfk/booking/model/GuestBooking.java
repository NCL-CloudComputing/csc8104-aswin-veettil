package org.jboss.quickstarts.wfk.booking.model;

public class GuestBooking {
    private Booking booking;
    private Customer customer;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
