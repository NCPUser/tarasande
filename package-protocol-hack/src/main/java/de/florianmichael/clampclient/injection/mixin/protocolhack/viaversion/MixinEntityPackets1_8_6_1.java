package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.EntityPackets$6$1", remap = false)
public abstract class MixinEntityPackets1_8_6_1 {

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "transform(Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;Ljava/lang/Short;)Ljava/lang/Integer;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/data/entity/EntityTracker;clientEntityId()I"), cancellable = true)
    private void fixOutOfBoundsSlot(PacketWrapper wrapper, Short slot, CallbackInfoReturnable<Integer> cir) throws Exception {
        final int entityId = wrapper.get(Type.VAR_INT, 0);
        final int clientPlayerId = wrapper.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId();
        if (slot < 0 || slot > 4 || (entityId == clientPlayerId && slot > 3)) {
            wrapper.cancel();
            cir.setReturnValue(0);
        }
    }

}
