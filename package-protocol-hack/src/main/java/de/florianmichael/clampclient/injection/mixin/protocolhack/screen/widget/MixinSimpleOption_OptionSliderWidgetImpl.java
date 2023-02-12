package de.florianmichael.clampclient.injection.mixin.protocolhack.screen.widget;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.mouse.SensitivityCalculations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnnecessaryUnboxing")
@Mixin(SimpleOption.OptionSliderWidgetImpl.class)
public abstract class MixinSimpleOption_OptionSliderWidgetImpl extends OptionSliderWidget {

    @Shadow @Final private SimpleOption<?> option;

    protected MixinSimpleOption_OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, double value) {
        super(options, x, y, width, height, value);
    }

    @Inject(method = "updateMessage", at = @At("RETURN"))
    public void injectUpdateMessage(CallbackInfo ci) {
        if (this.option == MinecraftClient.getInstance().options.getMouseSensitivity()) {
            float approximation = SensitivityCalculations.get1_12SensitivityFor1_19(((Double) this.option.value).doubleValue());
            final Text customText = Text.literal(" (" + ProtocolVersion.v1_12_2.getName() + ": " + SensitivityCalculations.getPercentage(approximation) + "%)").styled(style -> style.withColor(TarasandeValues.INSTANCE.getAccentColor().getColor().getRGB()));

            this.setMessage(Text.literal("").append(this.getMessage()).append(customText));
        }
    }
}