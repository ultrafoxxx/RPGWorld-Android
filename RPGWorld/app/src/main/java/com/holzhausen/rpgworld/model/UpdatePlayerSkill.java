package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdatePlayerSkill {

    @SerializedName("player_identifier")
    private String playerIdentifier;

    @SerializedName("remaining_skills")
    private int remainingSkills;

    @SerializedName("skill_amount")
    private List<SkillAmount> skillAmounts;

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public void setPlayerIdentifier(String playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }

    public int getRemainingSkills() {
        return remainingSkills;
    }

    public void setRemainingSkills(int remainingSkills) {
        this.remainingSkills = remainingSkills;
    }

    public List<SkillAmount> getSkillAmounts() {
        return skillAmounts;
    }

    public void setSkillAmounts(List<SkillAmount> skillAmounts) {
        this.skillAmounts = skillAmounts;
    }
}
