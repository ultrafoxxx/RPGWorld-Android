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

    @SerializedName("background")
    private String background;

    @SerializedName("gender")
    private String gender;

    @SerializedName("hero_class")
    private String heroClass;

    @SerializedName("skills")
    private List<SkillAmount> skillAmountList;

    @SerializedName("attacks")
    private List<Attack> attacks;

    public boolean hasTrait(String trait){
        return background.equals(trait) || gender.equals(trait) || heroClass.equals(trait);
    }

    public boolean hasEnoughSkill(String skillName, int skillAmount) {
        return skillAmountList.stream().anyMatch(skill -> skill.getSkillName().equals(skillName)
                && skill.getSkillAmount() >= skillAmount);
    }

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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }
}
