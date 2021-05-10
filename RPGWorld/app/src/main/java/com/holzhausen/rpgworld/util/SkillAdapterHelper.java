package com.holzhausen.rpgworld.util;

@FunctionalInterface
public interface SkillAdapterHelper {

    boolean updateRemainingSkill(int lastProgress, int newProgress);

}
