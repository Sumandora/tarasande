package net.tarasandedevelopment.tarasande.util.screen

import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.tarasandedevelopment.tarasande.mc

object ScreenUtil {

    fun playClickSound() {
        mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

}