package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets1_13 {

    @Inject(method = "toNewId", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private static void returnAirDefault(int oldId, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

}
