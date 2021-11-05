package org.jboss.quickstarts.wfk.booking.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Customer.class)
public abstract class Customer_ {

	public static volatile SingularAttribute<Customer, String> firstName;
	public static volatile SingularAttribute<Customer, String> lastName;
	public static volatile SingularAttribute<Customer, Long> id;
	public static volatile ListAttribute<Customer, Booking> bookings;
	public static volatile SingularAttribute<Customer, String> email;

}

