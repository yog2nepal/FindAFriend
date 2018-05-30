package com.example.biespiel_pc.and_2.Model;

/**
 * Created by Res Non Verba on 22/12/2017.
 */

public class LoginModel {
    private String lemail, lpass;

    public LoginModel(String lemail, String lpass) {
        this.lemail = lemail;
        this.lpass = lpass;
    }

    public String getLemail() {
        return lemail;
    }

    public String getLpass() {
        return lpass;
    }
}
