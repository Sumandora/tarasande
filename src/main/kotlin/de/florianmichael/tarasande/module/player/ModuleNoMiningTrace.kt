package de.florianmichael.tarasande.module.player

import de.florianmichael.tarasande.event.EventEntityRaycast
import net.minecraft.client.MinecraftClient
import net.minecraft.item.PickaxeItem
import net.minecraft.util.hit.HitResult
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleNoMiningTrace : Module("No mining trace", "Allows you to mine blocks through entities", ModuleCategory.PLAYER) {

    private val onlyWhenPickaxe = ValueBoolean(this, "Only when holding Pickaxe", true)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventEntityRaycast) {
            if (!this.onlyWhenPickaxe.value || (MinecraftClient.getInstance().player!!.mainHandStack.item is PickaxeItem || MinecraftClient.getInstance().player!!.offHandStack.item is PickaxeItem))
                if (event.hitResult != null && event.hitResult.type == HitResult.Type.BLOCK)
                    event.cancelled = true
        }
    }
}
