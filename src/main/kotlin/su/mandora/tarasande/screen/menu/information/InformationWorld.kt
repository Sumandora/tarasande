package su.mandora.tarasande.screen.menu.information

import com.google.common.collect.Iterables
import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.base.screen.menu.information.Information

class InformationEntities : Information("World", "Entities") {
	override fun getMessage(): String? {
		if (MinecraftClient.getInstance().world == null)
			return null
		return Iterables.size(MinecraftClient.getInstance().world?.entities!!).toString()
	}
}

class InformationWorldTime : Information("World", "World Time") {
	override fun getMessage(): String? {
		if (MinecraftClient.getInstance().world == null)
			return null
		return MinecraftClient.getInstance().world?.timeOfDay.toString() + "/" + MinecraftClient.getInstance().world?.time
	}
}