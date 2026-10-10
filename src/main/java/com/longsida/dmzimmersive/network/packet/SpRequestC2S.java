package com.longsida.dmzimmersive.network.packet;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.sp.SpManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpRequestC2S {

    public SpRequestC2S() {}

    public static void encode(SpRequestC2S msg, FriendlyByteBuf buf) {}

    public static SpRequestC2S decode(FriendlyByteBuf buf) {
        return new SpRequestC2S();
    }

    public static void handle(SpRequestC2S msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
                SpManager.clearCache(player.getUUID());
                SpManager.recalcAndSync(player, data);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}