package net.tarasandedevelopment.tarasande.protocolhack

import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viaversion.api.platform.providers.ViaProviders
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.libs.gson.JsonArray
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider
import de.florianmichael.vialegacy.ViaLegacy
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.PreNettyProvider
import de.florianmichael.viaprotocolhack.INativeProvider
import de.florianmichael.viaprotocolhack.ViaProtocolHack
import de.florianmichael.viaprotocolhack.util.VersionList
import io.netty.channel.DefaultEventLoop
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.Person
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IFontStorage_Protocol
import net.tarasandedevelopment.tarasande.protocolhack.command.TarasandeCommandHandler
import net.tarasandedevelopment.tarasande.protocolhack.fix.EntityDimensionReplacement
import net.tarasandedevelopment.tarasande.protocolhack.fix.PackFormats
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.protocolhack.platform.ValueBooleanProtocol
import net.tarasandedevelopment.tarasande.protocolhack.platform.multiplayerfeature.MultiplayerFeatureProtocolHack
import net.tarasandedevelopment.tarasande.protocolhack.platform.multiplayerfeature.MultiplayerFeatureProtocolHackSettings
import net.tarasandedevelopment.tarasande.protocolhack.provider.viaversion.FabricHandItemProvider
import net.tarasandedevelopment.tarasande.protocolhack.provider.viaversion.FabricMovementTransmitterProvider
import net.tarasandedevelopment.tarasande.protocolhack.provider.viaversion.FabricVersionProvider
import net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy.FabricOldAuthProvider
import net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy.FabricPreNettyProvider
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.Information
import su.mandora.event.EventDispatcher
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadFactory
import java.util.logging.Logger

class TarasandeProtocolHack(private val rootDirectory: File) : INativeProvider {

    val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getProtocolVersion().toDouble(), Double.MAX_VALUE, 1.0, true)
    private val compression = arrayOf("decompress", "compress")

    init {
        ViaProtocolHack.instance().init(this) {
            this.createChannelMappings()
            ViaLegacy.init(Logger.getLogger("ViaLegacy-Tarasande"))
        }

        PackFormats.checkOutdated(nativeVersion())

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java, 10000) {
                update(ProtocolVersion.getProtocol(version.value.toInt()))
                // Protocol Hack
                TarasandeMain.managerMultiplayerFeature().apply {
                    insert(MultiplayerFeatureProtocolHack(), 0)
                    insert(MultiplayerFeatureProtocolHackSettings(), 1)
                }
            }
        }

        TarasandeMain.managerInformation().apply {
            add(object : Information("Via Version", "Protocol Version") {
                override fun getMessage() = VersionList.getProtocols().firstOrNull { it.version == ViaProtocolHack.instance().provider().clientsideVersion }?.name
            })

            add(object : Information("Via Version", "Via Pipeline") {
                override fun getMessage(): String? {
                    val names = (MinecraftClient.getInstance().networkHandler?.connection as? IClientConnection_Protocol)?.protocolhack_getViaConnection()?.protocolInfo?.pipeline?.pipes()?.map { p -> p.javaClass.simpleName } ?: return null
                    if (names.isEmpty()) return null
                    return "\n" + names.subList(0, names.size - 1).joinToString("\n")
                }
            })
        }

    }

    fun update(protocol: ProtocolVersion) {
        // Owners may change, orientate on one setting
        TarasandeMain.managerValue().getValues(ProtocolHackValues.viaVersionDebug.owner).forEach {
            if (it is ValueBooleanProtocol)
                it.value = it.version.any { range -> protocol in range }
        }

        if (!FabricLoader.getInstance().isModLoaded("dashloader")) {
            MinecraftClient.getInstance().fontManager.fontStorages.values.forEach {
                (it as IFontStorage_Protocol).protocolhack_clearCaches()
            }
        }

        EntityDimensionReplacement.reloadDimensions()
    }

    private fun createChannelMappings() {
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|HS"] = "fml:hs"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|MP"] = "fml:mp"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML"] = "minecraft:fml"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FORGE"] = "minecraft:forge"
    }

    override fun isSinglePlayer() = MinecraftClient.getInstance().isInSingleplayer
    override fun nativeVersion() = SharedConstants.getProtocolVersion()
    override fun targetVersion() = this.version.value.toInt()
    override fun nettyOrder() = this.compression
    override fun run() = rootDirectory

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
        }.forEach { mods.add(it) }

        platformSpecific.add("mods", mods)
        platformSpecific.addProperty("native version", SharedConstants.getGameVersion().protocolVersion)

        return platformSpecific
    }

    override fun eventLoop(threadFactory: ThreadFactory?, executorService: ExecutorService?) = DefaultEventLoop(executorService)

    override fun createProviders(providers: ViaProviders?) {
        // Via Legacy
        providers?.use(OldAuthProvider::class.java, FabricOldAuthProvider())
        providers?.use(PreNettyProvider::class.java, FabricPreNettyProvider())

        // Via Version
        providers?.use(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
        providers?.use(VersionProvider::class.java, FabricVersionProvider())
        providers?.use(HandItemProvider::class.java, FabricHandItemProvider())
    }

    override fun onBuildViaPlatform(builder: ViaManagerImpl.ViaManagerBuilder) {
        builder.commandHandler(TarasandeCommandHandler())
    }

    override fun getOptionalProtocols(): MutableList<ProtocolVersion> = LegacyProtocolVersion.PROTOCOL_VERSIONS
}
