package com.example.biespiel_pc.and_2.Model;

/**
 * Created by Biespiel-PC on 11/26/2017.
 */

public class SignUpModel {
    private String  suUsername, suFullname, suEmail,suGender, suAge, supassword, sucopass;

    public SignUpModel(String suUsername, String suFullname, String suEmail, String suGender, String suAge, String supassword, String sucopass) {
        this.suUsername = suUsername;
        this.suFullname = suFullname;
        this.suEmail = suEmail;
        this.suGender = suGender;
        this.suAge = suAge;
        this.supassword = supassword;
        this.sucopass = sucopass;
    }

    public String getSuUsername() {
        return suUsername;
    }

    public String getSuFullname() {
        return suFullname;
    }

    public String getSuEmail() {
        return suEmail;
    }

    public String getSuGender() {
        return suGender;
    }

    public String getSuAge() {
        return suAge;
    }

    public String getSupassword() {
        return supassword;
    }

    public String getSucopass() {
        return sucopass;
    }
}
