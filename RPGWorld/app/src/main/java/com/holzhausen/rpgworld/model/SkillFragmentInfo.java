package com.holzhausen.rpgworld.model;

import java.util.List;

public class SkillFragmentInfo {

    private final String name;

    private final List<Skill> skills;

    public SkillFragmentInfo(String name, List<Skill> amount) {
        this.name = name;
        this.skills = amount;
    }

    public String getName() {
        return name;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
