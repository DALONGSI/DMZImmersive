package com.longsida.dmzimmersive.command;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import com.longsida.dmzimmersive.sp.SpManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DMZImmersiveCommand {

    private static final String[] STATS = {"STR", "SKP", "PWR", "RES", "VIT", "ENE"};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dmzimmersive")
                        .then(Commands.literal("points")
                                .executes(ctx -> {
                                    CommandSourceStack source = ctx.getSource();
                                    ServerPlayer player = source.getPlayerOrException();

                                    StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
                                        int totalStats = data.getStats().getTotalStats();
                                        int capBase = ImmersiveConfig.COMMON.capBase.get();
                                        double capCoefficient = ImmersiveConfig.COMMON.capCoefficient.get();
                                        double cap = capBase + totalStats * capCoefficient;

                                        CompoundTag pdata = player.getPersistentData();
                                        float tpPool = pdata.getFloat("dmzimmersive_tp_pool");

//                                        boolean useSpecialized = ImmersiveConfig.COMMON.useSpecializedTraining.get();
                                        String mode = ImmersiveConfig.COMMON.trainingMode.get();

                                        source.sendSuccess(() -> Component.literal("§6=== DMZImmersive 隐藏点 ==="), false);
                                        source.sendSuccess(() -> Component.literal("§eTP 总池: §f" + String.format("%.2f", tpPool) + " §7/ " + String.format("%.2f", cap)), false);
                                        source.sendSuccess(() -> Component.literal("§6--- 六个子池 ---"), false);

                                        for (String stat : STATS) {
                                            float value = pdata.getFloat("dmzimmersive_growth_" + stat);
                                            source.sendSuccess(() -> Component.literal("§e" + stat + ": §f" + String.format("%.2f", value)), false);
                                        }

                                        source.sendSuccess(() -> Component.literal("§6--- 模式 ---"), false);
//                                        source.sendSuccess(() -> Component.literal("§e当前模式: §f" + (useSpecialized ? "B（专项训练）" : "A（固定权重）")), false);
                                        source.sendSuccess(() -> Component.literal("§e当前模式: §f" + mode), false);
                                    });

                                    return 1;
                                })
                        )
                        .then(Commands.literal("sp")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    StatsProvider.get(StatsCapability.INSTANCE, player).ifPresent(data -> {
                                        int available = SpManager.calcAvailable(player, data);
                                        int level = data.getLevel();
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("§b可用 SP: §f" + available + " §7(等级 " + level + ")"),
                                                false);
                                    });
                                    return 1;
                                })
                        )
        );
    }
}