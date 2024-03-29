package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Blocks
import net.minecraft.util.shape.VoxelShapes
import su.mandora.tarasande.event.impl.EventCollisionShape
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAntiCactus : Module("Anti cactus", "Prevents you from taking damage from cacti", ModuleCategory.PLAYER) {

    init {
        registerEvent(EventCollisionShape::class.java) { event ->
            if(mc.world?.getBlockState(event.pos)?.block == Blocks.CACTUS)
                event.collisionShape = VoxelShapes.fullCube()
        }
    }
}