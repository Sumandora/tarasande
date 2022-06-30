package su.mandora.tarasande.module.misc

import com.mojang.authlib.minecraft.UserApiService
import net.minecraft.client.util.ProfileKeys
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import java.util.*
import java.util.function.Consumer

class ModuleNoSign : Module("No sign", "Prevents chat message signing", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTick) {
            if (mc.profileKeys != null) {
                (mc as IMinecraftClient).tarasande_setProfileKeys(ProfileKeys(UserApiService.OFFLINE, UUID.randomUUID(), mc.runDirectory.toPath()))
            }
        }
    }

    override fun onDisable() {
        val accessor = mc as IMinecraftClient
        accessor.tarasande_setProfileKeys(ProfileKeys(accessor.tarasande_getUserApiService(), mc.session?.profile?.id, mc.runDirectory.toPath()))
    }

}