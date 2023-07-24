package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.math.Direction
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.event.impl.EventStep
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.times
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.player.prediction.with

class ModuleStep : Module("Step", "Allows you to step up blocks", ModuleCategory.MOVEMENT) {

    private val stepHeight = ValueNumber(this, "Step height", 0.0, 1.0, 5.0, 0.1)
    private val mode = ValueMode(this, "Mode", false, "Vanilla", "No cheat")
    private val predictedMotionLimit = ValueNumber(this, "Predicted motion limit", 1.0, 2.0, 5.0, 1.0)

    private val slowdown = ValueNumber(this, "Slowdown", 0.0, 1.0, 1.0, 0.1)
    private val slowdownTicks = ValueNumber(this, "Slowdown ticks", 1.0, 1.0, 10.0, 1.0, isEnabled = { slowdown.value < 1.0 })
    private val onGroundTicks = ValueNumber(this, "On-ground ticks", 0.0, 0.0, 10.0, 1.0)

    private var stepTick = 0
    private var offGroundTick = 0

    private var prevAge = 0

    override fun onEnable() {
        stepTick = 0
        offGroundTick = 0
        prevAge = 0
    }

    private fun predictMotions(): List<Double> {
        val prediction = PredictionEngine.predictState(predictedMotionLimit.value.toInt(), input = mc.player?.input?.with(jumping = true))
        val motions = prediction.second.map { it.y - mc.player?.y!! }

        val neededMotions = ArrayList<Double>()
        var last: Double? = null
        for (motion in motions) {
            if (last != null) {
                if (last > 1 && motion <= 1)
                    break
            }
            neededMotions.add(motion)
            last = motion
        }
        return neededMotions
    }

    init {
        registerEvent(EventStep::class.java) { event ->
            when (event.state) {
                EventStep.State.PRE -> {
                    if (mc.player?.age!! - offGroundTick > onGroundTicks.value && mc.player?.velocity?.y!! < 0.0)
                        event.stepHeight = stepHeight.value.toFloat()
                }

                EventStep.State.POST -> {
                    if (event.stepHeight in mc.player?.stepHeight!!..stepHeight.value.toFloat() && mc.player?.isOnGround == true && mc.player?.velocity?.y!! < 0.0) {
                        if (mode.isSelected(1)) {
                            for (motion in predictMotions()) {
                                mc.networkHandler?.sendPacket(PositionAndOnGround(mc.player?.pos?.x!!, mc.player?.pos?.y?.plus(event.stepHeight * motion)!!, mc.player?.pos?.z!!, false))
                            }
                        }
                        stepTick = mc.player?.age!!
                    }
                }
            }
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (mc.player?.isOnGround == false && mc.player?.age!! - stepTick > 3) {
                    offGroundTick = mc.player?.age!!
                }
            }
        }

        registerEvent(EventMovement::class.java, 1001) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (mc.player?.age!! - stepTick <= slowdownTicks.value)
                event.velocity = (event.velocity * slowdown.value).let { if (slowdown.value != 0.0) it.withAxis(Direction.Axis.Y, event.velocity.y) else it }
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if (mc.player == null || mc.player?.age!! < prevAge) {
                    stepTick = 0
                    offGroundTick = 0
                }
                prevAge = mc.player?.age ?: return@registerEvent
            }
        }
    }

}