package org.jboss.quickstarts.wfk.booking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Table(name = "taxi")
public class Taxi implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 7)
    @Pattern(regexp = "[A-Za-z-0-9-']+", message = "Please use a valid registration number")
    @Column(name = "vehicle_reg_no")
    private String vehicleRegNo;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[0-9-']+", message = "Please specify a valid number of seats")
    @Column(name = "noo_of_seats")
    private String noOfSeats;

    @NotNull
    @Column(name = "booking_id")
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Booking booking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    public void setVehicleRegNo(String vehicleRegNo) {
        this.vehicleRegNo = vehicleRegNo;
    }

    public String getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(String noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
