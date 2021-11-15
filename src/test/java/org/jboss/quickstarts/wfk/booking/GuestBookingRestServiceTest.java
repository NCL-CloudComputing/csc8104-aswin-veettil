package org.jboss.quickstarts.wfk.booking;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.model.Booking;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.GuestBooking;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.util.RestServiceException;
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
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link GuestBookingRestService#createGuestBooking(GuestBooking)}).<p/>
 *
 * @see GuestBookingRestService
 */
@RunWith(Arquillian.class)
public class GuestBookingRestServiceTest {
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
    GuestBookingRestService guestBookingRestService;
    @Inject
    TaxiRestService taxiSvc;
    @Inject
    CustomerRestService customerSvc;

    Date currentDate = new Date();
    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(1);
    Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());


    @Test
    @InSequence(1)
    public void testRegisterBookingNewCustomer() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("BXT8790", 6));
        Taxi taxi = (Taxi) response.getEntity();
        Customer customer = createCustomerInstance("john201@doe.com", "John", "Doe", "(212) 555-1234");
        Booking booking = createBookingInstance(taxi);
        GuestBooking gb = createGuestBookingInstance(booking, customer);
        response = guestBookingRestService.createGuestBooking(gb);
        assertEquals(201, response.getStatus());
    }

    @Test
    @InSequence(2)
    public void testRegisterBookingExistingCustomer() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("BXT8791", 6));
        Taxi taxi = (Taxi) response.getEntity();
        Customer customer = createCustomerInstance("john202@doe.com", "John", "Doe", "(212) 555-1234");
        customerSvc.createCustomer(customer);
        Booking booking = createBookingInstance(taxi);
        booking.setCustomerId(customer.getId());
        booking.setCustomer(customer);
        GuestBooking gb = createGuestBookingInstance(booking, customer);
        response = guestBookingRestService.createGuestBooking(gb);
        assertEquals(201, response.getStatus());
    }

    @Test
    @InSequence(3)
    public void testInvalidGuestBooking() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("BXT8792", 6));
        Taxi taxi = (Taxi) response.getEntity();
        Customer customer = createCustomerInstance("john203@doe.com", "John", "Doe", "(212) 555-1234");
        customerSvc.createCustomer(customer);
        Booking booking = createBookingInstance(taxi);
        booking.setCustomerId(customer.getId());
        booking.setCustomer(customer);
        booking.setBookingDate(new Date());
        GuestBooking gb = createGuestBookingInstance(booking, customer);
        try {
            guestBookingRestService.createGuestBooking(gb);
        }
        catch (RestServiceException e){
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
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
    private Booking createBookingInstance(Taxi taxi) {
        Booking booking = new Booking();
        booking.setTaxiId(taxi.getId());
        booking.setTaxi(taxi);
        booking.setBookingDate(date);
        return booking;
    }
    private GuestBooking createGuestBookingInstance(Booking booking, Customer customer) {
        GuestBooking guestBooking = new GuestBooking();
        guestBooking.setCustomer(customer);
        guestBooking.setBooking(booking);
        return guestBooking;
    }
}
