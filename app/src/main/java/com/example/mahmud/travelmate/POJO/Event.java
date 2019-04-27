package com.example.mahmud.travelmate.POJO;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    private String Id;
    private String EventName;
    private String StartLocation;
    private String Destination;
    private String DepartureDate;
    private String CurrnetDate;
    private double Budget;

    public Event() {
    }

    public Event(String eventName, String startLocation, String destination, String departureDate,
                 double budget) {
        EventName = eventName;
        StartLocation = startLocation;
        Destination = destination;
        DepartureDate = departureDate;
        Budget = budget;
    }

    public Event(String id, String eventName, String startLocation, String destination,
                 String departureDate, double budget, String currnetDate) {
        Id = id;
        EventName = eventName;
        StartLocation = startLocation;
        Destination = destination;
        DepartureDate = departureDate;
        Budget = budget;
        CurrnetDate = currnetDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public void setStartLocation(String startLocation) {
        StartLocation = startLocation;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getDepartureDate() {
        return DepartureDate;
    }

    public void setDepartureDate(String departureDate) {
        DepartureDate = departureDate;
    }

    public double getBudget() {
        return Budget;
    }

    public void setBudget(double budget) {
        Budget = budget;
    }
    public String getCurrnetDate() {
        return CurrnetDate;
    }

    public void setCurrnetDate(String currnetDate) {
        CurrnetDate = currnetDate;
    }
}
