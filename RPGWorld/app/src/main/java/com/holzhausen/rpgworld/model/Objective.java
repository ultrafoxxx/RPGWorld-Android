package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

public class Objective {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("eventType")
    private String eventType;

    @SerializedName("order_in_quest")
    private int orderInQuest;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getOrderInQuest() {
        return orderInQuest;
    }

    public void setOrderInQuest(int orderInQuest) {
        this.orderInQuest = orderInQuest;
    }
}
