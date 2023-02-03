package de.florianmichael.clampclient.injection.mixin.protocolhack.item;

import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void removeModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (ProtocolHackValues.INSTANCE.getReplacePetrifiedOakSlab().getValue() && world != null /* world is null in gui rendering */ && stack.isOf(Items.PETRIFIED_OAK_SLAB)) {
            cir.setReturnValue(this.models.getModelManager().getMissingModel());
        }
    }
}
