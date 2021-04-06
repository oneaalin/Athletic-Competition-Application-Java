package chat.model;

import java.io.Serializable;

public class Challenge extends Entity<Long> implements Serializable{

    private int minimumAge;
    private int maximumAge;
    private String name;

    /**
     * constructor
     * @param minimumAge the minimum age of a challenge
     * @param maximumAge the maximum age of a challenge
     * @param name the name of a challenge
     */
    public Challenge(int minimumAge, int maximumAge, String name) {
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.name = name;
    }

    /**
     * return the minumim age of a challenge
     * @return minimumAge int
     */
    public int getMinimumAge() {
        return minimumAge;
    }

    /**
     * set the minimum age of a challenge
     * @param minimumAge the minimum age of a challenge
     */
    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    /**
     * return the maximum age of a challenge
     * @return maximumAge int
     */
    public int getMaximumAge() {
        return maximumAge;
    }

    /**
     * set the maximum age of a challenge
     * @param maximumAge the maximum age of a challenge
     */
    public void setMaximumAge(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    /**
     * return the name of a challenge
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of a challenge
     * @param name the name of a challenge
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getId() + " " + minimumAge + " " + maximumAge + " " + name;
    }
}
