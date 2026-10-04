package com.longsida.dmzimmersive.mixin;

import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.network.C2S.UpdateSkillC2S;
import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.skills.Skill;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import com.longsida.dmzimmersive.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(UpdateSkillC2S.class)
public abstract class UpdateSkillC2SMixin {

    @Shadow private String skillName;
    @Shadow private UpdateSkillC2S.SkillAction action;

    @Shadow private static boolean isMasterOnlyFormSkill(StatsData data, String skillName) { return false; }
    @Shadow private static boolean isSkillAllowedForPlayerRace(StatsData data, String skillName) { return false; }
    @Shadow private void unlockTechniqueIfPresent(StatsData data, String techId) {}
    @Shadow private static void refreshRuntimeMaxLevel(StatsData data, String skillName, Skill skill) {}

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, remap = false)
    private void onHandle(Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
                Skill skill = data.getSkills().getSkill(skillName);
                boolean raceAllowed = isSkillAllowedForPlayerRace(data, skillName);
                boolean isFormSkill = ConfigManager.getSkillsConfig()
                        .getFormSkills().contains(skillName.toLowerCase());

                switch (action) {
                    case TOGGLE -> {
                        if (skill != null && skill.getLevel() > 0) skill.setActive(!skill.isActive());
                    }
                    case UPGRADE -> {
                        if (skill == null) break;
                        if (skill.getLevel() <= 0 && !raceAllowed) break;
                        boolean isStackSkill = ConfigManager.getSkillsConfig()
                                .getStackSkills().contains(skillName.toLowerCase());
                        if (isStackSkill && skill.getLevel() <= 0) break;
                        if (skill.getLevel() <= 0 && isMasterOnlyFormSkill(data, skillName)) break;
                        refreshRuntimeMaxLevel(data, skillName, skill);
                        if (skill.isMaxLevel()) break;
                        if (skillName.equals("potentialunlock") && skill.getLevel() == 10) break;

                        int price = SpPriceConfig.INSTANCE.getPrice(skillName, skill.getLevel(), isFormSkill);
                        if (price < 0) break;

                        int available = SpManager.calcAvailable(player, data);
                        if (available < price) break;

                        boolean wasLevelZero = skill.getLevel() == 0;
                        skill.addLevel(1);
                        if (wasLevelZero) unlockTechniqueIfPresent(data, skillName);
                    }
                    case PURCHASE -> {
                        if (!raceAllowed) break;
                        boolean isFormSkillPurchase = ConfigManager.getSkillsConfig()
                                .getFormSkills().contains(skillName.toLowerCase());
                        if (isFormSkillPurchase) {
                            var charConfig = ConfigManager.getRaceCharacter(
                                    data.getCharacter().getRaceName());
                            if (charConfig == null || !charConfig.hasFormSkill(skillName)) break;
                        }
                        boolean notOwned = !data.getSkills().hasSkill(skillName)
                                || (isFormSkillPurchase
                                && data.getSkills().getSkillLevel(skillName) == 0);
                        if (!notOwned) break;

                        int price = SpPriceConfig.INSTANCE.getPrice(skillName, 0, isFormSkillPurchase);
                        if (price < 0) break;

                        int available = SpManager.calcAvailable(player, data);
                        if (available < price) break;

                        data.getSkills().setSkillLevel(skillName, 1);
                        Skill purchased = data.getSkills().getSkill(skillName);
                        if (purchased != null) refreshRuntimeMaxLevel(data, skillName, purchased);
                        unlockTechniqueIfPresent(data, skillName);
                    }
                }
                NetworkHandler.sendToTrackingEntityAndSelf(new StatsSyncS2C(player), player);
                SpManager.recalcAndSync(player, data);
            });
        });
        ctx.get().setPacketHandled(true);
        ci.cancel();
    }
}