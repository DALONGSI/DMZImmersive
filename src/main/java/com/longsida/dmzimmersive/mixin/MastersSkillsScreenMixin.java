package com.longsida.dmzimmersive.mixin;

import com.dragonminez.client.gui.MastersSkillsScreen;
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

@Mixin(MastersSkillsScreen.class)
public class MastersSkillsScreenMixin {

    @Shadow private TexturedTextButton purchaseButton;
    @Shadow private StatsData statsData;
    @Shadow private String selectedSkill;

    @Redirect(method = "initPurchaseButton", at = @At(value = "INVOKE",
            target = "Lcom/dragonminez/common/stats/character/Resources;getTrainingPoints()F"), remap = false)
    private float redirectGetTrainingPoints(Resources resources) {
        return Float.MAX_VALUE;
    }

    @Inject(method = "initPurchaseButton", at = @At("TAIL"), remap = false)
    private void forceEnablePurchaseButton(CallbackInfo ci) {
        if (purchaseButton != null) {
            purchaseButton.active = true;
        }
    }

    @Inject(method = "renderSkillDetails", at = @At("TAIL"), remap = false)
    private void renderSkillRequirements(GuiGraphics graphics, int panelX, int panelY, CallbackInfo ci) {
        renderRequirements(graphics, panelX, panelY);
    }

    @Inject(method = "renderKiTechniqueDetails", at = @At("TAIL"), remap = false)
    private void renderKiRequirements(GuiGraphics graphics, int panelX, int panelY, CallbackInfo ci) {
        renderRequirements(graphics, panelX, panelY);
    }

    @Inject(method = "renderStrikeTechniqueDetails", at = @At("TAIL"), remap = false)
    private void renderStrikeRequirements(GuiGraphics graphics, int panelX, int panelY, CallbackInfo ci) {
        renderRequirements(graphics, panelX, panelY);
    }

    private void renderRequirements(GuiGraphics graphics, int panelX, int panelY) {
        if (selectedSkill == null || statsData == null) return;
        if (!SkillRequirementChecker.hasRequirement(selectedSkill)) return;

        int y = panelY + 196;
        int x = panelX + 70;
        graphics.fill(x - 40, y - 1, x + 40, y + 9, 0xFF000000);
        Component reqText = SkillRequirementChecker.buildRequirementComponent(statsData, selectedSkill, 0);
        TextUtil.drawCenteredStringWithBorder(graphics, Minecraft.getInstance().font, reqText, x, y, 0xFFAAAAAA);
    }
}