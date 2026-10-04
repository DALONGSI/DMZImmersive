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

    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> meteor;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> wolfFang;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kaiokenAttack;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> dragonFist;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> deadlyDance;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> superGodFist;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> deadlyDanceVegetto;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> oozaruFist;

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

    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> kaioken;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ultimate;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> fusion;

    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> superforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> androidforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> legendaryforms;
    public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> godforms;

    private final Map<String, ForgeConfigSpec.ConfigValue<List<? extends Integer>>> priceMap = new HashMap<>();

    static {
        var pair = new ForgeConfigSpec.Builder().configure(SpPriceConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public SpPriceConfig(ForgeConfigSpec.Builder builder) {
        builder.push("SP_Default");
        defaultSkillPrice = builder
                .comment("未在下面列出的普通技能，默认消耗多少 SP")
                .defineInRange("defaultSkillPrice", 1, 0, Integer.MAX_VALUE);
        defaultFormPrice = builder
                .comment("未在下面列出的变身技能，默认消耗多少 SP")
                .defineInRange("defaultFormPrice", 10, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("SP_Attack_Skills");
        builder.comment("攻击技能（气功类）");
        kiBarrage = def(builder, "ki_barrage", 1);
        masenko = def(builder, "masenko", 1);
        kamehameha = def(builder, "kamehameha", 1);
        galickGun = def(builder, "galick_gun", 1);
        taiyoken = def(builder, "taiyoken", 1);
        deathBeam = def(builder, "death_beam", 1);
        fakeMoon = def(builder, "fake_moon", 2);
        kienzan = def(builder, "kienzan", 2);
        makkanko = def(builder, "makkanko", 2);
        burningAttack = def(builder, "burning_attack", 2);
        bigBang = def(builder, "big_bang", 2);
        sokidan = def(builder, "sokidan", 2);
        kienzanDoble = def(builder, "kienzan_doble", 2);
        emperorDeathBeam = def(builder, "emperor_death_beam", 3);
        finalFlash = def(builder, "final_flash", 3);
        spiritbomb = def(builder, "spiritbomb", 5);
        supernova = def(builder, "supernova", 6);
        supernovaCooler = def(builder, "supernova_cooler", 8);
        finalExplosion = def(builder, "final_explosion", 10);
        soulPunisher = def(builder, "soul_punisher", 13);
        builder.pop();

        builder.push("SP_Strike_Skills");
        builder.comment("打击技");
        meteor = def(builder, "meteor", 2);
        wolfFang = def(builder, "wolf_fang", 2);
        kaiokenAttack = def(builder, "kaioken_attack", 3);
        dragonFist = def(builder, "dragon_fist", 3);
        deadlyDance = def(builder, "deadly_dance", 4);
        superGodFist = def(builder, "super_god_fist", 5);
        deadlyDanceVegetto = def(builder, "deadly_dance_vegetto", 6);
        oozaruFist = def(builder, "oozaru_fist", 8);
        builder.pop();

        builder.push("SP_Basic_Skills");
        builder.comment("基础技能");
        jump = def(builder, "jump", 2,2,2,4,4,4,6,6,6,8);
        sprint = def(builder, "sprint", 2,2,2,4,4,4,6,6,6,8);
        fly = def(builder, "fly", 2,2,2,4,4,4,6,6,6,8);
        meditation = def(builder, "meditation", 2,2,2,4,4,4,6,6,6,8);
        kisense = def(builder, "kisense", 2,2,2,4,4,4,6,6,6,8);
        kimanipulation = def(builder, "kimanipulation", 2,2,2,4,4,4,6,6,6,8);
        instantTransmission = def(builder, "instant_transmission", 2,2,2,4,4,4,6,6,6,8);
        defensePenetration = def(builder, "defense_penetration", 2,2,2,4,4,4,6,6,6,8);
        healingReduction = def(builder, "healing_reduction", 2,2,2,4,4,4,6,6,6,8);
        kiInfusion = def(builder, "ki_infusion", 2,2,2,4,4,4,6,6,6,8);
        kiprotection = def(builder, "kiprotection", 2,2,2,4,4,4,6,6,6,8);
        kiboost = def(builder, "kiboost", 2,2,2,4);
        kicontrol = def(builder, "kicontrol", 2);
        builder.comment("原版第 11 位是 -1（关卡解锁），必须保留");
        potentialunlock = def(builder, "potentialunlock", 2,2,2,4,4,4,6,6,6,8,-1,8,8);
        builder.pop();

        builder.push("SP_Stack_Skills");
        builder.comment("叠加变身技（所有种族都能学）");
        kaioken = def(builder, "kaioken", 50,70,90,110,130);
        ultimate = def(builder, "ultimate", 50);
        fusion = def(builder, "fusion", 50,70,90,110,130);
        builder.pop();

        builder.push("SP_Race_Forms");
        builder.comment("种族独特变身");
        builder.comment("legendaryforms 原版全是 -1（关卡解锁），必须保留");
        builder.comment("godforms 原版为空（不可购买）");
        superforms = def(builder, "superforms", 100,120,140,130,120,110,100,100);
        androidforms = def(builder, "androidforms", 100,120);
        legendaryforms = def(builder, "legendaryforms", -1,-1,-1);
        godforms = def(builder, "godforms", -1);
        builder.pop();
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
        if (prices.length == 0) {
            return isForm ? defaultFormPrice.get() : defaultSkillPrice.get();
        }
        if (level < 0) level = 0;
        if (level >= prices.length) return prices[prices.length - 1];
        return prices[level];
    }

    public int[] getPrices(String skillName) {
        if (skillName == null || skillName.isEmpty()) return new int[0];
        var entry = priceMap.get(skillName.toLowerCase());
        if (entry == null) return new int[0];
        List<? extends Integer> list = entry.get();
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            Integer v = list.get(i);
            result[i] = v != null ? v : -1;
        }
        return result;
    }

    public void clearCache() {
        // 不需要缓存，get() 每次直接从 ForgeConfigSpec 读
    }
}