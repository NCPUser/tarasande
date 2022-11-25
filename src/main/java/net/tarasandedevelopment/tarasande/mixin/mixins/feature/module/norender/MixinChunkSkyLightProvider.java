package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSkyLightProvider.class)
public class MixinChunkSkyLightProvider {

    @Inject(method = "recalculateLevel", at = @At("HEAD"), cancellable = true)
    public void noRender_recalculateLevel(long id, long excludedId, int maxLevel, CallbackInfoReturnable<Integer> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getSkylightUpdates().should()) {
            cir.setReturnValue(15);
        }
    }
}
