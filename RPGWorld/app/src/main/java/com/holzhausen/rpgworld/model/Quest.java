package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Quest {

    @SerializedName("id")
    private int questId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("experience_reward")
    private int experienceReward;

    @SerializedName("min_level")
    private int minLevel;

    @SerializedName("active_objective")
    private int activeObjective;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("objectives")
    private List<Objective> objectives;

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

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

    public int getExperienceReward() {
        return experienceReward;
    }

    public void setExperienceReward(int experienceReward) {
        this.experienceReward = experienceReward;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getActiveObjective() {
        return activeObjective;
    }

    public void setActiveObjective(int activeObjective) {
        this.activeObjective = activeObjective;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
