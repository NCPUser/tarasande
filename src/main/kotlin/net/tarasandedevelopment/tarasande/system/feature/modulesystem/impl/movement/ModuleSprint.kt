package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.impl.EventJump
import net.tarasandedevelopment.tarasande.event.impl.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    val allowBackwards = ValueBoolean(this, "Allow backwards", false, isEnabled = { !Rotations.correctMovement.isSelected(1) })

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options?.sprintKey)
                event.pressed = true
        }

        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE && allowBackwards.isEnabled() && allowBackwards.value) {
                event.yaw = PlayerUtil.getMoveDirection().toFloat()
            }
        }
    }
}
