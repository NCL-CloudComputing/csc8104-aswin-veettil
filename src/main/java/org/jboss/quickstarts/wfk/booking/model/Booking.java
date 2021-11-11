package org.jboss.quickstarts.wfk.booking.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQueries({
    @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b"),
})
@Table(name = "booking")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking implements Serializable {
    public static final String FIND_ALL = "Booking.findAll";
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Future(message = "Booking date can not be in the past.")
    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    @ManyToOne
    @JoinColumn(name = "taxi_id", referencedColumnName = "id")
    @JsonProperty("taxiId")
    private Taxi taxi;

    @Column(name = "hotel_id")
    private Long hotel;

    @Column(name = "flight_id")
    private Long flight;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonProperty("customerId")
    private Customer customer;

    @JsonIgnore
    private Long travelAgentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getHotelId() {
        return hotel;
    }

    public void setHotelId(Long hotelId) {
        this.hotel = hotelId;
    }

    public Long getFlightId() {
        return flight;
    }

    public void setFlightId(Long flightId) {
        this.flight = flightId;
    }

    @JsonIgnore
    public Long getTravelAgentId() {
        return travelAgentId;
    }

    @JsonIgnore
    public void setTravelAgentId(Long travelAgentId) {
        this.travelAgentId = travelAgentId;
    }

    @JsonProperty("taxiId")
    private void setTaxiObject(Long taxiId) {
        Taxi taxi = new Taxi();
        taxi.setId(taxiId);
        this.setTaxi(taxi);
    }

    @JsonProperty("customerId")
    private void setCustomerObject(Long customerId) {
        Customer customer = new Customer();
        customer.setId(customerId);
        this.setCustomer(customer);
    }
}

