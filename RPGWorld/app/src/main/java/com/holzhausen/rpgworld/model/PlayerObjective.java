package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

public class PlayerObjective {

    @SerializedName("objective_id")
    private int objectiveId;

    @SerializedName("player_identifier")
    private String playerIdentifier;

    public int getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(int objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public void setPlayerIdentifier(String playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
    }
}
