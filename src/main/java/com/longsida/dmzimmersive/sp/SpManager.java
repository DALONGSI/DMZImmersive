package com.longsida.dmzimmersive.sp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class SpManager {

    private static final String KEY_SP = "dmzimmersive_sp";
    private static final String KEY_LAST_LEVEL = "dmzimmersive_sp_last_level";

    private SpManager() {}

    public static int getSp(ServerPlayer player) {
        return player.getPersistentData().getInt(KEY_SP);
    }

    public static void setSp(ServerPlayer player, int amount) {
        player.getPersistentData().putInt(KEY_SP, Math.max(0, amount));
        SpSyncHelper.sync(player);
    }

    public static void addSp(ServerPlayer player, int amount) {
        setSp(player, getSp(player) + amount);
    }

    public static boolean spendSp(ServerPlayer player, int amount) {
        if (amount <= 0) return true;
        int current = getSp(player);
        if (current < amount) return false;
        setSp(player, current - amount);
        return true;
    }

    public static int getLastGrantedLevel(ServerPlayer player) {
        return player.getPersistentData().getInt(KEY_LAST_LEVEL);
    }

    public static void setLastGrantedLevel(ServerPlayer player, int level) {
        player.getPersistentData().putInt(KEY_LAST_LEVEL, Math.max(0, level));
        SpSyncHelper.sync(player);
    }

    public static int grantSpForLevel(ServerPlayer player, int currentLevel) {
        int last = getLastGrantedLevel(player);
        if (currentLevel == last) return 0;

        if (currentLevel < last) {
            setLastGrantedLevel(player, currentLevel);
            return 0;
        }

        int gain = currentLevel - last;
        addSp(player, gain);
        setLastGrantedLevel(player, currentLevel);
        return gain;
    }
}