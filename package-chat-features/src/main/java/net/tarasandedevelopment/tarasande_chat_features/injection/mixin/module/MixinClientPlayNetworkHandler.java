package net.tarasandedevelopment.tarasande_chat_features.injection.mixin.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.message.SignedMessage;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande_chat_features.TarasandeChatFeatures;
import net.tarasandedevelopment.tarasande_chat_features.module.ModuleNoChatContext;
import net.tarasandedevelopment.tarasande_chat_features.viaversion.ViaVersionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @ModifyVariable(method = "acknowledge", at = @At("HEAD"), argsOnly = true, index = 2)
    public boolean hookNoChatContext(boolean value, SignedMessage message) {
        if (ManagerModule.INSTANCE.get(ModuleNoChatContext.class).getEnabled()) {
            if (TarasandeChatFeatures.Companion.getTarasandeProtocolHackLoaded()) {
                if (ViaVersionUtil.INSTANCE.isSimpleSignatures()) {
                    return false;
                }
            }

            assert MinecraftClient.getInstance().player != null;
            return message.getSender().equals(MinecraftClient.getInstance().player.getUuid());
        }

        return value;
    }
}
