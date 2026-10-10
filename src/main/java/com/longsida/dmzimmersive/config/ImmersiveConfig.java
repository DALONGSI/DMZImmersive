package com.longsida.dmzimmersive.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ImmersiveConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        SPEC = pair.getRight();
    }

    public static class Common {
        // 上限 最低下限、百分比提升率
        public final ForgeConfigSpec.IntValue capBase;
        public final ForgeConfigSpec.DoubleValue capCoefficient;

        // TP 获取路径
        public final ForgeConfigSpec.IntValue tpPerHit;
        public final ForgeConfigSpec.DoubleValue tpHealthRatio;
        public final ForgeConfigSpec.DoubleValue enemyHealthPerStat;
        public final ForgeConfigSpec.IntValue killsToCap;

        // 模式B 训练场次
        public final ForgeConfigSpec.IntValue trainingSessions;

        // 模式选择
//        public final ForgeConfigSpec.BooleanValue useSpecializedTraining;
        public final ForgeConfigSpec.ConfigValue<String> trainingMode;

        // tp分成 直接经系统分配、自己手动加点比例
        public final ForgeConfigSpec.DoubleValue directRatio;
        public final ForgeConfigSpec.DoubleValue manualRatio;

        // 余数给谁
        public final ForgeConfigSpec.ConfigValue<String> remainderStat;

        // 模式 B (TP子池上限权重) 六项权重
        public final ForgeConfigSpec.DoubleValue weightStr;
        public final ForgeConfigSpec.DoubleValue weightSkp;
        public final ForgeConfigSpec.DoubleValue weightPwr;
        public final ForgeConfigSpec.DoubleValue weightRes;
        public final ForgeConfigSpec.DoubleValue weightVit;
        public final ForgeConfigSpec.DoubleValue weightEne;

        // 模式 A 的六项权重
        public final ForgeConfigSpec.DoubleValue modeAStr;
        public final ForgeConfigSpec.DoubleValue modeASkp;
        public final ForgeConfigSpec.DoubleValue modeAPwr;
        public final ForgeConfigSpec.DoubleValue modeARes;
        public final ForgeConfigSpec.DoubleValue modeAVit;
        public final ForgeConfigSpec.DoubleValue modeAEne;

        // 模式 B 的 6×6 分配器
        public final ForgeConfigSpec.ConfigValue<String> distributorStr;
        public final ForgeConfigSpec.ConfigValue<String> distributorSkp;
        public final ForgeConfigSpec.ConfigValue<String> distributorPwr;
        public final ForgeConfigSpec.ConfigValue<String> distributorRes;
        public final ForgeConfigSpec.ConfigValue<String> distributorVit;
        public final ForgeConfigSpec.ConfigValue<String> distributorEne;

        //获取训练模式
        public boolean isModeA() {
            String m = trainingMode.get();
            return "A".equalsIgnoreCase(m) || "AB".equalsIgnoreCase(m);
        }

        public boolean isModeB() {
            String m = trainingMode.get();
            return "B".equalsIgnoreCase(m) || "AB".equalsIgnoreCase(m);
        }

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("cap");
            capBase = builder.defineInRange("capBase", 10, 0, Integer.MAX_VALUE);
            capCoefficient = builder.defineInRange("capCoefficient", 0.05, 0.0, 10.0);
            builder.pop();

            builder.push("tp");
            tpPerHit = builder.defineInRange("tpPerHit", 2, 0, Integer.MAX_VALUE);
            tpHealthRatio = builder.defineInRange("tpHealthRatio", 0.25, 0.0, 10.0);
            enemyHealthPerStat = builder.defineInRange("enemyHealthPerStat", 1.5, 0.0, 100.0);
            killsToCap = builder.defineInRange("killsToCap", 25, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("training");
            trainingSessions = builder.defineInRange("trainingSessions", 25, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("mode");
//            useSpecializedTraining = builder.define("useSpecializedTraining", false);
            trainingMode = builder.comment(
                    "训练模式：",
                    "  A  = 固定权重（隐藏池按权重分配）",
                    "  B  = 专项训练（每个子池独立结算）",
                    "  AB = 同时开启 A 和 B"
            ).define("trainingMode", "A");
            directRatio = builder.defineInRange("directRatio", 0.9, 0.0, 1.0);
            manualRatio = builder.defineInRange("manualRatio", 0.1, 0.0, 1.0);
            remainderStat = builder
                    .comment("余数给哪一项，可选 STR/SKP/PWR/RES/VIT/ENE")
                    .define("remainderStat", "VIT");
            builder.pop();

            // 模式B Tp子池上限比例
            builder.push("weights");
            weightStr = builder.defineInRange("STR", 0.1667, 0.0, 1.0);
            weightSkp = builder.defineInRange("SKP", 0.1667, 0.0, 1.0);
            weightPwr = builder.defineInRange("PWR", 0.1667, 0.0, 1.0);
            weightRes = builder.defineInRange("RES", 0.1667, 0.0, 1.0);
            weightVit = builder.defineInRange("VIT", 0.1667, 0.0, 1.0);
            weightEne = builder.defineInRange("ENE", 0.1667, 0.0, 1.0);
            builder.pop();

            builder.push("modeA");
            modeAStr = builder.defineInRange("STR", 0.1, 0.0, 1.0);
            modeASkp = builder.defineInRange("SKP", 0.1, 0.0, 1.0);
            modeAPwr = builder.defineInRange("PWR", 0.1, 0.0, 1.0);
            modeARes = builder.defineInRange("RES", 0.1, 0.0, 1.0);
            modeAVit = builder.defineInRange("VIT", 0.3, 0.0, 1.0);
            modeAEne = builder.defineInRange("ENE", 0.3, 0.0, 1.0);
            builder.pop();

            //模式B Tp子池转为属性点比例
            builder.push("modeB");
            distributorStr = builder.define("STR", "0.6,0,0,0,0,0");
            distributorSkp = builder.define("SKP", "0,0.6,0,0,0,0");
            distributorPwr = builder.define("PWR", "0,0,0.6,0,0,0");
            distributorRes = builder.define("RES", "0,0,0,0.6,0,0");
            distributorVit = builder.define("VIT", "0,0,0,0,1.8,0");
            distributorEne = builder.define("ENE", "0,0,0,0,0,1.8");
            builder.pop();
        }
    }

    public static double getWeight(String stat) {
        return switch (stat.toUpperCase()) {
            case "STR" -> COMMON.weightStr.get();
            case "SKP" -> COMMON.weightSkp.get();
            case "PWR" -> COMMON.weightPwr.get();
            case "RES" -> COMMON.weightRes.get();
            case "VIT" -> COMMON.weightVit.get();
            case "ENE" -> COMMON.weightEne.get();
            default -> 1.0 / 6.0;
        };
    }

    public static double getModeAWeight(String stat) {
        return switch (stat.toUpperCase()) {
            case "STR" -> COMMON.modeAStr.get();
            case "SKP" -> COMMON.modeASkp.get();
            case "PWR" -> COMMON.modeAPwr.get();
            case "RES" -> COMMON.modeARes.get();
            case "VIT" -> COMMON.modeAVit.get();
            case "ENE" -> COMMON.modeAEne.get();
            default -> 1.0 / 6.0;
        };
    }

    public static double[] getDistributor(String sourceStat) {
        String raw = switch (sourceStat.toUpperCase()) {
            case "STR" -> COMMON.distributorStr.get();
            case "SKP" -> COMMON.distributorSkp.get();
            case "PWR" -> COMMON.distributorPwr.get();
            case "RES" -> COMMON.distributorRes.get();
            case "VIT" -> COMMON.distributorVit.get();
            case "ENE" -> COMMON.distributorEne.get();
            default -> "0,0,0,0,0,0";
        };
        String[] parts = raw.split(",");
        double[] result = new double[6];
        for (int i = 0; i < Math.min(parts.length, 6); i++) {
            try { result[i] = Double.parseDouble(parts[i].trim()); } catch (NumberFormatException e) { result[i] = 0; }
        }
        return result;
    }
}