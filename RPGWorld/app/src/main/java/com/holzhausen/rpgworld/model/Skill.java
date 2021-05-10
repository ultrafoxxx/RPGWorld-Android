package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Skill implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("amount")
    private int skillAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSkillAmount() {
        return skillAmount;
    }

    public void setSkillAmount(int skillAmount) {
        this.skillAmount = skillAmount;
    }
}
