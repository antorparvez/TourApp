package com.example.mahmud.travelmate.POJO;

public class Expense {
    private String Id;
    private String Name;
    private Double Cost;

    public Expense() {
    }
    public Expense(String id, String name, Double cost) {
        Id = id;
        Name = name;
        Cost = cost;
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

    public Double getCost() {
        return Cost;
    }

    public void setCost(Double cost) {
        Cost = cost;
    }
}
