package com.longsida.dmzimmersive.mixin;

import com.dragonminez.client.gui.character.SkillsMenuScreen;
import com.dragonminez.client.gui.buttons.TexturedTextButton;
import com.dragonminez.client.util.TextUtil;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Resources;
import com.longsida.dmzimmersive.requirement.SkillRequirementChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkillsMenuScreen.class)
public class SkillsMenuScreenMixin {

    @Shadow private TexturedTextButton upgradeButton;
    @Shadow private StatsData statsData;
    @Shadow private String selectedSkill;

    @Redirect(method = "initUpgradeButton", at = @At(value = "INVOKE",
            target = "Lcom/dragonminez/common/stats/character/Resources;getTrainingPoints()F"), remap = false)
    private float redirectGetTrainingPoints(Resources resources) {
        return Float.MAX_VALUE;
    }

    @Inject(method = "initUpgradeButton", at = @At("TAIL"), remap = false)
    private void forceEnableUpgradeButton(CallbackInfo ci) {
        if (upgradeButton != null) {
            upgradeButton.active = true;
        }
    }

    @Inject(method = "renderSkillDetails", at = @At("TAIL"), remap = false)
    private void renderRequirements(GuiGraphics graphics, int panelX, int panelY, CallbackInfo ci) {
        if (selectedSkill == null || statsData == null) return;
        if (!SkillRequirementChecker.hasRequirement(selectedSkill)) return;

        int y = panelY + 40 + 24;
        int x = panelX + 72;
        graphics.fill(x - 40, y - 1, x + 40, y + 9, 0xFF000000);
        Component reqText = SkillRequirementChecker.buildRequirementComponent(statsData, selectedSkill, 0);
        TextUtil.drawCenteredStringWithBorder(graphics, Minecraft.getInstance().font, reqText, x, y, 0xFFAAAAAA);
    }
}