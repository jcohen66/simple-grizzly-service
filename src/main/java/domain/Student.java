package domain;

import java.io.PrintStream;

/**
 * Created by jcohe_000 on 10/31/2014.
 */
public class Student {
    private Integer id;
    private String firstName;
    private String lastName;

    public Student() {
    }

    public Student(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        StringBuilder writer = new StringBuilder();
        writer.append("{");
        writer.append("\"id\":" + "\"" + getId() + "\",");
        writer.append("\"firstName\":" + "\"" + getFirstName() + "\",");
        writer.append("\"lastName\":" + "\"" + getLastName() + "\"");
        writer.append("}");
        return writer.toString();
    }
}
