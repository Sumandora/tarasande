package su.mandora.tarasande_protocol_spoofer.tarasandevalues

import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.resource.ResourcePackProfile
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtensionButtonList
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionButtonListPackScreen
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IConfirmScreen
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerResourcePackProvider

object ResourcePackSpoofer {
    init {
        ManagerScreenExtension.add(object : ScreenExtensionButtonList<ConfirmScreen>(ConfirmScreen::class.java) {
            init {
                add(Button("Spoof", { (mc.currentScreen as IConfirmScreen).tarasande_isResourcePacksScreen() }) {
                    accept {
                        // Don't do anything with the pack
                    }
                })

                add(Button("Dump pack and spoof", { (mc.currentScreen as IConfirmScreen).tarasande_isResourcePacksScreen() }) {
                    accept {
                        ManagerScreenExtension.get(ScreenExtensionButtonListPackScreen::class.java).dumpServerPack(it)
                    }
                })
            }
        })
    }

    private fun accept(consumer: (ResourcePackProfile) -> Unit) {
        (mc.serverResourcePackProvider as IServerResourcePackProvider).tarasande_setResourcePackConsumer(consumer)
        (mc.currentScreen as ConfirmScreen).callback.accept(true)
        mc.currentScreen?.close()

    }
}
