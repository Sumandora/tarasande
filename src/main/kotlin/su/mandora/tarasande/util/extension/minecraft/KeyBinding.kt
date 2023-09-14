package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import su.mandora.tarasande.mc

/**
 * This method ignores screens
 */
fun KeyBinding.forceIsPressed(): Boolean {
    return InputUtil.isKeyPressed(mc.window.handle, boundKey.code)
}