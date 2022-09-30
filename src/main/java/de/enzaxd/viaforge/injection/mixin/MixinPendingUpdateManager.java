package de.enzaxd.viaforge.injection.mixin;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import net.minecraft.client.network.PendingUpdateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PendingUpdateManager.class)
public class MixinPendingUpdateManager {

    @Inject(method = "incrementSequence", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/PendingUpdateManager;pendingSequence:Z", shift = At.Shift.BEFORE), cancellable = true)
    public void injectIncrementSequence(CallbackInfoReturnable<PendingUpdateManager> cir) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_18_2))
            cir.setReturnValue((PendingUpdateManager) (Object) this);
    }


}
