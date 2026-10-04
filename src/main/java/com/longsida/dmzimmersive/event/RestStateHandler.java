package com.longsida.dmzimmersive.event;

import com.longsida.dmzimmersive.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "dmzimmersive", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RestStateHandler {

    private static final float ENTER_THRESHOLD = 0.40f;
    private static final float EXIT_THRESHOLD = 0.90f;
    private static final int BUFF_DURATION = 3 * 60 * 20;

    private static final Map<UUID, Boolean> LAST_HAD_COMBAT = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 20 != 0) return;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        if (maxHealth <= 0) return;

        UUID uuid = player.getUUID();
        boolean hasCombat = player.hasEffect(ModEffects.COMBAT_READY.get());
        boolean hasRecover = player.hasEffect(ModEffects.RECOVERING.get());
        Boolean lastCombat = LAST_HAD_COMBAT.get(uuid);

        // 进入【充分战斗】
        if (!hasCombat && !hasRecover && health <= maxHealth * ENTER_THRESHOLD) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.COMBAT_READY.get(), BUFF_DURATION, 0, false, true, true));
            LAST_HAD_COMBAT.put(uuid, true);
            return;
        }

        // 【充分战斗】自然结束 → 【恢复中】
        if (lastCombat != null && lastCombat && !hasCombat && !hasRecover) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.RECOVERING.get(), BUFF_DURATION, 0, false, true, true));
            LAST_HAD_COMBAT.put(uuid, false);
            return;
        }

        // 【恢复中】+ 血量 ≥ 90% → 兑换
        if (hasRecover && health >= maxHealth * EXIT_THRESHOLD) {
            player.removeEffect(ModEffects.RECOVERING.get());
            LAST_HAD_COMBAT.put(uuid, false);
            RestEventHandler.doRest(player);
            return;
        }

        LAST_HAD_COMBAT.put(uuid, hasCombat);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_HAD_COMBAT.remove(event.getEntity().getUUID());
    }
}