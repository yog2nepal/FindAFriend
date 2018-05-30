package com.example.biespiel_pc.and_2.Model;

/**
 * Created by Res Non Verba on 23/11/2017.
 */

public class Profile {
    private String userName, fullName, email, gender;
    private int age;

    public Profile() {
    }

    public Profile(String userName, String fullName, String email, String gender, int age) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
