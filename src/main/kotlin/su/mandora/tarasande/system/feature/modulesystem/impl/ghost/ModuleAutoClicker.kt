package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.client.option.KeyBinding
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.feature.clickmethodsystem.api.ClickSpeedUtil
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleAutoClicker : Module("Auto clicker", "Automatically clicks for you", ModuleCategory.GHOST) {

    private val keyMap = mapOf<KeyBinding, String>(
        Pair(mc.options.attackKey, "Attack"),
        Pair(mc.options.useKey, "Use")
    )

    private val buttons: ValueMode = ValueMode(this, "Buttons", true, *keyMap.map { it.value }.toTypedArray())

    private val hashMap = HashMap<KeyBinding, ClickSpeedUtil>()

    init {
        for (pair in keyMap)
            hashMap[pair.key] = ClickSpeedUtil(this, { buttons.isSelected(pair.value) }, namePrefix = pair.value + ": ")
    }

    override fun onEnable() {
        hashMap.forEach { (_, u) -> u.reset() }
    }

    init {
        registerEvent(EventAttack::class.java) { event ->
            if (event.dirty)
                return@registerEvent

            for (entry in hashMap) {
                if (buttons.isSelected(keyMap[entry.key]!!) && entry.key.pressed) {
                    val clicks = entry.value.getClicks()
                    if (clicks > 0) {
                        when (entry.key) {
                            mc.options.attackKey ->
                                if (mc.interactionManager?.isBreakingBlock == true)
                                    return@registerEvent

                            mc.options.useKey ->
                                if (mc.player?.isUsingItem == true || PlayerUtil.getUsedHand() != null)
                                    return@registerEvent
                        }
                        event.dirty = true
                        if (entry.key.timesPressed == 0)
                            entry.key.timesPressed = clicks
                    }
                } else {
                    entry.value.reset()
                }
            }
        }
    }
}
