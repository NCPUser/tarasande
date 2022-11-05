package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndPortalBlock.class)
public abstract class MixinEndPortalBlock extends BlockWithEntity {

    @Shadow
    @Final
    protected static VoxelShape SHAPE;
    @Unique
    private final VoxelShape protocolhack_SHAPE_1_8 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @Unique
    private final VoxelShape protocolhack_SHAPE_1_16_5 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    protected MixinEndPortalBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (MinecraftClient.getInstance().world == null) return;

        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8))
            cir.setReturnValue(protocolhack_SHAPE_1_8);
        else if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_16_4))
            cir.setReturnValue(protocolhack_SHAPE_1_16_5);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
