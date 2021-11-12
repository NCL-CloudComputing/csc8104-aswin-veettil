package org.jboss.quickstarts.wfk.booking.model.external.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight implements Serializable {
    public static final String FLIGHT_BASE_URL = "http://csc8104-build-stream-c0079023-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/";
    private Long id;
    private String number;
    private String departure;
    private String destination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
