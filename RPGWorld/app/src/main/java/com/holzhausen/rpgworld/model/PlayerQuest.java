package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

public class PlayerQuest {

    @SerializedName("player_identifier")
    private String playerIdentifier;

    @SerializedName("quest_id")
    private int questId;

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public void setPlayerIdentifier(String playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }
}
