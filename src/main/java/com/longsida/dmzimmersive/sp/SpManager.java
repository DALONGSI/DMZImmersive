package com.longsida.dmzimmersive.sp;

import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.skills.Skill;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import com.longsida.dmzimmersive.network.DmzImmersiveNetwork;
import com.longsida.dmzimmersive.network.packet.SpSyncS2C;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpManager {

    private static final Map<UUID, Integer> LAST_SYNCED = new HashMap<>();

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
            if (prices.length == 0) {
                prices = SpPriceConfig.INSTANCE.getPrices(skillName);
            }
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
        Integer last = LAST_SYNCED.get(player.getUUID());
        if (last != null && last == available) return;
        LAST_SYNCED.put(player.getUUID(), available);
        DmzImmersiveNetwork.sendToPlayer(
                new SpSyncS2C(available, data.getLevel()), player);
    }

    public static void clearCache(UUID uuid) {
        LAST_SYNCED.remove(uuid);
    }
}