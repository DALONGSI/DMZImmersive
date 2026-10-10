package com.longsida.dmzimmersive.mixin.client;

import com.dragonminez.client.gui.character.CharacterStatsScreen;
import com.dragonminez.common.stats.StatsData;
import com.longsida.dmzimmersive.client.SpClientCache;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(CharacterStatsScreen.class)
public class CharacterStatsScreenMixin {

    private static final ResourceLocation DMZ_FONT =
            ResourceLocation.fromNamespaceAndPath("dragonminez", "smooth");

    @Shadow private StatsData statsData;

    //同步用户属性值界面，每次打开刷新数值，不然tp sp一直会显示一开始的
    @Inject(method = "m_7856_", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        com.longsida.dmzimmersive.network.DmzImmersiveNetwork.CHANNEL.sendToServer(
                new com.longsida.dmzimmersive.network.packet.SpRequestC2S());
    }

    /**
     * @author longsida
     * @reason 用 SPs / TP（训练进度）替换 TPS / TPC
     */
    @Overwrite(remap = false)
    private void renderStatsInfo(GuiGraphics graphics, int mouseX, int mouseY) {
        int uiW = uiWidth();
        int uiH = uiHeight();
        int centerY = uiH / 2;
        int titleY = centerY - 88;

        Font font = Minecraft.getInstance().font;

        drawCenteredTr(graphics, font, "gui.dragonminez.character_stats.info", 85, titleY, 16499996, true);

        int level = this.statsData.getLevel();
        int sp = SpClientCache.getSp();
        String characterClass = this.statsData.getCharacter().getCharacterClass();
        String form = this.statsData.getCharacter().getActiveForm();
        String stackForm = this.statsData.getCharacter().getActiveStackForm();

        int labelX = 30;
        int valueX = 70;
        int startY = centerY - 72;

        // Level
        drawTrBold(graphics, font, "gui.dragonminez.character_stats.level", labelX, startY, 14155509);
        drawRaw(graphics, font, String.valueOf(level), valueX + 5, startY, 16777215);

        // SPs（当前可用 SP）
        drawRawBold(graphics, font, "SPs", labelX, startY + 11, 14155509);
        drawRaw(graphics, font, String.valueOf(sp), valueX + 5, startY + 11, 16770451);

        // Form
        drawTrBold(graphics, font, "gui.dragonminez.character_stats.form", labelX, startY + 22, 14155509);
        boolean isBase = form == null || form.isEmpty() || form.equals("base");
        boolean hasActiveStack = stackForm != null && !stackForm.isEmpty();
        String activeStackGroup = this.statsData.getCharacter().getActiveStackFormGroup();

        MutableComponent baseFormComponent;
        if (isBase) {
            baseFormComponent = Component.translatable("race.dragonminez.base")
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT));
        } else {
            baseFormComponent = Component.translatable(
                            "race.dragonminez." + this.statsData.getCharacter().getRaceName()
                                    + ".form." + this.statsData.getCharacter().getActiveFormGroup() + "." + form)
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT));
        }

        MutableComponent formComponent;
        if (!isBase) {
            if (hasActiveStack) {
                formComponent = baseFormComponent.copy().append(" ")
                        .append(Component.translatable(
                                        "race.dragonminez.stack.form." + activeStackGroup + "." + stackForm)
                                .withStyle(Style.EMPTY.withFont(DMZ_FONT)));
            } else {
                formComponent = baseFormComponent;
            }
        } else if (hasActiveStack) {
            formComponent = Component.translatable("race.dragonminez.stack.group." + activeStackGroup)
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT))
                    .append(" ")
                    .append(Component.translatable(
                                    "race.dragonminez.stack.form." + activeStackGroup + "." + stackForm)
                            .withStyle(Style.EMPTY.withFont(DMZ_FONT)));
        } else {
            formComponent = baseFormComponent;
        }
        graphics.drawString(font, formComponent, valueX + 5, startY + 22, 13101820, true);

        // Class
        drawTrBold(graphics, font, "gui.dragonminez.character_stats.class", labelX, startY + 33, 14155509);
        drawRaw(graphics, font, Component.translatable("class.dragonminez." + characterClass).getString(),
                valueX + 5, startY + 33, 16777215);

        drawCenteredTr(graphics, font, "gui.dragonminez.character_stats.stats", 82, centerY - 21, 6868223, true);

        String[] statNames = {"str", "skp", "res", "vit", "pwr", "ene"};
        String[] statNamesUpper = {"STR", "SKP", "RES", "VIT", "PWR", "ENE"};
        int[] statValues = {
                this.statsData.getStats().getStrength(),
                this.statsData.getStats().getStrikePower(),
                this.statsData.getStats().getResistance(),
                this.statsData.getStats().getVitality(),
                this.statsData.getStats().getKiPower(),
                this.statsData.getStats().getEnergy()
        };
        int statY = centerY - 3;

        for (int i = 0; i < statNames.length; i++) {
            int statLabelX = 42;
            int yPos = statY + i * 12;
            double totalMult = this.statsData.getTotalMultiplier(statNamesUpper[i]);
            int baseValue = statValues[i];
            double modifiedValue = baseValue * totalMult;

            drawTrBold(graphics, font, "gui.dragonminez.character_stats." + statNames[i],
                    statLabelX, yPos, 14095410);

            boolean hasMult = Math.abs(totalMult - 1.0) > 0.01;
            int statColor = hasMult ? 16776960 : 16766891;
            String statText = hasMult
                    ? formatNumber((int) modifiedValue) + " x" + String.format(Locale.US, "%.1f", totalMult)
                    : formatNumber(baseValue);
            drawRaw(graphics, font, statText, valueX + 5, yPos, statColor);

            if (mouseX >= statLabelX && mouseX <= statLabelX + 25 && mouseY >= yPos && mouseY <= yPos + 9) {
                List<Component> extras = new ArrayList<>();
                float pool = SpClientCache.getSubPool(statNamesUpper[i]);
                int totalStats = this.statsData.getStats().getTotalStats();
                int capBase = ImmersiveConfig.COMMON.capBase.get();
                double capCoef = ImmersiveConfig.COMMON.capCoefficient.get();
                float cap = (float) (capBase + totalStats * capCoef);
                float subCap = cap * (float) ImmersiveConfig.getWeight(statNamesUpper[i]);
                float percent = subCap <= 0 ? 0 : (pool / subCap * 100f);
                if (percent < 0) percent = 0;
                if (percent > 100) percent = 100;

                extras.add(Component.literal("训练进度 / Training Progress: " + String.format(Locale.US, "%.1f%%", percent))
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT).withColor(ChatFormatting.GREEN)));

                com.dragonminez.client.util.TextUtil.renderAdvancedTooltip(
                        graphics, font, mouseX, mouseY,
                        uiW, uiH,
                        Component.translatable("gui.dragonminez.character_stats." + statNames[i])
                                .withStyle(Style.EMPTY.withFont(DMZ_FONT).withColor(ChatFormatting.BOLD)),
                        new ArrayList<>(),
                        extras, 14095410);
            }
        }

        int pendingAP = this.statsData.getPendingAttributePoints();
        boolean showAP = pendingAP > 0;
        int bottomY = statY + 76;
        int bottomValueX = 75;

        MutableComponent bottomLabel;
        int labelColor;
        if (showAP) {
            bottomLabel = Component.translatable("gui.dragonminez.character_stats.ap")
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT).withBold(true));
            labelColor = 11758591;
        } else {
            bottomLabel = Component.literal("TP")
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT).withBold(true));
            labelColor = 2883554;
        }
        graphics.drawString(font, bottomLabel, 42, bottomY, labelColor, true);

        MutableComponent bottomValue;
        int valueColor;
        if (showAP) {
            bottomValue = Component.literal(formatNumber(pendingAP))
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT));
            valueColor = 16776960;
        } else {
            bottomValue = Component.literal(String.format(Locale.US, "%.1f%%", SpClientCache.getProgressPercent()))
                    .withStyle(Style.EMPTY.withFont(DMZ_FONT));
            valueColor = 16764481;
        }
        graphics.drawString(font, bottomValue, bottomValueX, bottomY, valueColor, true);

        int bottomWidth = font.width(bottomValue);
        if (mouseX >= 42 && mouseX <= bottomValueX + bottomWidth && mouseY >= bottomY && mouseY <= bottomY + 9) {
            List<Component> desc = new ArrayList<>();
            List<Component> extras = new ArrayList<>();
            int color;
            MutableComponent title;
            if (showAP) {
                title = Component.translatable("gui.dragonminez.character_stats.ap")
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT).withColor(ChatFormatting.LIGHT_PURPLE));
                color = 11758591;
                desc.add(Component.translatable("gui.dragonminez.character_stats.ap.desc")
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT)));
            } else {
                title = Component.literal("TP")
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT).withColor(ChatFormatting.AQUA));
                color = 2883554;
                desc.add(Component.literal("训练进度 / Training Progress")
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT)));
                desc.add(Component.literal("本次锻炼到达上限进度 / This session's progress toward cap")
                        .withStyle(Style.EMPTY.withFont(DMZ_FONT).withColor(ChatFormatting.GRAY)));
            }
            com.dragonminez.client.util.TextUtil.renderAdvancedTooltip(
                    graphics, font, mouseX, mouseY,
                    uiW, uiH, title, desc, extras, color);
        }
    }

    private int uiWidth() {
        try {
            Method m = Class.forName("com.dragonminez.client.gui.character.util.ScaledScreen")
                    .getDeclaredMethod("getUiWidth");
            m.setAccessible(true);
            return (int) m.invoke(this);
        } catch (Exception e) {
            return ((net.minecraft.client.gui.screens.Screen) (Object) this).width;
        }
    }

    private int uiHeight() {
        try {
            Method m = Class.forName("com.dragonminez.client.gui.character.util.ScaledScreen")
                    .getDeclaredMethod("getUiHeight");
            m.setAccessible(true);
            return (int) m.invoke(this);
        } catch (Exception e) {
            return ((net.minecraft.client.gui.screens.Screen) (Object) this).height;
        }
    }

    // ================ 绘制辅助 ================

    private static void drawTrBold(GuiGraphics g, Font font, String key, int x, int y, int color) {
        MutableComponent c = Component.translatable(key)
                .withStyle(Style.EMPTY.withFont(DMZ_FONT).withBold(true));
        g.drawString(font, c, x, y, color, true);
    }

    private static void drawCenteredTr(GuiGraphics g, Font font, String key, int x, int y, int color, boolean bold) {
        MutableComponent c = Component.translatable(key)
                .withStyle(Style.EMPTY.withFont(DMZ_FONT).withBold(bold));
        g.drawCenteredString(font, c, x, y, color);
    }

    private static void drawRaw(GuiGraphics g, Font font, String text, int x, int y, int color) {
        MutableComponent c = Component.literal(text)
                .withStyle(Style.EMPTY.withFont(DMZ_FONT));
        g.drawString(font, c, x, y, color, true);
    }

    private static void drawRawBold(GuiGraphics g, Font font, String text, int x, int y, int color) {
        MutableComponent c = Component.literal(text)
                .withStyle(Style.EMPTY.withFont(DMZ_FONT).withBold(true));
        g.drawString(font, c, x, y, color, true);
    }

    private static String formatNumber(long value) {
        return NumberFormat.getInstance(Locale.US).format(value);
    }
}