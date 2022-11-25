package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.event.EventCollisionShape
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAntiCactus : Module("Anti cactus", "Prevents you from taking damage from cacti", ModuleCategory.PLAYER) {

    init {
        registerEvent(EventCollisionShape::class.java) {
            if (MinecraftClient.getInstance().world?.getBlockState(it.pos)?.block == Blocks.CACTUS) {
                it.collisionShape = VoxelShapes.fullCube()
            }
        }
    }
}