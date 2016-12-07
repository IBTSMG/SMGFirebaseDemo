package com.example.IbtechSMG.FirebaseDemo;

/**
 * Created by smg on 23/09/16.
 */

public class User {
    private String userName;
    private String phoneNumber;
    private String image;

    public User(String userName, String phoneNumber, String image) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setImage(String image) { this.image = image; }

    public String getImage() { return this.image; }
}
