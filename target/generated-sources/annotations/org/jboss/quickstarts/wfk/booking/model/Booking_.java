package org.jboss.quickstarts.wfk.booking.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Booking.class)
public abstract class Booking_ {

	public static volatile SingularAttribute<Booking, Boolean> isTravelAgentBooking;
	public static volatile SingularAttribute<Booking, Long> flightBookingId;
	public static volatile SingularAttribute<Booking, Taxi> taxi;
	public static volatile SingularAttribute<Booking, Long> flightId;
	public static volatile SingularAttribute<Booking, Date> bookingDate;
	public static volatile SingularAttribute<Booking, Long> id;
	public static volatile SingularAttribute<Booking, Long> hotelId;
	public static volatile SingularAttribute<Booking, Long> hotelBookingId;
	public static volatile SingularAttribute<Booking, Customer> customer;

}

