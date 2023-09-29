package su.mandora.tarasande.feature.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

open class ScreenBetter(title: String, var prevScreen: Screen?) : Screen(Text.of(title)) {
    override fun close() {
        client!!.setScreen(prevScreen)
    }
}
