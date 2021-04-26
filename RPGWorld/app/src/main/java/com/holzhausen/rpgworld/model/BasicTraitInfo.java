package com.holzhausen.rpgworld.model;

import com.google.gson.annotations.SerializedName;

public class BasicTraitInfo extends Skill{

    @SerializedName("imageAssetName")
    private String imageAssetName;


    public String getImageAssetName() {
        return imageAssetName;
    }

    public void setImageAssetName(String imageAssetName) {
        this.imageAssetName = imageAssetName;
    }
}
