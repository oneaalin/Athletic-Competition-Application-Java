package chat.model;

import java.io.Serial;
import java.io.Serializable;

public class ChallengeDTO implements Serializable {
    private int minimumAge;
    private int maximumAge;
    private String name;
    private int childsNumber;

    public ChallengeDTO(int minimumAge, int maximumAge, String name, int childsNumber) {
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
        this.name = name;
        this.childsNumber = childsNumber;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChildsNumber() {
        return childsNumber;
    }

    public void setChildsNumber(int childsNumber) {
        this.childsNumber = childsNumber;
    }
}
