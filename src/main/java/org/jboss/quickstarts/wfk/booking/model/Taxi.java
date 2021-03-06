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
    @NamedQuery(name = Taxi.FIND_BY_REG_NO, query = "SELECT t FROM Taxi t WHERE t.vehicleRegNo = :regNo"),
})
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "vehicle_reg_no"))
public class Taxi implements Serializable {
    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REG_NO = "Taxi.findByRegNo";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Size(min = 7, max = 7)
    @Pattern(regexp = "[A-Za-z-0-9-']+", message = "Please use a valid registration number")
    @Column(name = "vehicle_reg_no")
    private String vehicleRegNo;

    @NotNull
    @Column(name = "no_of_seats")
    private int noOfSeats;

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

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
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
