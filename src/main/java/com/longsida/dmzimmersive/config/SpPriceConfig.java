package com.longsida.dmzimmersive.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpPriceConfig {

    public static final ForgeConfigSpec SPEC;
    public static final SpPriceConfig INSTANCE;

    public final ForgeConfigSpec.IntValue defaultSkillPrice;
    public final ForgeConfigSpec.IntValue defaultFormPrice;

    // 攻击技能
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kiBarrage;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> masenko;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kamehameha;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> galickGun;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> taiyoken;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> deathBeam;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> fakeMoon;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kienzan;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> makkanko;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> burningAttack;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> bigBang;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> sokidan;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kienzanDoble;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> emperorDeathBeam;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> finalFlash;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> spiritbomb;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> supernova;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> supernovaCooler;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> finalExplosion;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> soulPunisher;

    // 打击技
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> meteor;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> wolfFang;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kaiokenAttack;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> dragonFist;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> deadlyDance;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> superGodFist;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> deadlyDanceVegetto;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> oozaruFist;

    // 基础技能
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> jump;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> sprint;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> fly;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> meditation;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kisense;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kimanipulation;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> instantTransmission;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> defensePenetration;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> healingReduction;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kiInfusion;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kiprotection;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kiboost;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kicontrol;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> potentialunlock;

    // 叠加技能
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kaioken;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ultimate;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> fusion;

    // 种族变身（默认值，所有种族共用，可被下面种族 section 覆盖）
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> superforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> androidforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> legendaryforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> godforms;

    // 种族专属覆盖
    private final Map<String, Map<String, ForgeConfigSpec.ConfigValue<List<? extends Integer>>>> raceMap = new HashMap<>();

    private final Map<String, ForgeConfigSpec.ConfigValue<List<? extends Integer>>> priceMap = new HashMap<>();
    private final Map<String, int[]> parsedCache = new HashMap<>();
    private final Map<String, int[]> raceCache = new HashMap<>();

    static {
        var pair = new ForgeConfigSpec.Builder().configure(SpPriceConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public SpPriceConfig(ForgeConfigSpec.Builder builder) {
        builder.push("SP_Default");
        defaultSkillPrice = builder.comment("未列出的普通技能默认 SP 价").defineInRange("defaultSkillPrice", 1, 0, Integer.MAX_VALUE);
        defaultFormPrice = builder.comment("未列出的变身技能默认 SP 价").defineInRange("defaultFormPrice", 10, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("SP_Attack_Skills");
        kiBarrage = def(builder, "ki_barrage", 5);
        masenko = def(builder, "masenko", 7);
        kamehameha = def(builder, "kamehameha", 7);
        galickGun = def(builder, "galick_gun", 7);
        taiyoken = def(builder, "taiyoken", 5);
        deathBeam = def(builder, "death_beam", 5);
        fakeMoon = def(builder, "fake_moon", 5);
        kienzan = def(builder, "kienzan", 5);
        makkanko = def(builder, "makkanko", 7);
        burningAttack = def(builder, "burning_attack", 5);
        bigBang = def(builder, "big_bang", 7);
        sokidan = def(builder, "sokidan", 5);
        kienzanDoble = def(builder, "kienzan_doble", 7);
        emperorDeathBeam = def(builder, "emperor_death_beam", 7);
        finalFlash = def(builder, "final_flash", 9);
        spiritbomb = def(builder, "spiritbomb", 10);
        supernova = def(builder, "supernova", 6);
        supernovaCooler = def(builder, "supernova_cooler", 8);
        finalExplosion = def(builder, "final_explosion", 10);
        soulPunisher = def(builder, "soul_punisher", 13);
        builder.pop();

        builder.push("SP_Strike_Skills");
        meteor = def(builder, "meteor", 3);
        wolfFang = def(builder, "wolf_fang", 3);
        kaiokenAttack = def(builder, "kaioken_attack", 5);
        dragonFist = def(builder, "dragon_fist", 7);
        deadlyDance = def(builder, "deadly_dance", 5);
        superGodFist = def(builder, "super_god_fist", 7);
        deadlyDanceVegetto = def(builder, "deadly_dance_vegetto", 5);
        oozaruFist = def(builder, "oozaru_fist", 7);
        builder.pop();

        builder.push("SP_Basic_Skills");
        jump = def(builder, "jump", 2,2,2,3,3,3,4,4,4,5);
        sprint = def(builder, "sprint", 2,2,2,3,3,3,4,4,4,5);
        fly = def(builder, "fly", 2,2,2,4,4,4,6,6,6,8);
        meditation = def(builder, "meditation", 2,2,2,3,3,3,4,4,4,5);
        kisense = def(builder, "kisense", 2,2,2,3,3,3,4,4,4,5);
        kimanipulation = def(builder, "kimanipulation", 3,3,3,4,4,4,5,5,5,6);
        instantTransmission = def(builder, "instant_transmission", 2,2,2,3,3,3,4,4,4,5);
        defensePenetration = def(builder, "defense_penetration", 3,3,3,4,4,4,5,5,5,6);
        healingReduction = def(builder, "healing_reduction", 3,3,3,4,4,4,5,5,5,6);
        kiInfusion = def(builder, "ki_infusion", 3,3,3,4,4,4,5,5,5,6);
        kiprotection = def(builder, "kiprotection", 3,3,3,4,4,4,5,5,5,6);
        kiboost = def(builder, "kiboost", 7,7,9,11);
        kicontrol = def(builder, "kicontrol", 10);
        potentialunlock = def(builder, "potentialunlock", 4,4,4,5,5,5,6,6,6,7,-1,9,9);
        builder.pop();

        builder.push("SP_Stack_Skills");
        kaioken = def(builder, "kaioken", 50,70,90,110,130);
        ultimate = def(builder, "ultimate", 50);
        fusion = def(builder, "fusion", 50,70,90,110,130);
        builder.pop();

        builder.push("SP_Race_Forms");
        builder.comment("全局默认（未在下面种族 section 列出的技能走这里）");
        superforms = def(builder, "superforms", 65,75,85,110,150,200,240,260);
        androidforms = def(builder, "androidforms", 100,120);
        legendaryforms = def(builder, "legendaryforms", -1,-1,-1);
        godforms = def(builder, "godforms", -1);
        builder.pop();

        // 种族专属
        pushRace(builder, "saiyan",
                new int[]{65,75,85,110,150,200,240,260},
                null,
                new int[]{-1,-1,-1},
                new int[]{-1});
        pushRace(builder, "human",
                new int[]{75,110,200,260},
                new int[]{110,260},
                new int[]{-1,-1,-1},
                new int[]{-1});
        pushRace(builder, "frostdemon",
                new int[]{70,85,150,250,275},
                null,
                new int[]{-1,-1,-1},
                new int[]{-1});
        pushRace(builder, "majin",
                new int[]{80,130,240,270},
                null,
                new int[]{-1,-1,-1},
                new int[]{-1});
        pushRace(builder, "namekian",
                new int[]{80,130,240,275},
                null,
                new int[]{-1,-1,-1},
                new int[]{-1});
        pushRace(builder, "bioandroid",
                new int[]{85,140,250,285},
                null,
                new int[]{-1,-1,-1},
                new int[]{-1});
    }

    private void pushRace(ForgeConfigSpec.Builder builder, String race,
                          int[] superforms, int[] androidforms,
                          int[] legendaryforms, int[] godforms) {
        builder.push("SP_Race_Forms_" + race);
        Map<String, ForgeConfigSpec.ConfigValue<List<? extends Integer>>> map = new HashMap<>();
        if (superforms != null) map.put("superforms", defRaw(builder, "superforms", superforms));
        if (androidforms != null) map.put("androidforms", defRaw(builder, "androidforms", androidforms));
        if (legendaryforms != null) map.put("legendaryforms", defRaw(builder, "legendaryforms", legendaryforms));
        if (godforms != null) map.put("godforms", defRaw(builder, "godforms", godforms));
        raceMap.put(race.toLowerCase(), map);
        builder.pop();
    }

    private ForgeConfigSpec.ConfigValue<List<? extends Integer>> defRaw(
            ForgeConfigSpec.Builder builder, String name, int[] defaults) {
        Integer[] boxed = new Integer[defaults.length];
        for (int i = 0; i < defaults.length; i++) boxed[i] = defaults[i];
        return builder.defineList(name, List.of(boxed),
                o -> o instanceof Integer && (Integer) o >= -1);
    }

    private ForgeConfigSpec.ConfigValue<List<? extends Integer>> def(
            ForgeConfigSpec.Builder builder, String name, Integer... defaults) {
        var value = builder.defineList(name, List.of(defaults),
                o -> o instanceof Integer && (Integer) o >= -1);
        priceMap.put(name.toLowerCase(), value);
        return value;
    }

    public int getPrice(String skillName, int level, boolean isForm) {
        int[] prices = getPrices(skillName);
        if (prices.length == 0) return isForm ? defaultFormPrice.get() : defaultSkillPrice.get();
        if (level < 0) level = 0;
        if (level >= prices.length) return prices[prices.length - 1];
        return prices[level];
    }

    public int getPrice(String skillName, int level, boolean isForm, String raceName) {
        int[] racePrices = getPrices(skillName, raceName);
        if (racePrices.length > 0) {
            if (level < 0) level = 0;
            if (level >= racePrices.length) return racePrices[racePrices.length - 1];
            return racePrices[level];
        }
        return getPrice(skillName, level, isForm);
    }

    public int[] getPrices(String skillName) {
        if (skillName == null || skillName.isEmpty()) return new int[0];
        String key = skillName.toLowerCase();

        int[] cached = parsedCache.get(key);
        if (cached != null) return cached;

        var entry = priceMap.get(key);
        if (entry == null) {
            int[] empty = new int[0];
            parsedCache.put(key, empty);
            return empty;
        }
        int[] result = toArray(entry.get());
        parsedCache.put(key, result);
        return result;
    }

    public int[] getPrices(String skillName, String raceName) {
        if (skillName == null || skillName.isEmpty()) return new int[0];

        String skillKey = skillName.toLowerCase();
        String raceKey = (raceName != null ? raceName : "").toLowerCase();

        if (raceKey.isEmpty()) return getPrices(skillName);

        String cacheKey = raceKey + ":" + skillKey;
        int[] cached = raceCache.get(cacheKey);
        if (cached != null) return cached;

        Map<String, ForgeConfigSpec.ConfigValue<List<? extends Integer>>> raceEntry = raceMap.get(raceKey);
        if (raceEntry != null) {
            var value = raceEntry.get(skillKey);
            if (value != null) {
                int[] result = toArray(value.get());
                raceCache.put(cacheKey, result);
                return result;
            }
        }

        int[] empty = new int[0];
        raceCache.put(cacheKey, empty);
        return empty;
    }

    private static int[] toArray(List<? extends Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            Integer v = list.get(i);
            result[i] = v != null ? v : -1;
        }
        return result;
    }

    public void clearCache() {
        parsedCache.clear();
        raceCache.clear();
    }
}