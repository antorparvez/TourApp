package com.example.mahmud.travelmate.POJO;

public class UserGeofence {
    private String Name;
    private String Id;
    private String StoppingTime;
    private String Radius;
    private String Latitude;
    private String Longitude;

    public UserGeofence() {
    }

    public UserGeofence(String name, String id, String stoppingTime, String radius, String latitude, String longitude) {
        Name = name;
        Id = id;
        Radius = radius;
        Latitude = latitude;
        Longitude = longitude;
        StoppingTime = stoppingTime;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStoppingTime() {
        return StoppingTime;
    }

    public void setStoppingTime(String stoppingTime) {
        StoppingTime = stoppingTime;
    }

    public String getRadius() {
        return Radius;
    }

    public void setRadius(String radius) {
        Radius = radius;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
