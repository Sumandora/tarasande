package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.raphimc.noteblocklib.SongParser
import net.raphimc.noteblocklib.parser.Song
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenFileChooser
import net.tarasandedevelopment.tarasande.value.ValueButton
import net.tarasandedevelopment.tarasande.value.ValueSpacer
import java.io.File

class ModuleNoteBot : Module("Note bot", "", ModuleCategory.MISC) {

    private val folder = File(TarasandeMain.get().rootDirectory, "NoteBot")
    private var song: Song? = null
    private val information = ValueSpacer(this, "Waiting for a song...")

    init {
        if (!folder.exists()) {
            folder.mkdir()
        }
        object : ValueButton(this, "Select note bot song") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenFileChooser(MinecraftClient.getInstance().currentScreen!!, folder) {
                    song = try {
                        SongParser.parseSong(it)
                    } catch (e: IllegalStateException) {
                        information.name = "Invalid file"
                        null
                    }
                    song?.also {
                        information.name = it.title + ""
                    }
                })
            }
        }
    }
}
