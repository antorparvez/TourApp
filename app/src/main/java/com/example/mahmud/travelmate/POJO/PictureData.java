package com.example.mahmud.travelmate.POJO;

import android.net.Uri;

public class PictureData {
    private String Id;
    private String Name;
    private String DateTime;
    private String URL;

    public PictureData() {
    }

    public PictureData(String id, String name, String dateTime, String URL) {
        Id = id;
        Name = name;
        DateTime = dateTime;
        this.URL = URL;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
