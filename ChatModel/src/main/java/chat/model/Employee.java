package chat.model;

import java.io.Serial;
import java.io.Serializable;

public class Employee extends Entity<Long> implements Serializable {

    private String username;
    private String password;

    /**
     * constructor
     * @param username the username of an employee
     * @param password the password of an employee
     */
    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * return the username of an employee
     * @return username String
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the username of an employee
     * @param username the username of an employee
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * return the password of an employee
     * @return password String
     */
    public String getPassword() {
        return password;
    }

    /**
     * set the password of an employee
     * @param password the password of an employee
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return getId() + " " + username + " " + password;
    }
}
