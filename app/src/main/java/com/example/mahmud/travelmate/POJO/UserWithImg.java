package com.example.mahmud.travelmate.POJO;

import java.io.Serializable;

public class UserWithImg implements Serializable {
    private String user;
    private String phone;
    private String ImageUrl;
    private String ImageName;
    public UserWithImg() {
    }

    public UserWithImg(String user, String phone,String ImageName, String ImageUrl) {
        this.user = user;
        this.phone = phone;
        this.ImageName = ImageName;
        this.ImageUrl = ImageUrl;
    }

    public UserWithImg(String user, String phone) {
        this.user = user;
        this.phone = phone;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
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
