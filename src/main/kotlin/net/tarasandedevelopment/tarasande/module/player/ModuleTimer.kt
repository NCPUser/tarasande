package net.tarasandedevelopment.tarasande.module.player

import net.tarasandedevelopment.eventsystem.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventTimeTravel
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.mixin.accessor.IRenderTickCounter
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.max

class ModuleTimer : Module("Timer", "Changes the clientside ticks per second", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Constant", "Random", "Ground")
    private val ticksPerSecond = object : ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(0) || mode.isSelected(1)
    }
    private val variation = object : ValueNumber(this, "Variation", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(1)
    }
    private val onGroundTicksPerSecond = object : ValueNumber(this, "On ground ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }
    private val offGroundTicksPerSecond = object : ValueNumber(this, "Off ground ticks per second", 1.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = mode.isSelected(2)
    }

    override fun onDisable() {
        ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / 20.0f).toFloat())
    }

    init {
        registerEvent(EventTimeTravel::class.java) {
            when {
                mode.isSelected(0) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / ticksPerSecond.value).toFloat())
                mode.isSelected(1) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / max(ticksPerSecond.value + ThreadLocalRandom.current().nextInt(-variation.value.toInt() / 2, variation.value.toInt() / 2), 1.0)).toFloat())
                mode.isSelected(2) -> ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_setTickTime((1000.0 / (if (mc.player?.isOnGround!!) onGroundTicksPerSecond.value else offGroundTicksPerSecond.value)).toFloat())
            }
        }
    }
}