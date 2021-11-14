package org.jboss.quickstarts.wfk.booking;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
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

import static org.junit.Assert.*;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link CustomerRestService#createCustomer(Customer)} ).<p/>
 *
 * @see CustomerRestService
 */
@RunWith(Arquillian.class)
public class CustomerRestServiceTest {
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
    CustomerRestService customerRestService;

    @Test
    @InSequence(1)
    public void testRegisterCustomer() throws Exception {
        Customer customer = createCustomerInstance("john1@doe.com", "John", "Doe", "(212) 555-1234");
        Response response = customerRestService.createCustomer(customer);

        assertEquals("Unexpected response status", 201, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        Customer customer = createCustomerInstance("", "", "", "");
        try {
            customerRestService.createCustomer(customer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("jane2@doe.com","Jane", "Doe", "(212) 555-1234");
        customerRestService.createCustomer(customer);

        // Register a different user with the same email
        Customer anotherCustomer = createCustomerInstance("jane2@doe.com", "John", "Doe", "(213) 355-1234");

        try {
            customerRestService.createCustomer(anotherCustomer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be Unique email violation", e.getCause() instanceof UniqueEmailException);
            assertEquals("Unexpected response body", 1, e.getReasons().size());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testFindCustomer() throws Exception {
        Customer customer = createCustomerInstance("jane3@doe.com","Jane", "Doe", "(212) 555-1234");
        customerRestService.createCustomer(customer);

        Response res = customerRestService.retrieveCustomerById(customer.getId());
        Customer c = (Customer)res.getEntity();
        assertEquals(200, res.getStatus());
        assertEquals(c.getId(), customer.getId());

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testFindAllCustomer() throws Exception {
        Response res = customerRestService.retrieveAllCustomers(null, null);
        assertEquals(200, res.getStatus());
    }

    private Customer createCustomerInstance(String email, String fName, String lName, String phoneNo) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(fName);
        customer.setLastName(lName);
        customer.setPhoneNumber(phoneNo);
        return customer;
    }
}
