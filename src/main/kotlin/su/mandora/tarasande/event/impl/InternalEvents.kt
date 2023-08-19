package su.mandora.tarasande.event.impl

import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.Event
import su.mandora.tarasande.system.feature.modulesystem.Module
import java.awt.Color

class EventIsEntityAttackable(val entity: Entity, var attackable: Boolean) : Event(false)
class EventTagName(var entity: Entity, var displayName: Text) : Event(false)
class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)
class EventSuccessfulLoad : Event(false)
class EventModuleStateSwitched(val module: Module, val oldState: Boolean, val newState: Boolean) : Event(false)
class EventKillAuraAimPoint(val entity: Entity, var aimPoint: Vec3d, val box: Box, val state: State) : Event(false) {
    enum class State {
        PRE, PRE_RAND, POST
    }
}