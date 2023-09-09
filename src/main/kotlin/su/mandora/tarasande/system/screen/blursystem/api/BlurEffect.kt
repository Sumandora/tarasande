package su.mandora.tarasande.system.screen.blursystem.api

import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.system.screen.blursystem.ManagerBlur

class BlurEffect(owner: Any) {

    lateinit var selected: Blur

    val mode = object : ValueMode(owner, "Blur mode", false, *ManagerBlur.list.map { it.name }.toTypedArray()) {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
            if (!oldSelected && newSelected)
                selected = ManagerBlur.list[index]
        }
    }

    init {
        mode.select(2) // Select the fastest as default
    }

    val strength = ValueNumber(owner, "Blur strength", 1.0, 1.0, 20.0, 1.0, exceed = false)

    operator fun invoke(matrices: MatrixStack, strength: Int = this.strength.value.toInt(), targetBuffer: Framebuffer = mc.framebuffer): Framebuffer {
        return selected.render(matrices, targetBuffer, strength)
    }

}