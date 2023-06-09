package su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy

import com.google.gson.JsonObject
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload

class LegacyForgePayload(json: JsonObject) : IForgePayload {

    private val mods = ArrayList<ModStruct>()

    init {
        val type = if (json.has("type") && json.get("type").isJsonPrimitive) json.get("type").asString else null

        if (type != null && type.equals("FML", true)) {
            val list = json.get("modList").asJsonArray
            list.forEach {
                val obj = it.asJsonObject

                mods.add(ModStruct(obj.get("modid").asString, obj.get("version").asString))
            }
        }
    }

    override fun installedMods() = this.mods
}
