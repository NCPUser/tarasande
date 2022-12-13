package de.florianmichael.tarasande_protocol_spoofer.mixin;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableForgeFaker;
import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.ForgeCreator;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreenSubRun {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;"))
    public ClientConnection hookForgeHandler(InetSocketAddress address, boolean useEpoll) {
        final ClientConnection connection = ClientConnection.connect(address, useEpoll);
        TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableForgeFaker.class).setCurrentHandler(ForgeCreator.INSTANCE.createNetHandler(connection));
        return connection;
    }
}
