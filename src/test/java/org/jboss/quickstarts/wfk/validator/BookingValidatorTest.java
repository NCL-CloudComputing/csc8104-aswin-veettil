package org.jboss.quickstarts.wfk.validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.BookingRestService;
import org.jboss.quickstarts.wfk.booking.CustomerRestService;
import org.jboss.quickstarts.wfk.booking.TaxiRestService;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.booking.validate.BookingValidator;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * (see {@link BookingValidator).<p/>
 */
@RunWith(Arquillian.class)
public class BookingValidatorTest {
    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        // This is currently not well tested. If you run into issues, comment line 67 (the contents of 'resolve') and
        // uncomment 65. This will build our war with all dependencies instead.
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
//                .importRuntimeAndTestDependencies()
                .resolve(
                        "io.swagger:swagger-jaxrs:1.5.16"
                ).withTransitivity().asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    BookingValidator validator;
    @Inject
    TaxiRestService taxiSvc;
    @Inject
    CustomerRestService customerRestService;

    Date currentDate = new Date();
    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(1);
    Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

    @Test
    @InSequence(1)
    public void testBookingValidation() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8790", 6));
        Taxi taxi = (Taxi) response.getEntity();
        response = customerRestService.createCustomer(createCustomerInstance("john101@doe.com", "John", "Doe", "09876543210"));
        Customer customer = (Customer)response.getEntity();
        Booking booking = createBookingInstance(taxi, customer);
        validator.validateBooking(booking);
    }

    @Test
    @InSequence(2)
    public void testInvalidRegisterValidation() throws Exception {
        try {
            Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8781", 6));
            Taxi taxi = (Taxi) response.getEntity();
            response = customerRestService.createCustomer(createCustomerInstance("john9999@doe.com", "John", "Doe", "09876543210"));
            //set invalid customer
            Customer customer = new Customer();
            customer.setId((long)1234);
            Booking booking = createBookingInstance(taxi, customer);
            validator.validateBooking(booking);
            fail("Expected a ValidationException to be thrown");
        } catch(ValidationException e) {
            assertEquals("Unexpected response status", "Customer with the given id does not exist", e.getMessage());
        }

    }

    private Taxi createTaxiInstance(String regNo, int noOfSeats) {
        Taxi taxi = new Taxi();
        taxi.setVehicleRegNo(regNo);
        taxi.setNoOfSeats(noOfSeats);
        return taxi;
    }
    private Customer createCustomerInstance(String email, String fName, String lName, String phoneNo) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(fName);
        customer.setLastName(lName);
        customer.setPhoneNumber(phoneNo);
        return customer;
    }
    private Booking createBookingInstance(Taxi taxi, Customer customer) {
        Booking booking = new Booking();
        booking.setCustomerId(customer.getId());
        booking.setCustomer(customer);
        booking.setTaxiId(taxi.getId());
        booking.setTaxi(taxi);
        booking.setBookingDate(date);
        return booking;
    }
}
