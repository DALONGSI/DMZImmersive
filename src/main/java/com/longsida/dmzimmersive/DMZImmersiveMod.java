package com.longsida.dmzimmersive;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsProvider;
import com.longsida.dmzimmersive.command.DMZImmersiveCommand;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import com.longsida.dmzimmersive.config.SpPriceConfig;
import com.longsida.dmzimmersive.network.DmzImmersiveNetwork;
import com.longsida.dmzimmersive.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("dmzimmersive")
public class DMZImmersiveMod {

    public DMZImmersiveMod(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, ImmersiveConfig.SPEC,
                "dmzimmersive/immersive-common.toml");
        context.registerConfig(ModConfig.Type.COMMON, SpPriceConfig.SPEC,
                "dmzimmersive/sp-prices.toml");
        context.getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(DmzImmersiveNetwork::register);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DMZImmersiveCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SpPriceConfig.SPEC) {
            SpPriceConfig.INSTANCE.clearCache();
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            StatsProvider.get(StatsCapability.INSTANCE, sp).ifPresent(data -> {
                SpManager.clearCache(sp.getUUID());
                SpManager.recalcAndSync(sp, data);
            });
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            StatsProvider.get(StatsCapability.INSTANCE, sp).ifPresent(data ->
                    SpManager.recalcAndSync(sp, data));
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            StatsProvider.get(StatsCapability.INSTANCE, sp).ifPresent(data ->
                    SpManager.recalcAndSync(sp, data));
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SpManager.clearCache(event.getEntity().getUUID());
    }
}