package com.longsida.dmzimmersive.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CombatReadyEffect extends MobEffect {

    public CombatReadyEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4444);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}