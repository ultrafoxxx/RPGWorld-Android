package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ObjectiveCompletionResponse {

    @SerializedName("player")
    private Player player;

    @SerializedName("quests")
    private List<Quest> quests;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }
}
