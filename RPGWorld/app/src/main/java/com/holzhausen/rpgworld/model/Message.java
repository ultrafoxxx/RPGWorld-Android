package com.holzhausen.rpgworld.model;

import android.graphics.Color;
import android.text.Spanned;

public class Message {

    private Spanned content;

    private int color;

    private boolean isPlayer;

    private boolean isLast;

    private boolean isDisabled;

    private String order;

    public Spanned getContent() {
        return content;
    }

    public void setContent(Spanned content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
