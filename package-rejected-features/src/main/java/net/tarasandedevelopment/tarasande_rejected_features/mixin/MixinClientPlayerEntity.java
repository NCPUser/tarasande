package net.tarasandedevelopment.tarasande_rejected_features.mixin;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande_rejected_features.multiplayerfeature.MultiplayerFeatureNoSignatures;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
    public void hookNoSignatures(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
        if (TarasandeMain.Companion.managerMultiplayerFeature().get(MultiplayerFeatureNoSignatures.class).getState().getValue()) {
            cir.cancel();
        }
    }

    @Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
    public void hookNoSignatures(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (TarasandeMain.Companion.managerMultiplayerFeature().get(MultiplayerFeatureNoSignatures.class).getState().getValue()) {
            cir.cancel();
        }
    }
}
