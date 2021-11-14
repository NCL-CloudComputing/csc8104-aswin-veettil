package org.jboss.quickstarts.wfk.booking;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.model.Customer;
import org.jboss.quickstarts.wfk.booking.model.Taxi;
import org.jboss.quickstarts.wfk.contact.UniqueEmailException;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link TaxiRestService#createTaxi(Taxi)} (Taxi)} ).<p/>
 *
 * @see TaxiRestService
 */
@RunWith(Arquillian.class)
public class TaxiRestServiceTest {
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
    TaxiRestService taxiSvc;

    @Test
    @InSequence(1)
    public void testRegisterTaxi() throws Exception {
        Response response = taxiSvc.createTaxi(createTaxiInstance("AXT8791", 6));

        assertEquals(201, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        try {
            taxiSvc.createTaxi(createTaxiInstance("", 0));
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateRegNo() throws Exception {
        // Register an initial taxi
        Taxi t1 = createTaxiInstance("AXT4512", 4);
        taxiSvc.createTaxi(t1);

        // Register a different taxi with the same regNo
        Taxi t2 = createTaxiInstance("AXT4512", 5);

        try {
            taxiSvc.createTaxi(t2);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be Unique email violation", e.getCause() instanceof UniqueRegNoException);
            assertEquals("Unexpected response body", 1, e.getReasons().size());
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testFindTaxi() throws Exception {
        Taxi t1 = createTaxiInstance("AXT1234", 6);
        taxiSvc.createTaxi(t1);

        Response res = taxiSvc.retrieveTaxiById(t1.getId());
        Taxi taxi = (Taxi)res.getEntity();
        assertEquals(200, res.getStatus());
        assertEquals(taxi.getId(), t1.getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testFindAllTaxis() throws Exception {
        Response res = taxiSvc.retrieveAllTaxis();
        assertEquals(200, res.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testDeleteTaxi() throws Exception {
        Taxi t1 = createTaxiInstance("AXT1235", 2);
        taxiSvc.createTaxi(t1);

        Response res = taxiSvc.deleteTaxi(t1.getId());
        assertEquals(204, res.getStatus());
    }

    private Taxi createTaxiInstance(String regNo, int noOfSeats) {
        Taxi taxi = new Taxi();
        taxi.setVehicleRegNo(regNo);
        taxi.setNoOfSeats(noOfSeats);
        return taxi;
    }
}
