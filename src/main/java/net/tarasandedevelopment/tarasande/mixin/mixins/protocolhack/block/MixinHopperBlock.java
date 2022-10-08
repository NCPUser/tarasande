package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public class MixinHopperBlock {

    @Unique
    private final static VoxelShape INSIDE_SHAPE_1122 = Block.createCuboidShape(2, 10, 2, 14, 16, 14);

    @Unique
    private final static VoxelShape HOPPER_SHAPE_1122 = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            INSIDE_SHAPE_1122,
            BooleanBiFunction.ONLY_FIRST);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            cir.setReturnValue(HOPPER_SHAPE_1122);
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void injectGetRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            cir.setReturnValue(INSIDE_SHAPE_1122);
    }
}
