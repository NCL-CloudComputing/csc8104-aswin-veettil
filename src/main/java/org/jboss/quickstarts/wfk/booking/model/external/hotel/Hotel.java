package org.jboss.quickstarts.wfk.booking.model.external.hotel;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hotel implements Serializable {
    public static final String HOTEL_BASE_URL = "http://csc8104-build-stream-igorwieczorek-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/";
    private Long id;
    private String name;
    private String phoneNumber;
    private String postCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
