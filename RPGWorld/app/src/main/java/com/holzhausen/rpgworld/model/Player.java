package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("name")
    private String name;

    @SerializedName("level")
    private int level;

    @SerializedName("experiencePoints")
    private int experiencePoints;

    @SerializedName("skillPoints")
    private int skillPoints;

    @SerializedName("skills")
    private List<SkillAmount> skillAmountList;

    @SerializedName("attacks")
    private List<Attack> attacks;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public List<SkillAmount> getSkillAmountList() {
        return skillAmountList;
    }

    public void setSkillAmountList(List<SkillAmount> skillAmountList) {
        this.skillAmountList = skillAmountList;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<Attack> attacks) {
        this.attacks = attacks;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }
}
