package com.longsida.dmzimmersive;

import com.longsida.dmzimmersive.command.DMZImmersiveCommand;
import com.longsida.dmzimmersive.config.ImmersiveConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("dmzimmersive")
public class DMZImmersiveMod {

    public DMZImmersiveMod(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, ImmersiveConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DMZImmersiveCommand.register(event.getDispatcher());
    }
}