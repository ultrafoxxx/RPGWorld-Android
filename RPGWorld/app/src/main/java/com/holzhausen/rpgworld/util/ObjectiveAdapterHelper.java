package com.holzhausen.rpgworld.util;

import com.holzhausen.rpgworld.model.Message;

import java.util.List;

public interface ObjectiveAdapterHelper {

    List<Message> getNextMessages(Message previousMessage);

    void endActivity();

    void scrollToPosition(int position);

    boolean isTalkEvent();

}
