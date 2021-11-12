package org.jboss.quickstarts.wfk.booking.model;

import java.io.Serializable;

public class Hotel implements Serializable {
    public static final String HOTEL_BASE_URL = "http://csc8104-build-stream-akshayamathur-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/";
    private Long id;
    private String names;
    private String phoneNumber;
    private String pincode;
}
