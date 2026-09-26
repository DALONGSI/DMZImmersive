package com.longsida.dmzimmersive.event;

import com.dragonminez.common.events.DMZEvent;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "dmzimmersive", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrainingEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTPGain(DMZEvent.TPGainEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        int incomingTP = event.getTpGain();
        if (incomingTP <= 0) return;

        StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
            int totalStats = data.getStats().getTotalStats();

            int capBase = ImmersiveConfig.COMMON.capBase.get();
            double capCoefficient = ImmersiveConfig.COMMON.capCoefficient.get();
            int tpPerHit = ImmersiveConfig.COMMON.tpPerHit.get();
            double tpHealthRatio = ImmersiveConfig.COMMON.tpHealthRatio.get();
            double enemyHealthPerStat = ImmersiveConfig.COMMON.enemyHealthPerStat.get();
            int killsToCap = ImmersiveConfig.COMMON.killsToCap.get();

            int cap = capBase + (int) (totalStats * capCoefficient);

            CompoundTag pdata = player.getPersistentData();
            int current = pdata.getInt("dmzimmersive_tp_pool");
            if (current >= cap) {
                event.setTpGain(0);
                return;
            }

            int perKillTP = tpPerHit + (int) Math.round(totalStats * enemyHealthPerStat * tpHealthRatio);
            int totalTP = killsToCap * perKillTP;
            double conversion = totalTP > 0 ? (double) cap / totalTP : 1.0;

            int hiddenGain = (int) Math.round(incomingTP * conversion);

            int remaining = cap - current;
            if (hiddenGain > remaining) hiddenGain = remaining;
            if (hiddenGain <= 0) {
                event.setTpGain(0);
                return;
            }

            pdata.putInt("dmzimmersive_tp_pool", current + hiddenGain);
            event.setTpGain(0);
        });
    }
}