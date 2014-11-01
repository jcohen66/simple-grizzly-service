package com.example;

import javassist.NotFoundException;
import junit.framework.Assert;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jcohe_000 on 10/31/2014.
 */
public class StudentResourceTest {
    private HttpServer server;
    private WebTarget target;
    private Client client;

    @Before
    public void setUp() throws Exception {
        // start the server as a Grizzly container.
        server = Main.startServer();

        // create the client
        client = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = client.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testPrintNames() {
        // Connect to MyResource
        String responseMsg = target.path("students/printNames").request().get(String.class);

        // Compare the result.
        assertNotNull(responseMsg);
    }

    @Test
    public void testAddStudent() {
        System.out.println("**** Create some new Students ****");

        StringBuilder newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Bill\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Burke\"");
        newStudentJSON.append("}");

        Response response = client.target(
                "http://localhost:8080/myapp/students")
                .request().post(Entity.json(newStudentJSON.toString())
                );

        newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Amy\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Smith\"");
        newStudentJSON.append("}");

        response = client.target(
                "http://localhost:8080/myapp/students")
                .request().post(Entity.json(newStudentJSON.toString())
                );

        newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Derek\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Bonas\"");
        newStudentJSON.append("}");

        response = client.target(
                "http://localhost:8080/myapp/students")
                .request().post(Entity.json(newStudentJSON.toString())
                );

        String location = response.getLocation().toString();
        System.out.println("Location: " + location);

        Assert.assertEquals(response.getStatus(), 201);

        // Lookup all of the names in the cache.
        String responseStr = client.target(
                "http://localhost:8080/myapp/students/printNames")
                .request().get(String.class);

        System.out.println(responseStr);

    }

    @Test
    public void testGetStudent() {

        System.out.println("**** GET Created Student ****");

        StringBuilder newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Bill\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Burke\"");
        newStudentJSON.append("}");

        Response response = client.target(
                "http://localhost:8080/myapp/students")
                .request().post(Entity.json(newStudentJSON.toString())
                );
        String student = client.target("http://localhost:8080/myapp/students/Burke").request().get(String.class);
        System.out.println(student);
        Assert.assertNotNull(student);
    }

    @Test
    public void testGetStudentNoFind() {
        System.out.println("**** GET Nonexistent Student ****");

        StringBuilder newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Bill\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Burke\"");
        newStudentJSON.append("}");

        Response response = client.target(
                "http://localhost:8080/myapp/students")
                .request().post(Entity.json(newStudentJSON.toString())
                );

        response = client.target("http://localhost:8080/myapp/students/Zodiac").request().get(Response.class);

        Assert.assertEquals(response.getStatus(), 404);
    }

    @Test
    public void testUpdateStudent() {
        System.out.println("**** UPDATE Student ****");

        StringBuilder newStudentJSON = new StringBuilder();
        newStudentJSON.append("{");
        newStudentJSON.append("\"id\": " + "1" + ",");
        newStudentJSON.append("\"firstName\": " + "\"Bob\"" + ",");
        newStudentJSON.append("\"lastName\": " + "\"Burke\"");
        newStudentJSON.append("}");

        Response response = client.target(
                "http://localhost:8080/myapp/students/Burke")
                .request().put(Entity.json(newStudentJSON.toString())
                );

        response = client.target("http://localhost:8080/myapp/students/Zodiac").request().get(Response.class);

        // Lookup all of the names in the cache.
        String responseStr = client.target(
                "http://localhost:8080/myapp/students/printNames")
                .request().get(String.class);

        System.out.println(responseStr);

        Assert.assertEquals(response.getStatus(), 404);
    }

}
