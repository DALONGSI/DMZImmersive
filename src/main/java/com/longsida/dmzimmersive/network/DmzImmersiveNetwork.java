package com.longsida.dmzimmersive.network;

import com.longsida.dmzimmersive.network.packet.SpRequestC2S;
import com.longsida.dmzimmersive.network.packet.SpSyncS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class DmzImmersiveNetwork {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath("dmzimmersive", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                SpSyncS2C.class,
                SpSyncS2C::encode,
                SpSyncS2C::decode,
                SpSyncS2C::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                SpRequestC2S.class,
                SpRequestC2S::encode,
                SpRequestC2S::decode,
                SpRequestC2S::handle
        );
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAll(Object msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }
}