package org.jboss.quickstarts.wfk.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT t FROM Taxi t"),
})
@Table(name = "taxi")
public class Taxi implements Serializable {
    public static final String FIND_ALL = "Taxi.findAll";

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

    @JsonIgnore
    @OneToMany(mappedBy = "taxi", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public Taxi() {
    }

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

    @JsonIgnore
    public List<Booking> getBooking() {
        return bookings;
    }

    @JsonIgnore
    public void setBooking(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
