package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.tarasandedevelopment.tarasande.event.EventTimeTravel
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max

class ModuleTimer : Module("Timer", "Changes the clientside ticks per second", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Constant", "Random", "Ground")
    private val ticksPerSecond = ValueNumber(this, "Ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(0) || mode.isSelected(1) })
    private val variation = ValueNumber(this, "Variation", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(1) })
    private val onGroundTicksPerSecond = ValueNumber(this, "On ground ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(2) })
    private val offGroundTicksPerSecond = ValueNumber(this, "Off ground ticks per second", 1.0, 20.0, 100.0, 1.0, isEnabled = { mode.isSelected(2) })

    override fun onDisable() {
        mc.renderTickCounter.tickTime = (1000.0 / 20.0F).toFloat()
    }

    init {
        registerEvent(EventTimeTravel::class.java) {
            when {
                mode.isSelected(0) -> mc.renderTickCounter.tickTime = (1000.0 / ticksPerSecond.value).toFloat()
                mode.isSelected(1) -> mc.renderTickCounter.tickTime = (1000.0 / max(ticksPerSecond.value + ThreadLocalRandom.current().nextInt(-variation.value.toInt() / 2, variation.value.toInt() / 2), 1.0)).toFloat()
                mode.isSelected(2) -> mc.renderTickCounter.tickTime = (1000.0 / (if (mc.player?.isOnGround!!) onGroundTicksPerSecond.value else offGroundTicksPerSecond.value)).toFloat()
            }
        }
    }
}