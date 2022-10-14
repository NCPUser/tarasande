package net.tarasandedevelopment.tarasande.util.player.entitycolor

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventEntityColor
import net.tarasandedevelopment.tarasande.module.render.ModuleESP
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueColor
import java.awt.Color

class EntityColor(val moduleESP: ModuleESP) {

    private val modifyTeamColor = ValueBoolean(this, "Modify team color", true)
    private val selfColor = object : ValueColor(this, "Self Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val friendsColor = object : ValueColor(this, "Friends Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val useTeamColor = object : ValueBoolean(this, "Use Team Color", true) {
        override fun isEnabled() = modifyTeamColor.value
    }
    private val playerColor = object : ValueColor(this, "Player Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }
    private val animalColor = object : ValueColor(this, "Animal Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }
    private val mobColor = object : ValueColor(this, "Mob Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = modifyTeamColor.value && !useTeamColor.value
    }

    fun getColor(entity: Entity): Color? {
        if (!moduleESP.enabled || !modifyTeamColor.value)
            return null
        var color: Color? = null

        if (!useTeamColor.value) {
            if (entity is PlayerEntity) color = playerColor.getColor()

            if (entity is AnimalEntity) color = animalColor.getColor()

            if (entity is Monster) color = mobColor.getColor()
        }

        if (entity == MinecraftClient.getInstance().player) color = selfColor.getColor()
        else if (entity is PlayerEntity && TarasandeMain.get().friends.isFriend(entity.gameProfile)) color = friendsColor.getColor()

        val eventEntityColor = EventEntityColor(entity, color)
        TarasandeMain.get().eventDispatcher.call(eventEntityColor)
        return eventEntityColor.color
    }

}