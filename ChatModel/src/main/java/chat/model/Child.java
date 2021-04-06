package chat.model;

import java.io.Serializable;

public class Child extends Entity<Long> implements Serializable{

    private String name;
    private int age;

    /**
     * constructor
     * @param name the name of a child
     * @param age the age of a child
     */
    public Child(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * return the name of a child
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of a child
     * @param name the name of a child
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * return the age of a child
     * @return age int
     */
    public int getAge() {
        return age;
    }

    /**
     * set the age of a child
     * @param age the age of a child
     */
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return getId() + " " + name + " " + age;
    }
}
