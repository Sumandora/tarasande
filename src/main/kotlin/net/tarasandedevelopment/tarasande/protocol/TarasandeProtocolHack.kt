package net.tarasandedevelopment.tarasande.protocol

import com.viaversion.viaversion.api.platform.providers.ViaProviders
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.commands.ViaCommandHandler
import com.viaversion.viaversion.libs.gson.JsonArray
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider
import de.florianmichael.vialegacy.ViaLegacy
import de.florianmichael.vialegacy.api.via.config.ViaLegacyConfigImpl
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.viaprotocolhack.INativeProvider
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.Person
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocol.command.TarasandeCommandHandler
import net.tarasandedevelopment.tarasande.protocol.platform.ViaLegacyTarasandePlatform
import net.tarasandedevelopment.tarasande.protocol.provider.FabricHandItemProvider
import net.tarasandedevelopment.tarasande.protocol.provider.FabricMovementTransmitterProvider
import net.tarasandedevelopment.tarasande.protocol.provider.FabricVersionProvider
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.io.File
import java.util.*
import java.util.logging.Logger

class TarasandeProtocolHack : INativeProvider {

    private val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getGameVersion().protocolVersion.toDouble(), Double.MAX_VALUE, 1.0, true)
    private val auto = ValueBoolean(this, "Auto", false)
    private val compression = arrayOf("decompress", "compress")

    var realClientsideVersion = this.clientsideVersion()

    val viaLegacy = ViaLegacyTarasandePlatform(this)

    init {
        ViaProtocolHack.instance().init(this) {
            this.createChannelMappings()
            val config = ViaLegacyConfigImpl(File(ViaProtocolHack.instance().directory(), "vialegacy.yml"))
            config.reloadConfig()
            ViaLegacy.init(viaLegacy, config, Logger.getLogger("ViaLegacy-Tarasande"))
        }
    }

    private fun createChannelMappings() {
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|HS"] = "fml:hs"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|MP"] = "fml:mp"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML"] = "minecraft:fml"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FORGE"] = "minecraft:forge"
    }

    override fun isSinglePlayer(): Boolean {
        return MinecraftClient.getInstance().isInSingleplayer
    }

    fun setVersion(version: Int) {
        this.version.value = version.toDouble()
    }

    override fun clientsideVersion(): Int {
        return this.version.value.toInt()
    }

    override fun realClientsideVersion(): Int {
        return this.realClientsideVersion
    }

    override fun nettyOrder(): Array<String> {
        return this.compression
    }

    override fun run(): File {
        return TarasandeMain.get().rootDirectory
    }

    override fun createDump(): JsonObject {
        val platformSpecific = JsonObject()
        val mods = JsonArray()

        FabricLoader.getInstance().allMods.stream().map { mod: ModContainer ->
            val jsonMod = JsonObject()
            jsonMod.addProperty("id", mod.metadata.id)
            jsonMod.addProperty("name", mod.metadata.name)
            jsonMod.addProperty("version", mod.metadata.version.friendlyString)
            val authors = JsonArray()
            mod.metadata.authors.stream().map { it: Person ->
                val info = JsonObject()
                val contact = JsonObject()
                it.contact.asMap().forEach { (property: String?, value: String?) -> contact.addProperty(property, value) }
                if (contact.size() != 0) {
                    info.add("contact", contact)
                }
                info.addProperty("name", it.name)
                info
            }.forEach { element: JsonObject? -> authors.add(element) }
            jsonMod.add("authors", authors)
            jsonMod
        }.forEach { element: JsonObject? -> mods.add(element) }

        platformSpecific.add("mods", mods)
        platformSpecific.addProperty("native version", SharedConstants.getGameVersion().protocolVersion)

        return platformSpecific
    }

    override fun createProviders(providers: ViaProviders?) {
        providers?.register(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
        providers?.register(VersionProvider::class.java, FabricVersionProvider())
        providers?.register(HandItemProvider::class.java, FabricHandItemProvider())
    }

    override fun commandHandler(): Optional<ViaCommandHandler> {
        return Optional.of(TarasandeCommandHandler())
    }

    override fun optionalVersions(): MutableList<ProtocolVersion> = LegacyProtocolVersion.PROTOCOL_VERSIONS

    fun isAuto() = this.auto.value

    fun toggleAuto() {
        this.auto.value = !this.auto.value
    }
}
