package com.longsida.dmzimmersive.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RecoveringEffect extends MobEffect {

    public RecoveringEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x55FF55);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}