package net.tarasandedevelopment.tarasande.module.ghost

import net.minecraft.client.option.KeyBinding
import net.minecraft.util.hit.HitResult
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mixin.accessor.IKeyBinding
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import java.util.function.Consumer

class ModuleAutoClicker : Module("Auto clicker", "Automatically clicks for you", ModuleCategory.GHOST) {

    private val keyMap = mapOf<KeyBinding, String>(
        Pair(mc.options.attackKey, "Attack"),
        Pair(mc.options.useKey, "Use")
    )

    private val buttons: ValueMode = ValueMode(this, "Buttons", true, *keyMap.map { it.value }.toTypedArray())

    private val hashMap = HashMap<KeyBinding, ClickSpeedUtil>()

    init {
        for (pair in keyMap)
            hashMap[pair.key] = ClickSpeedUtil(this, { buttons.selected.contains(pair.value) })
    }

    override fun onEnable() {
        hashMap.forEach { (_, u) -> u.reset() }
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventAttack -> {
                if (event.dirty)
                    return@Consumer

                for (entry in hashMap) {
                    if (buttons.selected.contains(keyMap[entry.key]) && (entry.key as IKeyBinding).tarasande_forceIsPressed()) {
                        val clicks = entry.value.getClicks()
                        for (i in 1..clicks) {
                            (entry.key as IKeyBinding).tarasande_increaseTimesPressed()
                            event.dirty = true
                        }
                    } else {
                        entry.value.reset()
                    }
                }
            }

            is EventKeyBindingIsPressed -> {
                for (entry in hashMap) {
                    if (buttons.selected.contains(keyMap[entry.key]) && event.keyBinding == entry.key) {
                        event.pressed = if (entry.key == mc.options.attackKey) event.pressed && mc.crosshairTarget?.type == HitResult.Type.BLOCK else false
                    }
                }
            }
        }
    }

}