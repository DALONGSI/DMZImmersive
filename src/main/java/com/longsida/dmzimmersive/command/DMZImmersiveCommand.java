package com.longsida.dmzimmersive.command;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import com.longsida.dmzimmersive.sp.SpManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

                                        boolean useSpecialized = ImmersiveConfig.COMMON.useSpecializedTraining.get();

                                        source.sendSuccess(() -> Component.literal("§6=== DMZImmersive 隐藏点 ==="), false);
                                        source.sendSuccess(() -> Component.literal("§eTP 总池: §f" + String.format("%.2f", tpPool) + " §7/ " + String.format("%.2f", cap)), false);
                                        source.sendSuccess(() -> Component.literal("§6--- 六个子池 ---"), false);

                                        for (String stat : STATS) {
                                            float value = pdata.getFloat("dmzimmersive_growth_" + stat);
                                            source.sendSuccess(() -> Component.literal("§e" + stat + ": §f" + String.format("%.2f", value)), false);
                                        }

                                        source.sendSuccess(() -> Component.literal("§6--- 模式 ---"), false);
                                        source.sendSuccess(() -> Component.literal("§e当前模式: §f" + (useSpecialized ? "B（专项训练）" : "A（固定权重）")), false);
                                    });

                                    return 1;
                                })
                        )
                        .then(Commands.literal("sp")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int sp = SpManager.getSp(player);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§b当前 SP: §f" + sp), false);
                                    return sp;
                                })
                                .then(Commands.literal("set")
                                        .requires(src -> src.hasPermission(2))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    SpManager.setSp(player, amount);
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§a已将 SP 设置为 §f" + amount), true);
                                                    return amount;
                                                })
                                        )
                                )
                                .then(Commands.literal("add")
                                        .requires(src -> src.hasPermission(2))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                                    SpManager.addSp(player, amount);
                                                    int now = SpManager.getSp(player);
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§a已增加 SP，当前: §f" + now), true);
                                                    return now;
                                                })
                                        )
                                )
                        )
        );
    }
}