package com.example.mahmud.travelmate.POJO;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String phone;

    public User() {
    }

    public User(String user, String phone) {
        this.user = user;
        this.phone = phone;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}