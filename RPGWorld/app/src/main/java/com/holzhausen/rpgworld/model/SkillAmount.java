package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SkillAmount implements Serializable {

    @SerializedName("skillName")
    private String skillName;

    @SerializedName("skillAmount")
    private int skillAmount;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getSkillAmount() {
        return skillAmount;
    }

    public void setSkillAmount(int skillAmount) {
        this.skillAmount = skillAmount;
    }
}
