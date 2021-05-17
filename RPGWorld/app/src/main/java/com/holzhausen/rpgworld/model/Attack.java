package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attack implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("max_damage")
    private int maxDamage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    @Override
    public String toString() {
        return name + " (" + maxDamage + " HP)";
    }
}
