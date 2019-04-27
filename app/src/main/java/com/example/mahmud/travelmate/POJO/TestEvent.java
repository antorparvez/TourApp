package com.example.mahmud.travelmate.POJO;

public class TestEvent {
    private String id;
    private String name;
    private String date;
    private double cost;

    public TestEvent() {
    }

    public TestEvent(String id, String name, String date, double cost) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


}
