package su.mandora.tarasande.util.clientvalue

import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.value.*

class ClientValues {

	val menuHotkey = object : ValueKeyBind(this, "Menu hotkey", GLFW.GLFW_KEY_RIGHT_SHIFT) {
		override fun filter(keyBind: Int) = keyBind != GLFW.GLFW_KEY_UNKNOWN
	}
	val accentColor = ValueColor(this, "Accent color", 0.6f, 1.0f, 1.0f, -1.0f)
	val targets = ValueMode(this, "Targets", true, "Players", "Animals", "Mobs", "Other")
	val correctMovement = ValueMode(this, "Correct movement", false, "Off", "Prevent Backwards Sprinting", "Direct", "Silent")
	val blurStrength = object : ValueNumber(this, "Blur strength", 1.0, 1.0, 20.0, 1.0) {
		override fun onChange() {
			TarasandeMain.get().blur?.kawasePasses = null
		}
	}

	override fun toString(): String {
		return "ClientValues"
	}
}