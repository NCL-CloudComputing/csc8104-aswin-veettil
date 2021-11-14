package org.jboss.quickstarts.wfk.booking;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.quickstarts.wfk.util.UniqueRegNoException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link BookingRestService#createBooking(Booking)}).<p/>
 *
 * @see TaxiRestService
 */
@RunWith(Arquillian.class)
public class BookingRestServiceTest {
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
    BookingRestService bookingSvc;
    @Inject
    TaxiRestService taxiSvc;
    @Inject
    CustomerRestService customerRestService;

    Date currentDate = new Date();
    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(1);
    Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());


    @Test
    @InSequence(1)
    public void testRegisterBooking() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8790", 6));
        Taxi taxi = (Taxi) response.getEntity();
        response = customerRestService.createCustomer(createCustomerInstance("john101@doe.com", "John", "Doe", "(212) 555-1234"));
        Customer customer = (Customer)response.getEntity();
        Booking booking = createBookingInstance(taxi, customer);
        response = bookingSvc.createBooking(booking);
        assertEquals(201, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        try {
            Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8781", 6));
            Taxi taxi = (Taxi) response.getEntity();
            response = customerRestService.createCustomer(createCustomerInstance("john102@doe.com", "John", "Doe", "(212) 555-1234"));
            //set invalid customer
            Customer customer = new Customer();
            customer.setId((long)1234);
            Booking booking = createBookingInstance(taxi, customer);
            bookingSvc.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateBooking() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8782", 6));
        Taxi taxi = (Taxi) response.getEntity();
        response = customerRestService.createCustomer(createCustomerInstance("john105@doe.com", "John", "Doe", "(212) 555-1234"));
        Customer customer = (Customer)response.getEntity();
        Booking booking = createBookingInstance(taxi, customer);
        bookingSvc.createBooking(booking);
        try {
            bookingSvc.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testFindBooking() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8783", 6));
        Taxi taxi = (Taxi) response.getEntity();
        response = customerRestService.createCustomer(createCustomerInstance("john103@doe.com", "John", "Doe", "(212) 555-1234"));
        Customer customer = (Customer)response.getEntity();
        Booking booking = createBookingInstance(taxi, customer);
        bookingSvc.createBooking(booking);

        Response res = bookingSvc.retrieveTaxiById(booking.getId());
        Booking bkng = (Booking) res.getEntity();
        assertEquals(200, res.getStatus());
        assertEquals(booking.getId(), bkng.getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testFindAllTaxis() throws Exception {
        Response res = bookingSvc.retrieveAllBookings(null);
        assertEquals(200, res.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testDeleteBooking() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8784", 6));
        Taxi taxi = (Taxi) response.getEntity();
        response = customerRestService.createCustomer(createCustomerInstance("john104@doe.com", "John", "Doe", "(212) 555-1234"));
        Customer customer = (Customer)response.getEntity();
        Booking booking = createBookingInstance(taxi, customer);
        bookingSvc.createBooking(booking);

        Response res = bookingSvc.deleteBooking(booking.getId());
        assertEquals(204, res.getStatus());
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
