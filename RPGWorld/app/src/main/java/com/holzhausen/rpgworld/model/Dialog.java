package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

public class Dialog {

    @SerializedName("content")
    private String content;

    @SerializedName("traitGuard")
    private String traitGuard;

    @SerializedName("skill_guard")
    private String skillGuard;

    @SerializedName("skill_amount")
    private int skillAmount;

    @SerializedName("is_player")
    private boolean isPlayer;

    @SerializedName("order")
    private String order;

    @SerializedName("is_fight_start")
    private boolean isStartFight;

    @SerializedName("is_last")
    private boolean isLast;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTraitGuard() {
        return traitGuard;
    }

    public void setTraitGuard(String traitGuard) {
        this.traitGuard = traitGuard;
    }

    public String getSkillGuard() {
        return skillGuard;
    }

    public void setSkillGuard(String skillGuard) {
        this.skillGuard = skillGuard;
    }

    public int getSkillAmount() {
        return skillAmount;
    }

    public void setSkillAmount(int skillAmount) {
        this.skillAmount = skillAmount;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isStartFight() {
        return isStartFight;
    }

    public void setStartFight(boolean startFight) {
        isStartFight = startFight;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
