package net.tarasandedevelopment.tarasande.module.movement

import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.ValueComponentRegistry
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.ValueComponentText
import net.tarasandedevelopment.tarasande.screen.cheatmenu.valuecomponent.ValueComponentTextList
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean

class ModuleInventoryMove : Module("Inventory move", "Allows you to move while in inventory", ModuleCategory.MOVEMENT) {

    val cancelOpenPacket = object : ValueBoolean(this, "Cancel open packets", false) {
        override fun isEnabled() = VersionList.isOlderOrEqualTo(VersionList.R1_11_1)
    }
    val cancelClosePacket = ValueBoolean(this, "Cancel close packets", false)
    private val updateSneaking = ValueBoolean(this, "Update sneaking", false)

    private val movementKeys = ArrayList(PlayerUtil.movementKeys)

    private var textBoxFocused = false

    init {
        movementKeys.add(mc.options.jumpKey)
        movementKeys.add(mc.options.sneakKey)
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND) {
                if (cancelClosePacket.value && event.packet is CloseHandledScreenC2SPacket && event.packet.syncId == 0)
                    event.cancelled = true
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (isPassingEvents())
                if (movementKeys.contains(event.keyBinding))
                    if (event.keyBinding != mc.options.sneakKey || updateSneaking.value)
                        event.pressed = InputUtil.isKeyPressed(mc.window?.handle!!, event.keyBinding.boundKey.code)
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.POST)
                textBoxFocused = isTextboxFocused()
        }
    }

    private fun isTextboxFocused(): Boolean {
        return TarasandeMain.get().screenCheatMenu.managerValueComponent.instances.any {
            when (it) {
                is ValueComponentText -> it.isFocused()
                is ValueComponentRegistry -> it.isFocused()
                is ValueComponentTextList -> it.isFocused()
                else -> false
            }
        }
    }

    private fun isPassingEvents(): Boolean {
        return (enabled && (mc.currentScreen is HandledScreen<*> || (mc.currentScreen is ScreenCheatMenu && !textBoxFocused))) || (mc.currentScreen == null || mc.currentScreen?.passEvents == true)
    }
}
