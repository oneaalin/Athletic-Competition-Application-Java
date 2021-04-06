package chat.network.dtos;

import java.io.Serializable;

public class RegisterDTO implements Serializable {
    private String name;
    private int age;
    private String challenge1;
    private String challenge2;

    public RegisterDTO(String name, int age, String challenge1, String challenge2) {
        this.name = name;
        this.age = age;
        this.challenge1 = challenge1;
        this.challenge2 = challenge2;
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

    public String getChallenge1() {
        return challenge1;
    }

    public void setChallenge1(String challenge1) {
        this.challenge1 = challenge1;
    }

    public String getChallenge2() {
        return challenge2;
    }

    public void setChallenge2(String challenge2) {
        this.challenge2 = challenge2;
    }
}
