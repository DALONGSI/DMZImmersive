package com.longsida.dmzimmersive.event;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.server.events.players.StatsEvents;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import com.longsida.dmzimmersive.sp.SpManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "dmzimmersive", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RestEventHandler {

    private static final String[] STATS = {"STR", "SKP", "PWR", "RES", "VIT", "ENE"};

    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
            if (!data.getStatus().isHasCreatedCharacter()) return;

            CompoundTag pdata = player.getPersistentData();
            boolean useSpecialized = ImmersiveConfig.COMMON.useSpecializedTraining.get();

            boolean changed;
            if (useSpecialized) {
                changed = applyModeB(player, data, pdata);
            } else {
                changed = applyModeA(player, data, pdata);
            }

            // 改动 START
            int level = data.getLevel();
            int granted = SpManager.grantSpForLevel(player, level);
            if (granted > 0) {
                player.displayClientMessage(
                        Component.literal("§b获得 " + granted + " 技能点（SP）"), false);
            }
            // 改动 END            

            if (!changed) return;

            StatsEvents.applyHealthBonus(player);
            NetworkHandler.sendToTrackingEntityAndSelf(new StatsSyncS2C(player), player);

            player.displayClientMessage(
                    Component.translatable("dmzimmersive.message.trained"),
                    false
            );
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 1.0F);

            // ===== 提示音留白 =====
            // TODO: 以后在这里加自定义音效或语音
            System.out.println("[DMZImmersive] 我变强了");
            // ===== 留白结束 =====
        });
    }

    private static boolean applyModeA(ServerPlayer player, StatsData data, CompoundTag pdata) {
        float totalHidden = pdata.getFloat("dmzimmersive_tp_pool");
        if (totalHidden <= 0) return false;

        double directRatio = ImmersiveConfig.COMMON.directRatio.get();
        int directTotal = (int) (totalHidden * directRatio);
        int manualTotal = (int) totalHidden - directTotal;

        int remaining = directTotal;
        int[] shares = new int[6];
        for (int i = 0; i < 6; i++) {
            double w = ImmersiveConfig.getModeAWeight(STATS[i]);
            shares[i] = (int) (directTotal * w);
            remaining -= shares[i];
        }
        int remainderIndex = indexOf(ImmersiveConfig.COMMON.remainderStat.get());
        shares[remainderIndex] += remaining;

        for (int i = 0; i < 6; i++) {
            if (shares[i] > 0) addStat(data, STATS[i], shares[i]);
        }

        if (manualTotal > 0) {
            data.getResources().addPendingAttributePoints(manualTotal);
        }

        pdata.putFloat("dmzimmersive_tp_pool", 0);
        return true;
    }

    private static boolean applyModeB(ServerPlayer player, StatsData data, CompoundTag pdata) {
        double manualRatio = ImmersiveConfig.COMMON.manualRatio.get();

        float[] pools = new float[6];
        float totalPool = 0;
        for (int i = 0; i < 6; i++) {
            pools[i] = pdata.getFloat("dmzimmersive_growth_" + STATS[i]);
            totalPool += pools[i];
        }
        if (totalPool <= 0) return false;

        int manualTotal = 0;
        int[] afterManual = new int[6];
        for (int i = 0; i < 6; i++) {
            int cut = (int) (pools[i] * manualRatio);
            manualTotal += cut;
            afterManual[i] = (int) pools[i] - cut;
        }

        int[] attributeGains = new int[6];
        for (int source = 0; source < 6; source++) {
            if (afterManual[source] <= 0) continue;
            double[] row = ImmersiveConfig.getDistributor(STATS[source]);
            for (int target = 0; target < 6; target++) {
                attributeGains[target] += (int) (afterManual[source] * row[target]);
            }
        }

        for (int i = 0; i < 6; i++) {
            if (attributeGains[i] > 0) addStat(data, STATS[i], attributeGains[i]);
        }

        if (manualTotal > 0) {
            data.getResources().addPendingAttributePoints(manualTotal);
        }

        for (int i = 0; i < 6; i++) {
            pdata.putFloat("dmzimmersive_growth_" + STATS[i], 0);
        }
        return true;
    }

    private static int indexOf(String stat) {
        for (int i = 0; i < STATS.length; i++) {
            if (STATS[i].equalsIgnoreCase(stat)) return i;
        }
        return 4;
    }

    private static void addStat(StatsData data, String stat, int amount) {
        if (amount <= 0) return;
        switch (stat.toUpperCase()) {
            case "STR" -> data.getStats().addStrength(amount);
            case "SKP" -> data.getStats().addStrikePower(amount);
            case "PWR" -> data.getStats().addKiPower(amount);
            case "RES" -> data.getStats().addResistance(amount);
            case "VIT" -> data.getStats().addVitality(amount);
            case "ENE" -> data.getStats().addEnergy(amount);
        }
    }
}