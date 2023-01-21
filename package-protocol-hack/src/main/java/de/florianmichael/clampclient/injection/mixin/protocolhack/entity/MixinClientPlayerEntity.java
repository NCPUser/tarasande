/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import com.mojang.authlib.GameProfile;
import de.florianmichael.clampclient.injection.instrumentation_1_8.ArmorDefinition_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.LegacyConstants_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.PlayerAndLivingEntityMovementEmulation_1_8;
import de.florianmichael.clampclient.injection.mixininterface.IClientPlayerEntity_Protocol;
import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSprint;
import de.florianmichael.tarasande_protocol_hack.tarasande.EventSkipIdlePacket;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity_Protocol {

    @Shadow
    public Input input;
    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    private boolean autoJumpEnabled;
    @Shadow
    @Final
    protected MinecraftClient client;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private int ticksSinceLastPositionPacketSent;
    @Shadow
    private double lastX;
    @Shadow
    private double lastBaseY;
    @Shadow
    private double lastZ;
    @Unique
    private boolean protocolhack_areSwingCanceledThisTick = false;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected abstract boolean isCamera();

    @Shadow protected abstract void sendSprintingPacket();

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public boolean fixMovement(ClientPlayerEntity instance) {
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.getYaw() - this.lastYaw;
            double h = this.getPitch() - this.lastPitch;
            if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }
            double n = MathHelper.square(2.05E-4);
            if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_18_2)) {
                n = 9.0E-4D;
            }
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > n || this.ticksSinceLastPositionPacketSent >= 20;
            bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround || ProtocolHackValues.INSTANCE.getSendIdlePacket().getValue()) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            } else {
                EventDispatcher.INSTANCE.call(new EventSkipIdlePacket());
            }
            if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
                ++this.ticksSinceLastPositionPacketSent;
            }

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
        return false;
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && protocolhack_areSwingCanceledThisTick) {
            ci.cancel();
        }

        protocolhack_areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_4)) {
            if (this.input.sneaking) {
                this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
                this.input.movementForward = (float) ((double) this.input.movementForward / 0.3D);
            }
        }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_1)) {
            return false;
        } else {
            return _this.isSwimming();
        }
    }

    @Redirect(method = "isWalking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean easierUnderwaterSprinting(ClientPlayerEntity instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_1)) {
            return false;
        }
        return instance.isSubmergedInWater();
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_1)) {
            ModuleSprint moduleSprint = ManagerModule.INSTANCE.get(ModuleSprint.class);
            if (moduleSprint.getEnabled().getValue() && moduleSprint.getAllowBackwards().isEnabled() && moduleSprint.getAllowBackwards().getValue())
                return input.getMovementInput().lengthSquared() >= 0.8 * 0.8;

            return input.movementForward >= 0.8F;
        }

        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2)) {
            return false; // Disable all water related movement
        }

        return self.isTouchingWater();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    public void removeSprintingPacket(ClientPlayerEntity instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(VersionListEnum.r1_19_3)) {
            sendSprintingPacket();
        }
    }

    @Redirect(method = "canSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    public boolean removeNewCheck(ClientPlayerEntity instance) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && instance == MinecraftClient.getInstance().player) {
            return false;
        }
        return instance.hasVehicle();
    }

    // I-EEE 754
    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void fixRoundingConvention(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && (Object) this == MinecraftClient.getInstance().player) {
            cir.setReturnValue(this.input.movementForward >= 0.8F);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;noClip:Z"))
    public boolean canNoClipBeGood(ClientPlayerEntity instance) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && (Object) this == MinecraftClient.getInstance().player) {
            final PlayerAndLivingEntityMovementEmulation_1_8 a18PlayerAndLivingEntityMovementEmulation = ((ILivingEntity_Protocol) this).protocolhack_getPlayerLivingEntityMovementWrapper();

            a18PlayerAndLivingEntityMovementEmulation.pushOutOfBlocks(this.getPos().x - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D, this.getBoundingBox().minY + 0.5D, this.getPos().z + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D);
            a18PlayerAndLivingEntityMovementEmulation.pushOutOfBlocks(this.getPos().x - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D, this.getBoundingBox().minY + 0.5D, this.getPos().z - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D);
            a18PlayerAndLivingEntityMovementEmulation.pushOutOfBlocks(this.getPos().x + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D, this.getBoundingBox().minY + 0.5D, this.getPos().z - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D);
            a18PlayerAndLivingEntityMovementEmulation.pushOutOfBlocks(this.getPos().x + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D, this.getBoundingBox().minY + 0.5D, this.getPos().z + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 0.35D);
            return true;
        }
        return instance.noClip;
    }

    @Override
    public boolean shouldSwimInFluids() {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && (Object) this == MinecraftClient.getInstance().player) {
            return false;
        }

        return super.shouldSwimInFluids();
    }

    @Override
    public int getArmor() {
        if (ProtocolHackValues.INSTANCE.getEmulateArmorHud().getValue()) {
            return ArmorDefinition_1_8.sum();
        }
        return super.getArmor();
    }

    @Override
    public void protocolhack_cancelSwingOnce() {
        protocolhack_areSwingCanceledThisTick = true;
    }
}
