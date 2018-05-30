package com.example.biespiel_pc.and_2.Model;

/**
 * Created by Res Non Verba on 1/18/2018.
 */

public class User {
    private double latitude, longitude;
    private String uid, userName, pict, email;

    public User() {
    }

    public User(double latitude, double longitude, String uid, String userName, String pict) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
        this.userName = userName;
        this.pict = pict;

    }

    public String getPict() {
        return pict;
    }
    public  void setEmail(String email)
    {
        this.email = email;
    }

    public  String getEmail(){return  email;}

    public void setPict(String pict) {
        this.pict = pict;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUid() {
        return uid;
    }
}
