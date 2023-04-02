package su.mandora.tarasande_litematica.util

import fi.dy.masa.litematica.data.SchematicHolder
import fi.dy.masa.litematica.schematic.LitematicaSchematic
import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer
import fi.dy.masa.litematica.selection.AreaSelection
import fi.dy.masa.litematica.selection.Box
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.TARASANDE_NAME

object LitematicaGenerator {

    fun create(name: String, dimension: BlockPos, blockProvider: (container: LitematicaBlockStateContainer) -> Unit) {
        val areaSelection = AreaSelection()
        areaSelection.addSubRegionBox(Box(BlockPos.ORIGIN, dimension, "Main"), false)
        val schematic = LitematicaSchematic.createEmptySchematic(areaSelection, TARASANDE_NAME)
        schematic.getSubRegionContainer("Main")?.apply {
            blockProvider.invoke(this)
        }
        schematic.metadata.name = name
        SchematicHolder.getInstance().addSchematic(schematic, true)
    }
}
