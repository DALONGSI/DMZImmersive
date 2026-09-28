package com.longsida.dmzimmersive.mixin;

import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.network.C2S.UpdateSkillC2S;
import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.skills.Skill;
import com.dragonminez.common.stats.techniques.KiAttackData;
import com.dragonminez.common.stats.techniques.PredefinedTechniques;
import com.dragonminez.common.stats.techniques.StrikeAttackData;
import com.longsida.dmzimmersive.requirement.SkillRequirementChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(UpdateSkillC2S.class)
public class UpdateSkillC2SMixin {

    @Shadow private String skillName;
    @Shadow private UpdateSkillC2S.SkillAction action;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, remap = false)
    private void onHandle(Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
                Skill skill = data.getSkills().getSkill(skillName);

                switch (action) {
                    case TOGGLE -> {
                        // 开关不检查属性，也不消耗任何东西
                        if (skill != null && skill.getLevel() > 0) {
                            skill.setActive(!skill.isActive());
                        }
                    }
                    case UPGRADE -> {
                        if (skill == null) return;
                        if (skill.isMaxLevel()) return;

                        // 种族限制
                        if (skill.getLevel() <= 0 && !isSkillAllowedForPlayerRace(data, skillName)) return;

                        // 堆叠技能
                        boolean isStackSkill = ConfigManager.getSkillsConfig().getStackSkills().contains(skillName.toLowerCase());
                        if (isStackSkill && skill.getLevel() <= 0) return;

                        // 师傅专属变身
                        if (skill.getLevel() <= 0 && isMasterOnlyFormSkill(data, skillName)) return;

                        // 刷新最大等级
                        refreshRuntimeMaxLevel(data, skillName, skill);

                        // 属性检查
                        int currentLevel = skill.getLevel();
                        if (!SkillRequirementChecker.meets(data, skillName, currentLevel)) {
                            player.displayClientMessage(
                                    SkillRequirementChecker.buildRequirementMessage(data, skillName, currentLevel),
                                    false
                            );
                            return;
                        }

                        // 解锁
                        boolean wasLevelZero = skill.getLevel() == 0;
                        skill.addLevel(1);

                        if (wasLevelZero) unlockTechniqueIfPresent(data, skillName);
                    }
                    case PURCHASE -> {
                        if (!isSkillAllowedForPlayerRace(data, skillName)) return;

                        boolean isFormSkillPurchase = ConfigManager.getSkillsConfig().getFormSkills().contains(skillName.toLowerCase());

                        // 已拥有判断
                        boolean notOwned = !data.getSkills().hasSkill(skillName)
                                || (isFormSkillPurchase && data.getSkills().getSkillLevel(skillName) == 0);
                        if (!notOwned) return;

                        // 变身技能必须存在于种族配置中
                        if (isFormSkillPurchase) {
                            var charConfig = ConfigManager.getRaceCharacter(data.getCharacter().getRaceName());
                            if (charConfig == null || !charConfig.hasFormSkill(skillName)) return;
                        }

                        // 属性检查
                        if (!SkillRequirementChecker.meets(data, skillName, 0)) {
                            player.displayClientMessage(
                                    SkillRequirementChecker.buildRequirementMessage(data, skillName, 0),
                                    false
                            );
                            return;
                        }

                        // 解锁
                        data.getSkills().setSkillLevel(skillName, 1);
                        Skill purchased = data.getSkills().getSkill(skillName);
                        if (purchased != null) refreshRuntimeMaxLevel(data, skillName, purchased);
                        unlockTechniqueIfPresent(data, skillName);
                    }
                }

                NetworkHandler.sendToTrackingEntityAndSelf(new StatsSyncS2C(player), player);
            });
        });
        ctx.get().setPacketHandled(true);
        ci.cancel();
    }

    // ===== 以下是原版逻辑的复制，因为我们要完全替换 handle =====

    private static boolean isSkillAllowedForPlayerRace(StatsData data, String skillName) {
        if (data == null || skillName == null || skillName.isEmpty()) return false;
        String raceName = data.getCharacter() != null ? data.getCharacter().getRaceName() : "";
        return ConfigManager.getSkillsConfig().isSkillAllowedForRace(skillName, raceName);
    }

    private static boolean isMasterOnlyFormSkill(StatsData data, String skillName) {
        if (data == null || skillName == null) return false;
        if (!ConfigManager.getSkillsConfig().getFormSkills().contains(skillName.toLowerCase())) return false;
        String raceName = data.getCharacter() != null ? data.getCharacter().getRaceName() : "";
        var charConfig = ConfigManager.getRaceCharacter(raceName);
        return charConfig != null && charConfig.isFormSkillBuyFromMaster(skillName);
    }

    private static void refreshRuntimeMaxLevel(StatsData data, String skillName, Skill skill) {
        String normalized = skillName.toLowerCase();
        var skillsConfig = ConfigManager.getSkillsConfig();

        if (skillsConfig.getFormSkills().contains(normalized)) {
            String raceName = data.getCharacter().getRaceName();
            if (raceName == null || raceName.isEmpty()) return;
            var charConfig = ConfigManager.getRaceCharacter(raceName);
            int maxLevel = charConfig.getFormSkillTpCosts(normalized).length;
            skill.setMaxLevel(maxLevel);
            return;
        }

        int maxLevel = 0;
        var skillCosts = skillsConfig.getSkillCosts(normalized);
        if (skillCosts != null && skillCosts.getCosts() != null) maxLevel = skillCosts.getCosts().size();

        if ("potentialunlock".equalsIgnoreCase(normalized)) maxLevel = Math.min(maxLevel, 30);
        else maxLevel = Math.min(maxLevel, 50);

        skill.setMaxLevel(maxLevel);
    }

    private static void unlockTechniqueIfPresent(StatsData data, String techId) {
        if (PredefinedTechniques.REGISTRY.containsKey(techId)) {
            KiAttackData template = PredefinedTechniques.REGISTRY.get(techId);
            KiAttackData clone = new KiAttackData();
            clone.load(template.save());
            data.getTechniques().unlockTechnique(clone);
        } else if (PredefinedTechniques.STRIKE_REGISTRY.containsKey(techId)) {
            StrikeAttackData template = PredefinedTechniques.STRIKE_REGISTRY.get(techId);
            StrikeAttackData clone = new StrikeAttackData();
            clone.load(template.save());
            data.getTechniques().unlockTechnique(clone);
        }
    }
}