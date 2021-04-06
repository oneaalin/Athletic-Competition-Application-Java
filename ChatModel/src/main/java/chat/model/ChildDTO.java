package chat.model;

import java.io.Serializable;

public class ChildDTO implements Serializable{
    private String name;
    private int age;
    private int challengesNumber;

    public ChildDTO(String name, int age, int challengesNumber) {
        this.name = name;
        this.age = age;
        this.challengesNumber = challengesNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getChallengesNumber() {
        return challengesNumber;
    }

    public void setChallengesNumber(int challengesNumber) {
        this.challengesNumber = challengesNumber;
    }
}
