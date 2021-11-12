package org.jboss.quickstarts.wfk.booking.model.external.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight implements Serializable {
    public static final String FLIGHT_BASE_URL = "http://csc8104-build-stream-c0079023-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/";
    private Long id;
    private String departurePoint;
    private String destination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
