package com.longsida.dmzimmersive.sp;

import com.longsida.dmzimmersive.network.DmzImmersiveNetwork;
import com.longsida.dmzimmersive.network.packet.SpSyncS2C;
import net.minecraft.server.level.ServerPlayer;

public class SpSyncHelper {

    public static void sync(ServerPlayer player) {
        int sp = SpManager.getSp(player);
        int last = SpManager.getLastGrantedLevel(player);
        DmzImmersiveNetwork.sendToPlayer(new SpSyncS2C(sp, last), player);
    }
}