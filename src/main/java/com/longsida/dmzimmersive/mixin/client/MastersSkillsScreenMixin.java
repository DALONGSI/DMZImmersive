package com.longsida.dmzimmersive.mixin.client;

import com.dragonminez.client.gui.MastersSkillsScreen;
import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.stats.StatsData;
import com.longsida.dmzimmersive.client.SpClientCache;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Locale;

@Mixin(MastersSkillsScreen.class)
public abstract class MastersSkillsScreenMixin {

    @Shadow private StatsData statsData;

    /**
     * @author longsida
     * @reason 用 SP 价格表替换 DMZ 的 TP 取价逻辑，保留原版 -1（关卡解锁）语义
     */
    @Overwrite(remap = false)
    private int getUpgradeCost(String skillName, int currentLevel) {
        if (skillName == null || skillName.isEmpty()) return Integer.MAX_VALUE;

        String key = skillName.toLowerCase(Locale.ROOT);

        int originalCost = readOriginalCost(key, currentLevel);
        if (originalCost == -1) return -1;

        boolean isForm = ConfigManager.getSkillsConfig().getFormSkills().contains(key);
        boolean isStack = ConfigManager.getSkillsConfig().getStackSkills().contains(key);
        boolean isFormLike = isForm || isStack;

        int[] prices = SpPriceConfig.INSTANCE.getPrices(key);
        if (prices.length == 0) {
            return isFormLike
                    ? SpPriceConfig.INSTANCE.defaultFormPrice.get()
                    : SpPriceConfig.INSTANCE.defaultSkillPrice.get();
        }
        if (currentLevel < 0) currentLevel = 0;
        if (currentLevel >= prices.length) return prices[prices.length - 1];
        return prices[currentLevel];
    }

    private int readOriginalCost(String skillName, int targetLevel) {
        boolean isForm = ConfigManager.getSkillsConfig().getFormSkills().contains(skillName);
        boolean isStack = ConfigManager.getSkillsConfig().getStackSkills().contains(skillName);

        if (isForm) {
            String race = (statsData != null && statsData.getCharacter() != null)
                    ? statsData.getCharacter().getRaceName() : "";
            var raceConfig = ConfigManager.getRaceCharacter(race);
            if (raceConfig == null) return Integer.MAX_VALUE;
            Integer[] costs = raceConfig.getFormSkillTpCosts(skillName);
            if (costs != null && targetLevel >= 0 && targetLevel < costs.length) {
                Integer c = costs[targetLevel];
                return c != null ? c : Integer.MAX_VALUE;
            }
            return Integer.MAX_VALUE;
        }

        if (isStack) {
            var skillData = ConfigManager.getSkillsConfig().getSkillCosts(skillName);
            if (skillData != null && skillData.getCosts() != null) {
                List<Integer> costs = skillData.getCosts();
                if (targetLevel >= 0 && targetLevel < costs.size()) {
                    Integer c = costs.get(targetLevel);
                    return c != null ? c : Integer.MAX_VALUE;
                }
            }
            return Integer.MAX_VALUE;
        }

        var skillData = ConfigManager.getSkillsConfig().getSkills().get(skillName);
        if (skillData != null && skillData.getCosts() != null) {
            List<Integer> costs = skillData.getCosts();
            if (targetLevel >= 0 && targetLevel < costs.size()) {
                Integer c = costs.get(targetLevel);
                return c != null ? c : Integer.MAX_VALUE;
            }
        }
        return Integer.MAX_VALUE;
    }

    @Redirect(
            method = "initPurchaseButton",
            at = @At(value = "INVOKE",
                    target = "Lcom/dragonminez/common/stats/character/Resources;getTrainingPoints()F"),
            remap = false
    )
    private float redirectGetTrainingPoints(com.dragonminez.common.stats.character.Resources instance) {
        return SpClientCache.getSp();
    }

    @ModifyConstant(
            method = "renderSkillDetails",
            constant = @Constant(stringValue = "%d TPS"),
            remap = false
    )
    private String replaceTpsFormat(String original) {
        return "%d SP";
    }
}