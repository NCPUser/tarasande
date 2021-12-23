package su.mandora.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.base.value.Value

open class ValueText(owner: Any, name: String, var value: String) : Value(owner, name) {
	override fun save(): JsonElement {
		return JsonPrimitive(value)
	}

	override fun load(jsonElement: JsonElement) {
		value = jsonElement.asString
	}
}
