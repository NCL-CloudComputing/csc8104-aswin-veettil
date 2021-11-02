package org.jboss.quickstarts.wfk.booking.service;

import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.repository.BookingRepository;

import javax.inject.Inject;

public class BookingService {
    @Inject
    private BookingRepository crud;

    public BookingService() {

    }

    public Booking create(Booking booking) throws Exception {
        // Write the booking to the database.
        return crud.create(booking);
    }
}
