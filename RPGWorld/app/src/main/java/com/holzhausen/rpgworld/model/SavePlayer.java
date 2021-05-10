package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SavePlayer {

    @SerializedName("name")
    private String name;

    @SerializedName("genderName")
    private String genderName;

    @SerializedName("className")
    private String className;

    @SerializedName("backgroundName")
    private String backgroundName;

    @SerializedName("skills")
    private List<SkillAmount> skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public List<SkillAmount> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillAmount> skills) {
        this.skills = skills;
    }
}
