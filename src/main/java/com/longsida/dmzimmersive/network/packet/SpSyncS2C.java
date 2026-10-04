package com.longsida.dmzimmersive.network.packet;

import com.longsida.dmzimmersive.client.SpClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpSyncS2C {

    private final int sp;
    private final int lastGrantedLevel;

    public SpSyncS2C(int sp, int lastGrantedLevel) {
        this.sp = sp;
        this.lastGrantedLevel = lastGrantedLevel;
    }

    public static void encode(SpSyncS2C msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.sp);
        buf.writeInt(msg.lastGrantedLevel);
    }

    public static SpSyncS2C decode(FriendlyByteBuf buf) {
        return new SpSyncS2C(buf.readInt(), buf.readInt());
    }

    public static void handle(SpSyncS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        SpClientCache.set(msg.sp, msg.lastGrantedLevel))
        );
        ctx.get().setPacketHandled(true);
    }
}