package org.jboss.quickstarts.wfk.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "taxi_id", referencedColumnName = "id")
    private Taxi taxi;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

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

