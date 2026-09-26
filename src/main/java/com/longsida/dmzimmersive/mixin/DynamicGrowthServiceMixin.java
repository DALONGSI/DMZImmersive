package com.longsida.dmzimmersive.mixin;

import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.config.GeneralServerConfig.DynamicGrowthConfig;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.extras.DynamicGrowthStat;
import com.dragonminez.server.dynamicgrowth.DynamicGrowthService;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicGrowthService.class)
public class DynamicGrowthServiceMixin {

    @Inject(method = "award", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onAward(ServerPlayer player, StatsData data, DynamicGrowthStat stat,
                                double baseXp, LivingEntity target, CallbackInfo ci) {
        if (player == null || data == null) return;
        if (baseXp <= 0) return;
        if (player.isCreative() || player.isSpectator()) return;

        DynamicGrowthConfig cfg = ConfigManager.getServerConfig().getDynamicGrowth();
        if (!cfg.isEnabled()) return;

        // 乘原版倍率
        double xp = baseXp * cfg.getPracticeXpMultiplier() * cfg.getStatPracticeMultiplier(stat.key());
        if (!Double.isFinite(xp) || xp <= 0) { ci.cancel(); return; }

        // 算子里限
        int totalStats = data.getStats().getTotalStats();
        int capBase = ImmersiveConfig.COMMON.capBase.get();
        double capCoefficient = ImmersiveConfig.COMMON.capCoefficient.get();
        double cap = capBase + totalStats * capCoefficient;
        double subCap = cap * ImmersiveConfig.getWeight(stat.key());

        // 算换算率
        double conversion = computeConversion(stat, data, totalStats, subCap, cfg);
        if (conversion <= 0) { ci.cancel(); return; }

        int hiddenGain = (int) Math.round(xp * conversion);
        if (hiddenGain <= 0) { ci.cancel(); return; }

        // 存进子池（受子上限约束）
        CompoundTag pdata = player.getPersistentData();
        String key = "dmzimmersive_growth_" + stat.key();
        int current = pdata.getInt(key);
        int newValue = Math.min(current + hiddenGain, (int) subCap);
        pdata.putInt(key, newValue);

        ci.cancel();
    }

    private static double computeConversion(DynamicGrowthStat stat, StatsData data, int totalStats,
                                            double subCap, DynamicGrowthConfig cfg) {
        int sessions = ImmersiveConfig.COMMON.trainingSessions.get();
        double enemyHealthPerStat = ImmersiveConfig.COMMON.enemyHealthPerStat.get();

        switch (stat) {
            case STR, SKP, PWR, VIT -> {
                // 伤害类：单次 XP = 敌人血量 × 0.2 = totalStats × enemyHealthPerStat × 0.2
                double tenSessionsXp = sessions * totalStats * enemyHealthPerStat * 0.2;
                return tenSessionsXp > 0 ? subCap / tenSessionsXp : 0;
            }
            case RES -> {
                double maxStamina = data.getMaxStamina();
                double ratio = cfg.getStaminaSpentXpRatio();
                double tenSessionsXp = maxStamina * sessions * ratio;
                return tenSessionsXp > 0 ? subCap / tenSessionsXp : 0;
            }
            case ENE -> {
                double maxEnergy = data.getMaxEnergy();
                double ratio = cfg.getEnergySpentXpRatio();
                double tenSessionsXp = maxEnergy * sessions * ratio;
                return tenSessionsXp > 0 ? subCap / tenSessionsXp : 0;
            }
        }
        return 0;
    }
}