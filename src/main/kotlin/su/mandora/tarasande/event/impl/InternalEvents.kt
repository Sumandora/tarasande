package su.mandora.tarasande.event.impl

import net.minecraft.entity.Entity
import net.minecraft.text.Text
import su.mandora.tarasande.event.Event
import su.mandora.tarasande.system.feature.modulesystem.Module
import java.awt.Color

class EventIsEntityAttackable(val entity: Entity, var attackable: Boolean) : Event(false)
class EventTagName(var entity: Entity, var displayName: Text) : Event(false)
class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)
class EventSuccessfulLoad : Event(false)
class EventModuleStateSwitched(val module: Module, val oldState: Boolean, val newState: Boolean) : Event(false)