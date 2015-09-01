package com.malcolmcrum.spacetrader;

import java.util.List;

/**
 * Created by Malcolm on 9/1/2015.
 */
public class Skills {
    private static final int SKILL_BONUS = 3;
    private static final int CLOAK_BONUS = 2;

    public static int GetPilotSkill(List<Crew> crew, List<Gadget> gadgets, Difficulty d) {
        int maxSkill = crew.get(0).getPilotSkill();

        for (Crew member : crew) {
            if (member.getPilotSkill() > maxSkill) {
                maxSkill = member.getPilotSkill();
            }
        }

        if (gadgets.contains(Gadget.Navigation)) {
            maxSkill += Skills.SKILL_BONUS;
        }
        if (gadgets.contains(Gadget.Cloaking)) {
            maxSkill += CLOAK_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill, d);
    }

    private static int applyDifficultyModifierToSkill(int level, Difficulty d) {
        if (d == Difficulty.Beginner || d == Difficulty.Easy) {
            return level + 1;
        } else if (d == Difficulty.Impossible) {
            return Math.max(1, level - 1);
        } else {
            return level;
        }
    }

    public static int GetEngineerSkill(List<Crew> crew, List<Gadget> gadgets, Difficulty d) {
        int maxSkill = crew.get(0).getEngineerSkill();

        for (Crew member : crew) {
            if (member.getEngineerSkill() > maxSkill) {
                maxSkill = member.getEngineerSkill();
            }
        }

        if (gadgets.contains(Gadget.Repairs)) {
            maxSkill += SKILL_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill, d);
    }

    public static int GetFighterSkill(List<Crew> crew, List<Gadget> gadgets, Difficulty d) {
        int maxSkill = crew.get(0).getFighterSkill();

        for (Crew member : crew) {
            if (member.getFighterSkill() > maxSkill) {
                maxSkill = member.getFighterSkill();
            }
        }

        if (gadgets.contains(Gadget.Targeting)) {
            maxSkill += SKILL_BONUS;
        }

        return applyDifficultyModifierToSkill(maxSkill, d);
    }
}
