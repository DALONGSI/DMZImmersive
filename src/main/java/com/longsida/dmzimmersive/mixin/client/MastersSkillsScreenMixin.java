package com.longsida.dmzimmersive.mixin.client;

import com.dragonminez.client.gui.MastersSkillsScreen;
import com.dragonminez.client.gui.buttons.TexturedTextButton;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.requirement.SkillRequirementManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(MastersSkillsScreen.class)
public class MastersSkillsScreenMixin {

    @Shadow private String selectedSkill;
    @Shadow private TexturedTextButton purchaseButton;

    @Inject(method = "initPurchaseButton", at = @At("TAIL"), remap = false)
    private void afterInitPurchaseButton(CallbackInfo ci) {
        if (purchaseButton == null || selectedSkill == null) return;

        if (!SkillRequirementManager.isConfigured(selectedSkill)) return;

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("STR", data.getStats().getStrength());
            stats.put("SKP", data.getStats().getStrikePower());
            stats.put("PWR", data.getStats().getKiPower());
            stats.put("VIT", data.getStats().getVitality());
            stats.put("ENE", data.getStats().getEnergy());
            stats.put("RES", data.getStats().getResistance());

            if (!SkillRequirementManager.meetsRequirement(stats, selectedSkill, 1)) {
                purchaseButton.active = false;
            }
        });
    }
}