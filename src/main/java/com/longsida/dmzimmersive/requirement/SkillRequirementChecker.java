package com.longsida.dmzimmersive.requirement;

import com.dragonminez.common.stats.StatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SkillRequirementChecker {

    public static boolean meets(StatsData data, String skillName, int currentLevel) {
        SkillRequirement req = SkillRequirementManager.get(skillName);
        if (req == null) return true;

        for (var entry : req.stats.entrySet()) {
            String stat = entry.getKey();
            SkillRequirement.StatReq statReq = entry.getValue();
            int required = statReq.resolve(currentLevel);
            if (required <= 0) continue;

            int current = data.getCurrentStatValue(stat);
            if (current < required) return false;
        }
        return true;
    }

    public static Component buildRequirementMessage(StatsData data, String skillName, int currentLevel) {
        SkillRequirement req = SkillRequirementManager.get(skillName);
        if (req == null) return Component.literal("§c条件不满足");

        StringBuilder sb = new StringBuilder("§c需要: ");
        boolean first = true;
        for (var entry : req.stats.entrySet()) {
            String stat = entry.getKey();
            SkillRequirement.StatReq statReq = entry.getValue();
            int required = statReq.resolve(currentLevel);
            if (required <= 0) continue;

            int current = data.getCurrentStatValue(stat);
            if (current >= required) continue;

            if (!first) sb.append("§7, ");
            sb.append("§e").append(stat).append(" §f").append(current).append("§7/§f").append(required);
            first = false;
        }

        if (first) return Component.literal("§c条件不满足");
        return Component.literal(sb.toString());
    }

    public static Component buildRequirementComponent(StatsData data, String skillName, int currentLevel) {
        SkillRequirement req = SkillRequirementManager.get(skillName);
        if (req == null) return Component.empty();

        MutableComponent result = Component.empty();
        boolean first = true;
        for (var entry : req.stats.entrySet()) {
            String stat = entry.getKey();
            SkillRequirement.StatReq statReq = entry.getValue();
            int required = statReq.resolve(currentLevel);
            if (required <= 0) continue;

            int current = data.getCurrentStatValue(stat);
            if (!first) result.append(Component.literal("§7, "));

            MutableComponent line = Component.translatable(
                    "dmzimmersive.skill.requirement",
                    stat, current, required
            ).withStyle(current >= required ? ChatFormatting.GREEN : ChatFormatting.RED);
            result.append(line);
            first = false;
        }

        if (first) return Component.translatable("dmzimmersive.skill.requirement_met").withStyle(ChatFormatting.GREEN);
        return result;
    }

    /** 该技能是否有属性需求 */
    public static boolean hasRequirement(String skillName) {
        SkillRequirement req = SkillRequirementManager.get(skillName);
        if (req == null) return false;
        for (var entry : req.stats.entrySet()) {
            if (entry.getValue().resolve(0) > 0) return true;
        }
        return false;
    }

    /** 单个属性需求文本，如 "STR 10/30" */
    public static Component buildSingleStatComponent(StatsData data, String stat, int required) {
        int current = data.getCurrentStatValue(stat);
        return Component.translatable("dmzimmersive.skill.requirement", stat, current, required)
                .withStyle(current >= required ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}