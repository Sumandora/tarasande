package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleGhostHand : Module("Ghost hand", "Allows interaction to happen through walls", ModuleCategory.PLAYER) {

    val mode = ValueMode(this, "Mode", true, "Blocks", "Entities")
    val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, isEnabled = { mode.isSelected(0) }) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty // TODO not very correct, but should work for now
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val entityMode = ValueMode(this, "Entity mode", false, "Through blocks", "Through entities", isEnabled = { mode.isSelected(1) })

    fun getBlockDistance(): Double {
        return when {
            entityMode.isSelected(0) -> Double.MAX_VALUE
            entityMode.isSelected(1) -> Double.MIN_VALUE
            else -> 0.0
        }
    }

    init {
        mode.select(0)
    }

}
