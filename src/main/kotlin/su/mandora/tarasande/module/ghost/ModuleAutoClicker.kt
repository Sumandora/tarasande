package su.mandora.tarasande.module.ghost

import net.minecraft.util.hit.HitResult
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttack
import su.mandora.tarasande.event.EventHandleBlockBreaking
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.player.clickspeed.ClickSpeedUtil
import java.util.function.Consumer

class ModuleAutoClicker : Module("Auto clicker", "Automatically clicks for you", ModuleCategory.GHOST) {

    private val clickSpeedUtil = ClickSpeedUtil(this, { true })

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    private var clicked = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventAttack -> {
                clicked = false
                if((mc.options.attackKey as IKeyBinding).tarasande_forceIsPressed()) {
                    val clicks = clickSpeedUtil.getClicks()
                    if(clicks > 0)
                        clicked = true
                    for(i in 1..clicks)
                        (mc as IMinecraftClient).tarasande_invokeDoAttack()
                } else {
                    clickSpeedUtil.reset()
                }
            }
            is EventHandleBlockBreaking -> {
                event.parameter = event.parameter && mc.crosshairTarget?.type == HitResult.Type.BLOCK
            }
        }
    }

}