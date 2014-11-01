package com.example;

import domain.Student;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.print.Printable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jcohe_000 on 10/31/2014.
 */
@Path("students")
@Singleton
public class StudentResource {

    private Map<String, Student> studentCache = new ConcurrentSkipListMap<>();
    private AtomicInteger idCounter;


    @Path("/printNames")
         @GET
         @Produces("application/text")
         public String printNames() {
        String responseStr = "<Student list is empty>";

        StringBuilder builder = new StringBuilder();
        builder.append("Students:\n");
        if(studentCache.isEmpty()) {
            builder.append(responseStr + "\n");
        }
        else {
            for (String key : studentCache.keySet()) {
                builder.append(studentCache.get(key) + "\n");
            }
        }
        return builder.toString();
    }

    @Path("/records")
    @GET
    @Produces("application/text")
    //@Produces("application/json")
    public String printJSON() {

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (String key : studentCache.keySet()) {
            builder.append(studentCache.get(key));
            builder.append(",");
        }
        // Strip off last comma
        builder.deleteCharAt(builder.lastIndexOf(","));

        builder.append("]");
        return builder.toString();
    }


    @POST
    @Consumes("application/json")
    public Response createStudent(InputStream is) throws IOException {

        Student student = readStudent(is);
        studentCache.put(student.getLastName(), student);

        System.out.println("Student added: " + student);

        return Response.created(URI.create("/students/add")).build();
    }


    @GET
    @Path("{name}")
    @Produces("application/json")
    public StreamingOutput getStudent(@PathParam("name") String name) {
        final Student student = studentCache.get(name);
        if(student == null) {
            throw new WebApplicationException((Response.Status.NOT_FOUND));
        }
        return new StreamingOutput() {
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                outputStudent(outputStream, student);
            }
        };
    }


    @PUT
    @Path("{name}")
    @Consumes("application/json")
    public void updateStudent(@PathParam("name") String name, InputStream is) throws IOException {
        Student update = readStudent(is);
        Student current = studentCache.get(name);
        if(current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setId(update.getId());

        System.out.println("Update request rec'd");
        System.out.println("Was:" + current);
        System.out.println("Now:" + update);
    }

    /*  Utility functions */
    protected Student readStudent(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(is, Map.class);

        Student student = new Student();

        for(Map.Entry entry: jsonMap.entrySet()) {
            if(entry.getKey().toString().equalsIgnoreCase("ID")) {
                student.setId(Integer.parseInt(entry.getValue().toString()));
            }
            else if(entry.getKey().toString().equalsIgnoreCase("FIRSTNAME")) {
                student.setFirstName(entry.getValue().toString());
            }
            else if(entry.getKey().toString().equalsIgnoreCase("LASTNAME")) {
                student.setLastName(entry.getValue().toString());
            }
            else {
                System.out.println("Should never get here.");
            }
        }

        return student;
    }

    protected void outputStudent(OutputStream os, Student student)  {

        PrintStream writer = new PrintStream(os);
        writer.println("{");
        writer.println("    \"id\":" + "\"" + student.getId() + "\"," );
        writer.println("    \"firstName\":" + "\"" + student.getFirstName() + "\"," );
        writer.println("    \"lastName\":" + "\"" + student.getLastName() + "\"" );
        writer.println("}");

    }
}

