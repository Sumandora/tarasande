package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.event.impl.EventVelocityMultiplier
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTerrainSpeed : Module("Terrain speed", "Makes you move faster on certain terrain", ModuleCategory.MOVEMENT) {

    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val velocityMultiplier = ValueNumber(this, "Velocity multiplier", 0.0, 1.0, 2.0, 0.1)

    init {
        registerEvent(EventVelocityMultiplier::class.java) { event ->
            if (blocks.isSelected(event.block))
                event.velocityMultiplier = velocityMultiplier.value
        }
    }

}