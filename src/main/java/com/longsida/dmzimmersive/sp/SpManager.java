package com.longsida.dmzimmersive.sp;

import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.skills.Skill;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import com.longsida.dmzimmersive.network.DmzImmersiveNetwork;
import com.longsida.dmzimmersive.network.packet.SpSyncS2C;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpManager {

    private static final Map<UUID, int[]> LAST_SYNCED = new HashMap<>();

    private SpManager() {}

    public static int calcAvailable(ServerPlayer player, StatsData data) {
        int totalEarned = data.getLevel();
        int totalSpent = 0;
        String race = (data.getCharacter() != null) ? data.getCharacter().getRaceName() : "";

        for (Map.Entry<String, Skill> entry : data.getSkills().getAllSkills().entrySet()) {
            String skillName = entry.getKey();
            Skill skill = entry.getValue();
            if (skill == null) continue;
            int lv = skill.getLevel();
            if (lv <= 0) continue;

            int[] prices = SpPriceConfig.INSTANCE.getPrices(skillName, race);
            if (prices.length == 0) prices = SpPriceConfig.INSTANCE.getPrices(skillName);
            if (prices.length == 0) continue;

            for (int i = 0; i < lv; i++) {
                int idx = Math.min(i, prices.length - 1);
                int p = prices[idx];
                if (p > 0) totalSpent += p;
            }
        }

        return Math.max(0, totalEarned - totalSpent);
    }

    public static void recalcAndSync(ServerPlayer player, StatsData data) {
        int available = calcAvailable(player, data);
        int level = data.getLevel();

        int totalStats = data.getStats().getTotalStats();
        int capBase = ImmersiveConfig.COMMON.capBase.get();
        double capCoefficient = ImmersiveConfig.COMMON.capCoefficient.get();
        float cap = (float) (capBase + totalStats * capCoefficient);

        float tpPool = player.getPersistentData().getFloat("dmzimmersive_tp_pool");

        float pStr = player.getPersistentData().getFloat("dmzimmersive_growth_STR");
        float pSkp = player.getPersistentData().getFloat("dmzimmersive_growth_SKP");
        float pPwr = player.getPersistentData().getFloat("dmzimmersive_growth_PWR");
        float pRes = player.getPersistentData().getFloat("dmzimmersive_growth_RES");
        float pVit = player.getPersistentData().getFloat("dmzimmersive_growth_VIT");
        float pEne = player.getPersistentData().getFloat("dmzimmersive_growth_ENE");

        int poolBits = Float.floatToIntBits(tpPool);
        int capBits = Float.floatToIntBits(cap);

        int[] last = LAST_SYNCED.get(player.getUUID());
        if (last != null && last[0] == available && last[1] == level
                && last[2] == poolBits && last[3] == capBits) return;

        LAST_SYNCED.put(player.getUUID(), new int[]{available, level, poolBits, capBits});
        DmzImmersiveNetwork.sendToPlayer(
                new SpSyncS2C(available, level, tpPool, cap, pStr, pSkp, pPwr, pRes, pVit, pEne),
                player);
    }

    public static void clearCache(UUID uuid) {
        LAST_SYNCED.remove(uuid);
    }
}