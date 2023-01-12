package de.florianmichael.tarasande_protocol_spoofer.injection.mixin.haproxyhack;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableHAProxyHack;
import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnectionSubInitChannel {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void addChannelHandlers(Channel channel, CallbackInfo ci) {
        final EntrySidebarPanelToggleableHAProxyHack haProxyHack = ManagerScreenExtension.INSTANCE.get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableHAProxyHack.class);

        if (haProxyHack.getEnabled().getValue()) {
            channel.pipeline().addFirst("haproxy-encoder", HAProxyMessageEncoder.INSTANCE);
            channel.pipeline().addLast(haProxyHack.createHandler());
        }
    }
}
