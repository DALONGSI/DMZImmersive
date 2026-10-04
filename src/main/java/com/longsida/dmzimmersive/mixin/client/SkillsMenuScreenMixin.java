package com.longsida.dmzimmersive.mixin.client;

import com.dragonminez.client.gui.character.SkillsMenuScreen;
import com.dragonminez.common.config.ConfigManager;
import com.longsida.dmzimmersive.client.SpClientCache;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@Mixin(SkillsMenuScreen.class)
public class SkillsMenuScreenMixin {

    /**
     * @author longsida
     * @reason 用 SP 价格表替换 DMZ 的 TP 取价逻辑
     */
    @Overwrite(remap = false)
    private int getUpgradeCostForTargetLevel(String skillName, int targetLevel) {
        if (skillName == null || skillName.isEmpty()) return Integer.MAX_VALUE;

        String key = skillName.toLowerCase(Locale.ROOT);
        boolean isForm = ConfigManager.getSkillsConfig().getFormSkills().contains(key);
        boolean isStack = ConfigManager.getSkillsConfig().getStackSkills().contains(key);
        boolean isFormLike = isForm || isStack;

        int[] prices = SpPriceConfig.INSTANCE.getPrices(key);
        if (prices.length == 0) {
            return isFormLike
                    ? SpPriceConfig.INSTANCE.defaultFormPrice.get()
                    : SpPriceConfig.INSTANCE.defaultSkillPrice.get();
        }
        if (targetLevel < 0) targetLevel = 0;
        if (targetLevel >= prices.length) return prices[prices.length - 1];
        return prices[targetLevel];
    }

    @Redirect(
            method = {"initUpgradeButton", "renderFormsTree"},
            at = @At(value = "INVOKE",
                    target = "Lcom/dragonminez/common/stats/character/Resources;getTrainingPoints()F"),
            remap = false
    )
    private float redirectGetTrainingPoints(com.dragonminez.common.stats.character.Resources instance) {
        return SpClientCache.getSp();
    }

    @Redirect(
            method = "m_6375_",
            at = @At(value = "INVOKE",
                    target = "Lcom/dragonminez/common/stats/character/Resources;getTrainingPoints()F"),
            remap = false
    )
    private float redirectGetTrainingPointsMouseClicked(com.dragonminez.common.stats.character.Resources instance) {
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