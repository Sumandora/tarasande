package net.tarasandedevelopment.tarasande_protocol_hack

import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.platform.providers.ViaProviders
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.api.protocol.version.VersionProvider
import com.viaversion.viaversion.libs.gson.JsonArray
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol
import de.florianmichael.clampclient.injection.mixininterface.IFontStorage_Protocol
import de.florianmichael.vialegacy.ViaLegacy
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.EncryptionProvider
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.UUIDProvider
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
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelSelection
import net.tarasandedevelopment.tarasande_protocol_hack.command.ViaCommandHandlerTarasandeCommandHandler
import net.tarasandedevelopment.tarasande_protocol_hack.event.EventSkipIdlePacket
import net.tarasandedevelopment.tarasande_protocol_hack.extension.andOlder
import net.tarasandedevelopment.tarasande_protocol_hack.extension.getSpecialName
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.CommandArgumentsProvider
import net.tarasandedevelopment.tarasande_protocol_hack.fix.global.EntityDimensionReplacement
import net.tarasandedevelopment.tarasande_protocol_hack.fix.global.PackFormats
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ValueBooleanProtocol
import net.tarasandedevelopment.tarasande_protocol_hack.provider.clamp.FabricCommandArgumentsProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy.FabricEncryptionProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy.FabricOldAuthProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.vialegacy.FabricUUIDProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricHandItemProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricMovementTransmitterProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricVersionProvider
import net.tarasandedevelopment.tarasande_protocol_hack.util.formatRange
import su.mandora.event.EventDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadFactory
import java.util.logging.Logger

class TarasandeProtocolHack : INativeProvider {

    val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getProtocolVersion().toDouble(), Double.MAX_VALUE, 1.0, true)
    private val compression = arrayOf("decompress", "compress")

    companion object {
        lateinit var cancelOpenPacket: ValueBoolean
        var viaConnection: UserConnection? = null
    }

    fun initialize() {
        ViaProtocolHack.instance().init(this) {
            ViaLegacy.init(Logger.getLogger("ViaLegacy-tarasande"))
        }
        PackFormats.checkOutdated(nativeVersion())

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                TarasandeMain.managerInformation().apply {
                    add(object : Information("Via Version", "Protocol Version") {

                        var version: ProtocolVersion? = null

                        init {
                            EventDispatcher.apply {
                                add(EventConnectServer::class.java) {
                                    version = VersionList.PROTOCOLS.firstOrNull { it.version == ViaProtocolHack.instance().provider().clientsideVersion } ?: ProtocolVersion.unknown
                                }
                                add(EventDisconnect::class.java) { event ->
                                    if(event.connection == MinecraftClient.getInstance().networkHandler?.connection)
                                        version = null
                                }
                            }
                        }

                        override fun getMessage() = version?.name
                    })

                    add(object : Information("Via Version", "Via Pipeline") {
                        override fun getMessage(): String? {
                            val names = (MinecraftClient.getInstance().networkHandler?.connection as? IClientConnection_Protocol)?.protocolhack_getViaConnection()?.protocolInfo?.pipeline?.pipes()?.map { p -> p.javaClass.simpleName } ?: return null
                            if (names.isEmpty()) return null
                            return "\n" + names.subList(0, names.size - 1).joinToString("\n")
                        }
                    })
                }

                TarasandeMain.managerModule().apply {
                    cancelOpenPacket = object : ValueBoolean(get(ModuleInventoryMove::class.java), "Cancel open packet (" + ProtocolVersion.v1_11_1.andOlder() + ")", false) {
                        override fun isEnabled() = VersionList.isOlderOrEqualTo(ProtocolVersion.v1_11_1)
                    }

                    get(ModuleTickBaseManipulation::class.java).apply {
                        val chargeOnIdlePacketSkip = object : ValueBoolean(this, "Charge on idle packet skip (" + formatRange(*ProtocolHackValues.sendIdlePacket.version) + ")", false) {
                            override fun isEnabled() = ProtocolHackValues.sendIdlePacket.isEnabled()
                        }

                        registerEvent(EventSkipIdlePacket::class.java) {
                            if (chargeOnIdlePacketSkip.isEnabled() && chargeOnIdlePacketSkip.value)
                                shifted += mc.renderTickCounter.tickTime.toLong()
                        }
                    }
                }

                ProtocolHackValues /* Force-Load */
            }

            add(EventSuccessfulLoad::class.java, 10000 /* after value load */) {
                update(ProtocolVersion.getProtocol(version.value.toInt()), false)

                TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                    insert(object : EntrySidebarPanelSelection("Protocol Hack", "Protocol Hack", VersionList.PROTOCOLS.map { it.getSpecialName() }, ProtocolVersion.getProtocol(targetVersion()).getSpecialName()) {
                        override fun onClick(newValue: String) {
                            val newProtocol = VersionList.PROTOCOLS.first { it.getSpecialName() == newValue }.version.toDouble()
                            if (version.value != newProtocol) {
                                version.value = newProtocol
                                update(ProtocolVersion.getProtocol(version.value.toInt()))
                            }
                        }
                    }, 0)

                    insert(object : EntrySidebarPanel("Protocol Hack Values", "Protocol Hack") {
                        override fun onClick(mouseButton: Int) {
                            MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, name, ProtocolHackValues))
                        }
                    }, 1)
                }
            }

            add(EventConnectServer::class.java) {
                viaConnection = (it.connection as IClientConnection_Protocol).protocolhack_getViaConnection()
            }
        }
    }

    fun update(protocol: ProtocolVersion, reloadProtocolHackValues: Boolean = true) {
        System.setProperty("tarasande-target-version", protocol.version.toString()) // this provides the current clientside version to all other packages

        if (reloadProtocolHackValues) {
            TarasandeMain.managerValue().getValues(ProtocolHackValues).forEach {
                if (it is ValueBooleanProtocol)
                    it.value = it.version.any { range -> protocol in range }
            }
        }

        if (!FabricLoader.getInstance().isModLoaded("dashloader")) {
            MinecraftClient.getInstance().fontManager.fontStorages.values.forEach {
                (it as IFontStorage_Protocol).protocolhack_clearCaches()
            }
        }

        EntityDimensionReplacement.reloadDimensions()
    }

    override fun isSinglePlayer() = MinecraftClient.getInstance()?.isInSingleplayer != false
    override fun nativeVersion() = SharedConstants.getProtocolVersion()
    override fun targetVersion() = this.version.value.toInt()
    override fun nettyOrder() = this.compression
    override fun run() = TarasandeMain.get().rootDirectory

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
        // Clamp Fixes
        providers?.use(CommandArgumentsProvider::class.java, FabricCommandArgumentsProvider())

        // Via Legacy
        providers?.use(OldAuthProvider::class.java, FabricOldAuthProvider())
        providers?.use(UUIDProvider::class.java, FabricUUIDProvider())
        providers?.use(EncryptionProvider::class.java, FabricEncryptionProvider())

        // Via Version
        providers?.use(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
        providers?.use(VersionProvider::class.java, FabricVersionProvider())
        providers?.use(HandItemProvider::class.java, FabricHandItemProvider())
    }

    override fun onBuildViaPlatform(builder: ViaManagerImpl.ViaManagerBuilder) {
        builder.commandHandler(ViaCommandHandlerTarasandeCommandHandler())
    }

    override fun getOptionalProtocols(): MutableList<ProtocolVersion> = LegacyProtocolVersion.PROTOCOL_VERSIONS
}
