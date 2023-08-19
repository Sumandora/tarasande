package su.mandora.tarasande_example.examples.modules

import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.math.rotation.Rotation
import kotlin.math.sin

class AnotherModule : Module("Another module", "Also my dearest module", "My category") {
    init {
        registerEvent(EventRotation::class.java) {
            it.rotation = Rotation(sin(mc.player!!.age / 10F) * 180F, 90F)
        }
    }
}