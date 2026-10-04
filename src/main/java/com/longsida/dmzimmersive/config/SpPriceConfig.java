package com.longsida.dmzimmersive.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpPriceConfig {

    public static final ForgeConfigSpec SPEC;
    public static final SpPriceConfig INSTANCE;

    public final ForgeConfigSpec.IntValue defaultSkillPrice;
    public final ForgeConfigSpec.IntValue defaultFormPrice;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> skillPrices;

    private final Map<String, int[]> parsedCache = new HashMap<>();

    static {
        var pair = new ForgeConfigSpec.Builder().configure(SpPriceConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public SpPriceConfig(ForgeConfigSpec.Builder builder) {
        builder.push("spPrices");

        defaultSkillPrice = builder
                .comment("未在 skillPrices 中列出的普通技能，默认消耗多少 SP")
                .defineInRange("defaultSkillPrice", 1, 0, Integer.MAX_VALUE);

        defaultFormPrice = builder
                .comment("未在 skillPrices 中列出的变身技能，默认消耗多少 SP")
                .defineInRange("defaultFormPrice", 10, 0, Integer.MAX_VALUE);

        skillPrices = builder
                .comment(
                        "每个技能/变身的 SP 价格表。格式：\"技能名=价格1,价格2,...\"",
                        "价格列表按等级顺序，长度 = 该技能最大等级。",
                        "只写一个数字表示所有等级同价。",
                        "-1 表示禁止购买该等级。",
                        "例：",
                        "  \"kamehameha=1\"",
                        "  \"jump=1,1,1,1,1,1,1,1,1,1\"",
                        "  \"superforms=10,10,10,10,10\"",
                        "其他 mod 新增技能不写在这里，会自动走 defaultSkillPrice / defaultFormPrice。"
                )
                .defineList("skillPrices", SpPriceConfig::defaultPrices,
                        o -> o instanceof String);

        builder.pop();
    }

    private static List<String> defaultPrices() {
        List<String> list = new ArrayList<>();
        list.add("kamehameha=1");
        list.add("galick_gun=1");
        list.add("masenko=1");
        list.add("ki_barrage=1");
        list.add("kienzan=1");
        list.add("kienzan_doble=1");
        list.add("death_beam=1");
        list.add("makkanko=1");
        list.add("burning_attack=1");
        list.add("big_bang=1");
        list.add("sokidan=1");
        list.add("taiyoken=1");
        list.add("fake_moon=1");
        list.add("emperor_death_beam=1");
        list.add("final_flash=1");
        list.add("spiritbomb=1");
        list.add("supernova=1");
        list.add("supernova_cooler=1");
        list.add("final_explosion=1");
        list.add("soul_punisher=1");

        list.add("jump=1,1,1,1,1,1,1,1,1,1");
        list.add("sprint=1,1,1,1,1,1,1,1,1,1");
        list.add("fly=1,1,1,1,1,1,1,1,1,1");
        list.add("meditation=1,1,1,1,1,1,1,1,1,1");
        list.add("kisense=1,1,1,1,1,1,1,1,1,1");
        list.add("kimanipulation=1,1,1,1,1,1,1,1,1,1");
        list.add("instant_transmission=1,1,1,1,1,1,1,1,1,1");
        list.add("defense_penetration=1,1,1,1,1,1,1,1,1,1");
        list.add("healing_reduction=1,1,1,1,1,1,1,1,1,1");
        list.add("ki_infusion=1,1,1,1,1,1,1,1,1,1");
        list.add("kiprotection=1,1,1,1,1,1,1,1,1,1");
        list.add("kiboost=1,1,1,1");
        list.add("kicontrol=1");

        list.add("meteor=1");
        list.add("wolf_fang=1");
        list.add("kaioken_attack=1");
        list.add("dragon_fist=1");
        list.add("deadly_dance=1");
        list.add("super_god_fist=1");
        list.add("deadly_dance_vegetto=1");
        list.add("oozaru_fist=1");

        list.add("kaioken=1,1,1,1,1");
        list.add("ultimate=1");
        list.add("fusion=1,1,1,1,1");
        list.add("potentialunlock=1,1,1,1,1,1,1,1,1,1,1,1,1");

        list.add("superforms=10,10,10,10,10");
        list.add("legendaryforms=10,10,10,10,10");
        list.add("godforms=10,10,10,10,10");
        list.add("androidforms=10,10,10,10,10");

        return list;
    }

    public int getPrice(String skillName, int level, boolean isForm) {
        int[] prices = getPrices(skillName);
        if (prices.length == 0) return isForm ? defaultFormPrice.get() : defaultSkillPrice.get();
        if (level < 0) level = 0;
        if (level >= prices.length) return prices[prices.length - 1];
        return prices[level];
    }

    public int[] getPrices(String skillName) {
        if (skillName == null || skillName.isEmpty()) return new int[0];
        String key = skillName.toLowerCase();

        int[] cached = parsedCache.get(key);
        if (cached != null) return cached;

        for (String raw : skillPrices.get()) {
            if (raw == null || raw.isEmpty()) continue;
            int eq = raw.indexOf('=');
            if (eq <= 0 || eq >= raw.length() - 1) continue;

            String name = raw.substring(0, eq).trim().toLowerCase();
            if (!name.equals(key)) continue;

            int[] parsed = parsePrices(raw.substring(eq + 1).trim());
            parsedCache.put(key, parsed);
            return parsed;
        }

        int[] empty = new int[0];
        parsedCache.put(key, empty);
        return empty;
    }

    private static int[] parsePrices(String valuePart) {
        String[] parts = valuePart.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                result[i] = -1;
            }
        }
        return result;
    }

    public void clearCache() {
        parsedCache.clear();
    }
}