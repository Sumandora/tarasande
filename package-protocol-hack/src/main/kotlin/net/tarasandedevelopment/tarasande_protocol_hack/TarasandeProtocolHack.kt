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
import de.florianmichael.viabeta.ViaBeta
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicMPPassProvider
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicWorldHeightProvider
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtMessageTypesStorage
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.provider.EncryptionProvider
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher
import de.florianmichael.viacursed.protocol.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider
import de.florianmichael.vialoadingbase.NativeProvider
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import io.netty.channel.DefaultEventLoop
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventScreenRender
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleTickBaseManipulation
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.screen.impl.ScreenBetterOwnerValues
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanel
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.EntrySidebarPanelSelection
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande_protocol_hack.event.EventSkipIdlePacket
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.CommandArgumentsProvider
import net.tarasandedevelopment.tarasande_protocol_hack.fix.global.EntityDimensionReplacement
import net.tarasandedevelopment.tarasande_protocol_hack.fix.global.PackFormats
import net.tarasandedevelopment.tarasande_protocol_hack.module.ModuleEveryItemOnArmor
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ViaBetaPlatformImpl
import net.tarasandedevelopment.tarasande_protocol_hack.platform.ViaCursedPlatformImpl
import net.tarasandedevelopment.tarasande_protocol_hack.provider.clamp.FabricCommandArgumentsProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viabeta.*
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viacursed.FabricPlayerAbilitiesProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricHandItemProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricMovementTransmitterProvider
import net.tarasandedevelopment.tarasande_protocol_hack.provider.viaversion.FabricVersionProvider
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.andOlder
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ValueBooleanProtocol
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.command.ViaCommandHandlerTarasandeCommandHandler
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.formatRange
import su.mandora.event.EventDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadFactory

class TarasandeProtocolHack : NativeProvider {

    val version = ValueNumber(this, "Protocol", Double.MIN_VALUE, SharedConstants.getProtocolVersion().toDouble(), Double.MAX_VALUE, 1.0, true)
    private val compression = arrayOf("decompress", "compress")

    companion object {
        lateinit var cancelOpenPacket: ValueBoolean
        var viaConnection: UserConnection? = null
    }

    fun initialize() {
        ViaLoadingBase.instance().init(this) {
            ViaLoadingBase.loadSubPlatform("ViaBeta") {
                val isBetaLoaded = ViaLoadingBase.hasClass("de.florianmichael.viabeta.base.ViaBetaPlatform")
                if (isBetaLoaded) ViaBetaPlatformImpl()
                return@loadSubPlatform isBetaLoaded
            }
            ViaLoadingBase.loadSubPlatform("ViaCursed") {
                val isCursedLoaded = ViaLoadingBase.hasClass("de.florianmichael.viacursed.base.ViaCursedPlatform")
                if (isCursedLoaded) ViaCursedPlatformImpl()
                return@loadSubPlatform isCursedLoaded
            }
        }
        PackFormats.checkOutdated(nativeVersion().originalVersion)

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                TarasandeMain.managerInformation().apply {
                    add(object : Information("Via Version", "Protocol Version") {

                        var version: ProtocolVersion? = null

                        init {
                            EventDispatcher.apply {
                                add(EventConnectServer::class.java) {
                                    version = VersionListEnum.RENDER_VERSIONS.map { it.protocol }.firstOrNull { it.version == ViaLoadingBase.getTargetVersion().originalVersion } ?: ProtocolVersion.unknown
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
                    add(object : Information("Via Beta", VersionListEnum.r1_7_6tor1_7_10.getName() + " Entity Tracker") {
                        override fun getMessage() = ViaBeta.getTrackedEntities1_7_6_10(viaConnection)
                    })
                    add(object : Information("Via Beta", VersionListEnum.r1_7_6tor1_7_10.getName() + " Virtual Holograms") {
                        override fun getMessage() = ViaBeta.getVirtualHolograms1_7_6_10(viaConnection)
                    })
                    add(object : Information("Via Beta", VersionListEnum.r1_5_2.getName() + " Entity Tracker") {
                        override fun getMessage() = ViaBeta.getTrackedEntities1_5_2(viaConnection)
                    })
                    add(object : Information("Via Beta", VersionListEnum.r1_2_4tor1_2_5.getName() + " Entity Tracker") {
                        override fun getMessage() = ViaBeta.getTrackedEntities1_2_4_5(viaConnection)
                    })
                    add(object : Information("Via Beta", VersionListEnum.r1_1.getName() + " World Seed") {
                        override fun getMessage() = ViaBeta.getWorldSeed1_1(viaConnection)
                    })
                    add(object : Information("Via Beta", VersionListEnum.c0_30cpe.getName() + " Message Types Extension") {
                        override fun getMessage(): String? {
                            if (viaConnection == null) return null
                            val messageTypeStorage = viaConnection!!.get(ExtMessageTypesStorage::class.java) ?: return null
                            val list = messageTypeStorage.asDisplayList
                            if (list.isEmpty()) return null
                            return list.joinToString("\n")
                        }
                    })
                }

                TarasandeMain.managerModule().apply {
                    cancelOpenPacket = object : ValueBoolean(get(ModuleInventoryMove::class.java), "Cancel open packet (" + VersionListEnum.r1_11_1to1_11_2.andOlder() + ")", false) {
                        override fun isEnabled() = ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_11_1to1_11_2)
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

                    add(ModuleEveryItemOnArmor())
                }

                ProtocolHackValues /* Force-Load */
            }

            add(EventSuccessfulLoad::class.java, 10000 /* after value load */) {
                update(VersionListEnum.fromProtocolId(version.value.toInt()), false)

                TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                    insert(object : EntrySidebarPanelSelection("Protocol Hack", "Protocol Hack", VersionListEnum.RENDER_VERSIONS.map { it.getName() }, ViaLoadingBase.getTargetVersion().getName()) {
                        override fun onClick(newValue: String) {
                            val newProtocol = VersionListEnum.RENDER_VERSIONS.first { it.getName() == newValue }.version.toDouble()
                            if (version.value != newProtocol) {
                                version.value = newProtocol
                                update(VersionListEnum.fromProtocolId(version.value.toInt()), ProtocolHackValues.autoChangeValuesDependentOnVersion.value)
                            }
                        }
                    }, 0)

                    insert(object : EntrySidebarPanel("Protocol Hack Values", "Protocol Hack") {
                        override fun onClick(mouseButton: Int) {
                            MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, name, ProtocolHackValues))
                        }
                    }, 1)
                }

                TarasandeMain.managerScreenExtension().add(object : ScreenExtensionButtonList<GameMenuScreen>(GameMenuScreen::class.java) {
                    init {
                        "Protocol Hack Values".apply {
                            add(this, direction = Direction.RIGHT) {
                                MinecraftClient.getInstance().setScreen(ScreenBetterOwnerValues(MinecraftClient.getInstance().currentScreen!!, this, ProtocolHackValues))
                            }
                        }
                    }
                })
            }

            add(EventConnectServer::class.java) {
                viaConnection = (it.connection as IClientConnection_Protocol).protocolhack_getViaConnection()
            }

            add(EventScreenRender::class.java) {
                if (viaConnection != null && (it.screen is DownloadingTerrainScreen || it.screen is ConnectScreen)) {
                    val levelProgress = ViaBeta.getWorldLoading_C_0_30(viaConnection)
                    if (levelProgress != null) {
                        FontWrapper.text(it.matrices, levelProgress, (it.screen.width / 2).toFloat(), (it.screen.height / 2 - 30).toFloat(), centered = true)
                    }
                }
            }
        }
    }

    fun update(protocol: VersionListEnum, reloadProtocolHackValues: Boolean) {
        ViaLoadingBase.instance().switchVersionTo(protocol.originalVersion)

        if (reloadProtocolHackValues) {
            TarasandeMain.managerValue().getValues(ProtocolHackValues).forEach {
                if (it is ValueBooleanProtocol)
                    it.value = it.version.any { range -> protocol in range }
            }
        }

        EntityDimensionReplacement.reloadDimensions()
    }

    override fun isSinglePlayer() = MinecraftClient.getInstance()?.isInSingleplayer != false
    override fun nativeVersion() = VersionListEnum.r1_19_3
    override fun nettyOrder() = this.compression
    override fun run() = TarasandeMain.get().rootDirectory

    override fun createDump(): JsonObject {
        val platformSpecific = JsonObject()
        val mods = JsonArray()

        FabricLoader.getInstance().allMods.forEach { mod ->
            val jsonMod = JsonObject()
            jsonMod.addProperty("id", mod.metadata.id)
            jsonMod.addProperty("name", mod.metadata.name)
            jsonMod.addProperty("version", mod.metadata.version.friendlyString)
            val authors = JsonArray()
            mod.metadata.authors.stream().map {
                val info = JsonObject()
                val contact = JsonObject()
                it.contact.asMap().forEach { (property, value) -> contact.addProperty(property, value) }
                if (contact.size() != 0) {
                    info.add("contact", contact)
                }
                info.addProperty("name", it.name)
                info
            }.forEach { element: JsonObject? -> authors.add(element) }
            jsonMod.add("authors", authors)
            mods.add(jsonMod)
        }

        platformSpecific.add("mods", mods)
        platformSpecific.addProperty("native version", SharedConstants.getGameVersion().protocolVersion)

        return platformSpecific
    }

    override fun eventLoop(threadFactory: ThreadFactory?, executorService: ExecutorService?) = DefaultEventLoop(executorService)

    override fun createProviders(providers: ViaProviders?) {
        // Clamp Fixes
        providers?.use(CommandArgumentsProvider::class.java, FabricCommandArgumentsProvider())

        // Via Beta
        providers?.use(GameProfileFetcher::class.java, FabricGameProfileFetcher())
        providers?.use(EncryptionProvider::class.java, FabricEncryptionProvider())
        providers?.use(ClassicWorldHeightProvider::class.java, FabricClassicWorldHeightProvider())
        providers?.use(OldAuthProvider::class.java, FabricOldAuthProvider())
        providers?.use(ClassicMPPassProvider::class.java, FabricClassicMPPassProvider())

        // Via Cursed
        providers?.use(PlayerAbilitiesProvider::class.java, FabricPlayerAbilitiesProvider())

        // Via Version
        providers?.use(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
        providers?.use(VersionProvider::class.java, FabricVersionProvider())
        providers?.use(HandItemProvider::class.java, FabricHandItemProvider())
    }

    override fun createViaPlatform(builder: ViaManagerImpl.ViaManagerBuilder) {
        builder.commandHandler(ViaCommandHandlerTarasandeCommandHandler())
    }
}
