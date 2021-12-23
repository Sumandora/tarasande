package su.mandora.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.value.Value
import su.mandora.tarasande.module.movement.ModuleInventoryMove

open class ValueKeyBind(owner: Any, name: String, var keyBind: Int) : Value(owner, name) {

	override fun save(): JsonElement {
		return JsonPrimitive(keyBind)
	}

	override fun load(jsonElement: JsonElement) {
		keyBind = jsonElement.asInt
	}

	open fun filter(keyBind: Int): Boolean {
		return true
	}

	fun isPressed(): Boolean {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window?.handle!!, keyBind) && TarasandeMain.get().managerModule?.get(ModuleInventoryMove::class.java)?.isPassingEvents()!!
	}
}