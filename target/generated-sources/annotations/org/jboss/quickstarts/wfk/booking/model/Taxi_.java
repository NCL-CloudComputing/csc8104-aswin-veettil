package org.jboss.quickstarts.wfk.booking.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Taxi.class)
public abstract class Taxi_ {

	public static volatile SingularAttribute<Taxi, Integer> noOfSeats;
	public static volatile SingularAttribute<Taxi, Long> id;
	public static volatile ListAttribute<Taxi, Booking> bookings;
	public static volatile SingularAttribute<Taxi, String> vehicleRegNo;

}

