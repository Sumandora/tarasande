package su.mandora.tarasande.event.impl

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.event.Event

class EventBlockChange(val pos: BlockPos, val state: BlockState) : Event(false)