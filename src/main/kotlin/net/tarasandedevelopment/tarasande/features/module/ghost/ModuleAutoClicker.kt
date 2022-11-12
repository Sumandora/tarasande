package net.tarasandedevelopment.tarasande.features.module.ghost

import net.minecraft.client.option.KeyBinding
import net.minecraft.util.hit.HitResult
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.value.impl.ValueMode

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

    init {
        registerEvent(EventAttack::class.java) { event ->
            if (event.dirty)
                return@registerEvent

            for (entry in hashMap) {
                if (buttons.selected.contains(keyMap[entry.key]) && entry.key.pressed) {
                    val clicks = entry.value.getClicks()
                    if (entry.key == mc.options.useKey && mc.player?.isUsingItem == true)
                        return@registerEvent
                    for (i in 1..clicks) {
                        entry.key.timesPressed++
                        event.dirty = true
                    }
                } else {
                    entry.value.reset()
                }
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            for (entry in hashMap) {
                if (buttons.selected.contains(keyMap[entry.key]) && event.keyBinding == entry.key) {
                    event.pressed =
                        when (entry.key) {
                            mc.options.attackKey -> event.pressed && mc.crosshairTarget?.type == HitResult.Type.BLOCK
                            mc.options.useKey -> event.pressed || (mc.player?.isUsingItem == true && event.keyBinding.pressed)
                            else -> false
                        }
                }
            }
        }
    }
}
