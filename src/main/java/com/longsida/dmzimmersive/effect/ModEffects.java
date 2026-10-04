package com.longsida.dmzimmersive.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "dmzimmersive");

    public static final RegistryObject<MobEffect> COMBAT_READY =
            EFFECTS.register("combat_ready", CombatReadyEffect::new);

    public static final RegistryObject<MobEffect> RECOVERING =
            EFFECTS.register("recovering", RecoveringEffect::new);
}