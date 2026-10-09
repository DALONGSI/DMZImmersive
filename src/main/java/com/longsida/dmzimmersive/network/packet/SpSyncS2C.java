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
    private final float tpPool;
    private final float cap;
    private final float poolStr;
    private final float poolSkp;
    private final float poolPwr;
    private final float poolRes;
    private final float poolVit;
    private final float poolEne;

    public SpSyncS2C(int sp, int lastGrantedLevel, float tpPool, float cap,
                     float poolStr, float poolSkp, float poolPwr,
                     float poolRes, float poolVit, float poolEne) {
        this.sp = sp;
        this.lastGrantedLevel = lastGrantedLevel;
        this.tpPool = tpPool;
        this.cap = cap;
        this.poolStr = poolStr;
        this.poolSkp = poolSkp;
        this.poolPwr = poolPwr;
        this.poolRes = poolRes;
        this.poolVit = poolVit;
        this.poolEne = poolEne;
    }

    public static void encode(SpSyncS2C msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.sp);
        buf.writeInt(msg.lastGrantedLevel);
        buf.writeFloat(msg.tpPool);
        buf.writeFloat(msg.cap);
        buf.writeFloat(msg.poolStr);
        buf.writeFloat(msg.poolSkp);
        buf.writeFloat(msg.poolPwr);
        buf.writeFloat(msg.poolRes);
        buf.writeFloat(msg.poolVit);
        buf.writeFloat(msg.poolEne);
    }

    public static SpSyncS2C decode(FriendlyByteBuf buf) {
        return new SpSyncS2C(
                buf.readInt(), buf.readInt(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(SpSyncS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SpClientCache.set(
                        msg.sp, msg.lastGrantedLevel, msg.tpPool, msg.cap,
                        msg.poolStr, msg.poolSkp, msg.poolPwr,
                        msg.poolRes, msg.poolVit, msg.poolEne))
        );
        ctx.get().setPacketHandled(true);
    }
}