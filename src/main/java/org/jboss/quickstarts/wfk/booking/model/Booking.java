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
    @JsonIgnore
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Taxi taxi;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "flight_id")
    private Long flightId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @JsonIgnore
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Customer customer;

    @JsonIgnore
    @Column(name="is_travel_agent_booking")
    private boolean isTravelAgentBooking;

    @JsonIgnore
    private Long hotelBookingId;

    @JsonIgnore
    private Long flightBookingId;

    private Long taxiId;

    private Long customerId;

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
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public boolean isTravelAgentBooking() {
        return isTravelAgentBooking;
    }
    @JsonIgnore
    public void setTravelAgentBooking(boolean travelAgentBooking) {
        isTravelAgentBooking = travelAgentBooking;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
        Taxi taxi = new Taxi();
        taxi.setId(taxiId);
        this.setTaxi(taxi);
    }

    public Long getTaxiId() {
        return this.taxiId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
        Customer customer = new Customer();
        customer.setId(customerId);
        this.setCustomer(customer);
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    @JsonIgnore
    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    @JsonIgnore
    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }
}

