package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.client.network.ClientCommonNetworkHandler
import net.minecraft.client.resource.server.ServerResourcePackManager.PackEntry
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionButtonListPackScreen
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerResourcePackManager

object ResourcePackSpoofer {
    init {
        ManagerScreenExtension.add(object : ScreenExtensionButtonList<ClientCommonNetworkHandler.ConfirmServerResourcePackScreen>(ClientCommonNetworkHandler.ConfirmServerResourcePackScreen::class.java) {
            init {
                add(Button("Spoof") {
                    accept {
                        // Don't do anything with the pack
                    }
                })

                add(Button("Dump pack and spoof") {
                    accept {
                        ManagerScreenExtension.get(ScreenExtensionButtonListPackScreen::class.java).dumpServerPack(it)
                    }
                })
            }
        })
    }

    private fun accept(consumer: (PackEntry) -> Unit) {
        (mc.serverResourcePackProvider.manager as IServerResourcePackManager).tarasande_setResourcePackConsumer(consumer)
        (mc.currentScreen as ClientCommonNetworkHandler.ConfirmServerResourcePackScreen).callback.accept(true)
        mc.currentScreen?.close()

    }
}
