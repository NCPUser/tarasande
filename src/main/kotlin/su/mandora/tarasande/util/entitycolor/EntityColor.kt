package su.mandora.tarasande.util.entitycolor

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import java.awt.Color

class EntityColor {

	private val selfColor = ValueColor(TarasandeMain.get().clientValues!!, "Self Color", 0.0f, 1.0f, 1.0f, 1.0f)
	private val useTeamColor = ValueBoolean(TarasandeMain.get().clientValues!!, "Use Team Color", true)
	private val playerColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Player Color", 0.0f, 1.0f, 1.0f, 1.0f) {
		override fun isVisible() = !useTeamColor.value
	}
	private val animalColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Animal Color", 0.0f, 1.0f, 1.0f, 1.0f) {
		override fun isVisible() = !useTeamColor.value
	}
	private val mobColor = object : ValueColor(TarasandeMain.get().clientValues!!, "Mob Color", 0.0f, 1.0f, 1.0f, 1.0f) {
		override fun isVisible() = !useTeamColor.value
	}

	fun getColor(entity: Entity): Color {
		if (entity == MinecraftClient.getInstance().player)
			return selfColor.getColor()

		if (!useTeamColor.value) {
			if (entity is PlayerEntity)
				return playerColor.getColor()

			if (entity is AnimalEntity)
				return animalColor.getColor()

			if (entity is Monster)
				return mobColor.getColor()
		}

		// can't call method because it's overwritten in mixin -> infinite recursion
		val abstractTeam = entity.scoreboardTeam
		return if (abstractTeam != null && abstractTeam.color.colorValue != null) Color(abstractTeam.color.colorValue!!) else Color(16777215)
	}

}