package de.florianmichael.tarasande_protocol_hack

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.libs.gson.JsonArray
import com.viaversion.viaversion.libs.gson.JsonObject
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.Raytrace_1_8to1_12_2
import de.florianmichael.clampclient.injection.instrumentation_1_19_0.provider.CommandArgumentsProvider
import de.florianmichael.clampclient.injection.instrumentation_c_0_30.ClassicItemSelectionScreen
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol
import de.florianmichael.tarasande_protocol_hack.definition.ItemReleaseVersionsDefinition
import de.florianmichael.tarasande_protocol_hack.definition.PackFormatsDefinition
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.EntityDimensionsDefinition
import de.florianmichael.tarasande_protocol_hack.platform.ViaBedrockPlatformImpl
import de.florianmichael.tarasande_protocol_hack.platform.ViaBetaPlatformImpl
import de.florianmichael.tarasande_protocol_hack.platform.ViaSnapshotPlatformImpl
import de.florianmichael.tarasande_protocol_hack.platform.betacraft.SidebarEntryBetaCraftServers
import de.florianmichael.tarasande_protocol_hack.provider.clamp.FabricCommandArgumentsProvider
import de.florianmichael.tarasande_protocol_hack.provider.viabeta.*
import de.florianmichael.tarasande_protocol_hack.provider.viasnapshot.FabricPlayerAbilitiesProvider
import de.florianmichael.tarasande_protocol_hack.provider.viaversion.FabricHandItemProvider
import de.florianmichael.tarasande_protocol_hack.provider.viaversion.FabricMovementTransmitterProvider
import de.florianmichael.tarasande_protocol_hack.tarasande.information.*
import de.florianmichael.tarasande_protocol_hack.tarasande.module.ModuleEveryItemOnArmor
import de.florianmichael.tarasande_protocol_hack.tarasande.module.modifyModuleInventoryMove
import de.florianmichael.tarasande_protocol_hack.tarasande.module.modifyModuleNoWeb
import de.florianmichael.tarasande_protocol_hack.tarasande.module.modifyModuleTickBaseManipulation
import de.florianmichael.tarasande_protocol_hack.tarasande.sidebar.SidebarEntryProtocolHackValues
import de.florianmichael.tarasande_protocol_hack.tarasande.sidebar.SidebarEntrySelectionProtocolHack
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues
import de.florianmichael.tarasande_protocol_hack.util.values.ValueBooleanProtocol
import de.florianmichael.tarasande_protocol_hack.util.values.command.ViaCommandHandlerTarasandeCommandHandler
import de.florianmichael.tarasande_protocol_hack.xbox.XboxLiveSession
import de.florianmichael.viabedrock.api.BedrockProtocolAccess
import de.florianmichael.viabedrock.api.BedrockProtocols
import de.florianmichael.viabeta.api.BetaProtocolAccess
import de.florianmichael.viabeta.api.BetaProtocols
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.provider.ScreenStateProvider
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicMPPassProvider
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicWorldHeightProvider
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.provider.EncryptionProvider
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.ViaLoadingBase.ViaLoadingBaseBuilder
import de.florianmichael.vialoadingbase.api.SubPlatform
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList
import de.florianmichael.viasnapshot.api.SnapshotProtocols
import de.florianmichael.viasnapshot.protocol.protocol1_16to20w14infinite.provider.PlayerAbilitiesProvider
import io.netty.channel.DefaultEventLoop
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.tarasandedevelopment.tarasande.event.EventConnectServer
import net.tarasandedevelopment.tarasande.event.EventScreenRender
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.filesystem.ManagerFile
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import su.mandora.event.EventDispatcher
import java.net.InetSocketAddress

class TarasandeProtocolHack {

    companion object {
        // Connection data
        var viaConnection: UserConnection? = null
        var connectedAddress: InetSocketAddress? = null

        // Item splitter
        var displayItems: MutableList<Item> = ArrayList()

        fun update(protocol: ProtocolVersion, reloadProtocolHackValues: Boolean) {
            // Only reload if needed
            val comparable = InternalProtocolList.fromProtocolVersion(protocol)

            if (ViaLoadingBase.getTargetVersion() != protocol) {
                displayItems = Registries.ITEM.filter { ItemReleaseVersionsDefinition.shouldDisplay(it, comparable) }
                    .toMutableList()
                EntityDimensionsDefinition.reload(comparable)

                if (comparable.isOlderThan(BetaProtocols.a1_0_15)) {
                    ClassicItemSelectionScreen.INSTANCE.reload(comparable)
                }
            }

            ViaLoadingBase.getClassWrapper().reload(protocol)

            if (reloadProtocolHackValues) {
                ManagerValue.getValues(ProtocolHackValues).forEach {
                    if (it is ValueBooleanProtocol) {
                        it.value = it.version.any { range -> comparable in range }
                    }
                }
            }
        }
    }

    private val subPlatformViaBeta = SubPlatform("ViaBeta", { SubPlatform.isClass("de.florianmichael.viabeta.base.ViaBetaPlatform") }, { ViaBetaPlatformImpl() }) {
        it.addAll(BetaProtocols.getProtocols())
    }

    private val subPlatformViaSnapshot = SubPlatform("ViaSnapshot", { SubPlatform.isClass("de.florianmichael.viasnapshot.base.ViaSnapshotPlatform") }, { ViaSnapshotPlatformImpl() }) {
        SnapshotProtocols.addProtocols(it)
    }

    private val subPlatformViaBedrock = SubPlatform("ViaBedrock", { SubPlatform.isClass("de.florianmichael.viabedrock.base.ViaBedrockPlatform") }, { ViaBedrockPlatformImpl() }) {
        it.add(BedrockProtocols.VIA_PROTOCOL_VERSION)
    }

    fun initialize() {
        // ViaVersion loading
        ViaLoadingBaseBuilder.create().runDirectory(ManagerFile.rootDirectory)
            .nativeVersion(SharedConstants.getProtocolVersion())
            .singlePlayerProvider { MinecraftClient.getInstance() != null && mc.isInSingleplayer }
            .eventLoop(DefaultEventLoop()).dumpCreator {
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

                return@dumpCreator platformSpecific
            }.viaProviderCreator {
                // Clamp
                it.use(CommandArgumentsProvider::class.java, FabricCommandArgumentsProvider())

                // Via Beta
                it.use(GameProfileFetcher::class.java, FabricGameProfileFetcher())
                it.use(EncryptionProvider::class.java, FabricEncryptionProvider())
                it.use(ClassicWorldHeightProvider::class.java, FabricClassicWorldHeightProvider())
                it.use(OldAuthProvider::class.java, FabricOldAuthProvider())
                it.use(ClassicMPPassProvider::class.java, FabricClassicMPPassProvider())
                it.use(ScreenStateProvider::class.java, FabricScreenStateProvider())

                // Via Snapshot
                it.use(PlayerAbilitiesProvider::class.java, FabricPlayerAbilitiesProvider())

                // Via Version
                it.use(MovementTransmitterProvider::class.java, FabricMovementTransmitterProvider())
                it.use(HandItemProvider::class.java, FabricHandItemProvider())
            }.viaManagerBuilderCreator { it.commandHandler(ViaCommandHandlerTarasandeCommandHandler()) }
            .subPlatform(subPlatformViaBedrock, 0).subPlatform(subPlatformViaSnapshot).subPlatform(subPlatformViaBeta)
            .build()

        // Definition setup
        PackFormatsDefinition.checkOutdated(SharedConstants.getProtocolVersion())
        EntityDimensionsDefinition

        EventDispatcher.apply {
            add(EventSuccessfulLoad::class.java) {
                // Custom information list
                ManagerInformation.apply {
                    add(
                        // Via Version
                        InformationViaVersionProtocolVersion(),
                        InformationViaVersionProtocolsInPipeline(),

                        // Via Beta
                        InformationViaBeta1_7_6or1_7_10EntityTracker(),
                        InformationViaBeta1_7_6or1_7_10VirtualHolograms(),
                        InformationViaBeta1_5_2EntityTracker(),
                        InformationViaBeta1_2_4or1_2_5EntityTracker(),
                        InformationViaBeta1_1WorldSeed(),
                        InformationViaBetaC0_30CPE_MessageTypesExtension()
                    )
                }

                // Protocol based module modifications
                modifyModuleInventoryMove()
                modifyModuleNoWeb()
                modifyModuleTickBaseManipulation()

                // Protocol based modules
                ManagerModule.add(ModuleEveryItemOnArmor())

                // Sidebar modifications
                ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.apply {
                    insert(SidebarEntrySelectionProtocolHack(), 0)
                    insert(SidebarEntryProtocolHackValues(), 1)
                    insert(SidebarEntryBetaCraftServers(), 2)
                }

                ProtocolHackValues /* Force-Load */

                ClassicItemSelectionScreen.create(InternalProtocolList.fromProtocolVersion(BetaProtocols.c0_28toc0_30))
                val accessToken = System.getProperty("BedrockKey")
                if (accessToken != null) {
                    println("Loaded Bedrock Key, started Xbox Session: $accessToken")
                    MinecraftClient.getInstance().session = XboxLiveSession.create(accessToken)
                }
            }

            // First-time load
            add(EventSuccessfulLoad::class.java, 10000 /* after value load */) {
                println(ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.get(SidebarEntrySelectionProtocolHack::class.java).version.value.toInt())
                update(InternalProtocolList.fromProtocolId(ManagerScreenExtension.get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.get(SidebarEntrySelectionProtocolHack::class.java).version.value.toInt()), false)
            }

            // Via Connection tracker
            add(EventConnectServer::class.java) {
                viaConnection = (it.connection as IClientConnection_Protocol).protocolhack_getViaConnection()
            }

            // Custom progress bars implemented by ViaBeta
            add(EventScreenRender::class.java) {
                if (viaConnection != null && (it.screen is DownloadingTerrainScreen || it.screen is ConnectScreen)) {
                    var levelProgress: String? = null

                    if (ViaLoadingBase.getTargetVersion()
                            .isOlderThanOrEqualTo(BetaProtocols.c0_28toc0_30)
                    ) levelProgress = BetaProtocolAccess.getWorldLoading_C_0_30(viaConnection)
                    if (ViaLoadingBase.getTargetVersion().version == BedrockProtocols.VIA_PROTOCOL_VERSION.version) levelProgress =
                        BedrockProtocolAccess.getConnectionState_Bedrock_1_19_51(connectedAddress)

                    if (levelProgress != null) {
                        FontWrapper.text(
                            it.matrices,
                            levelProgress,
                            (it.screen.width / 2).toFloat(),
                            (it.screen.height / 2 - 30).toFloat(),
                            centered = true
                        )
                    }
                }
            }
        }
    }
}
