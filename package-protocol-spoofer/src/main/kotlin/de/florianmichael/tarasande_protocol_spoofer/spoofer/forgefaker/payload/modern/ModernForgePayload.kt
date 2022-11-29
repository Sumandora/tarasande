package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.modern

import com.google.gson.JsonObject
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.legacy.ModStruct

class ModernForgePayload(json: JsonObject) : IForgePayload {

    private val mods = ArrayList<ModStruct>()
    val channels = ArrayList<ChannelStruct>()
    val fmlNetworkVersion: Int

    init {
        json.get("mods").asJsonArray.forEach {
            val obj = it.asJsonObject
            mods.add(ModStruct(obj.get("modId").asString, obj.get("modmarker").asString))
        }

        json.get("channels").asJsonArray.forEach {
            val obj = it.asJsonObject
            channels.add(ChannelStruct(obj.get("res").asString, obj.get("version").asString, obj.get("required").asBoolean))
        }

        this.fmlNetworkVersion = json.get("fmlNetworkVersion").asInt
    }

    override fun installedMods() = this.mods
}
