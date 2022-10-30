package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion.viaprotocolhack;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaConfig;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;

@Mixin(value = CustomViaConfig.class, remap = false)
public abstract class MixinCustomViaConfig extends AbstractViaConfig {

    protected MixinCustomViaConfig(File configFile) {
        super(configFile);
    }

    @Override
    public boolean isLeftHandedHandling() {
        return false;
    }

    @Override
    public boolean isShieldBlocking() {
        return false;
    }
}
