package de.florianmichael.tarasande_protocol_hack.fix

import com.viaversion.viaversion.api.minecraft.Position
import com.viaversion.viaversion.libs.gson.JsonElement
import java.util.concurrent.CopyOnWriteArrayList

class Sign_1_8(val line1: JsonElement, val line2: JsonElement, val line3: JsonElement, val line4: JsonElement, val position: Position) {

    companion object {
        val signs = CopyOnWriteArrayList<Sign_1_8>()
    }
}
