package com.longsida.dmzimmersive.mixin;

import com.dragonminez.common.stats.StatsData;
import com.longsida.dmzimmersive.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsData.class)
public class StatsDataMixin {

    @Inject(method = "resetPlayerProgress", at = @At("RETURN"), remap = false)
    private void onResetPlayerProgress(ServerPlayer player, Integer keepPercentage,
                                       boolean keepSkills, boolean forceSaiyanTail,
                                       CallbackInfo ci) {
        if (player == null) return;
        SpManager.clearCache(player.getUUID());
        SpManager.recalcAndSync(player, (StatsData) (Object) this);
    }
}