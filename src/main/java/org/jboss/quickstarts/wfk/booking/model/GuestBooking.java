package org.jboss.quickstarts.wfk.booking.model;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.Serializable;

@TransactionManagement(value= TransactionManagementType.BEAN)
public class GuestBooking implements Serializable {
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
