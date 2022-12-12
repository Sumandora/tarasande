package net.tarasandedevelopment.tarasande_litematica.util

import fi.dy.masa.litematica.data.SchematicHolder
import fi.dy.masa.litematica.schematic.LitematicaSchematic
import fi.dy.masa.litematica.schematic.container.LitematicaBlockStateContainer
import fi.dy.masa.litematica.selection.AreaSelection
import fi.dy.masa.litematica.selection.Box
import net.minecraft.util.math.BlockPos

val start = BlockPos(0, 0, 0)

object LitematicaGenerator {

    fun create(name: String, dimension: BlockPos, blockProvider: (container: LitematicaBlockStateContainer) -> Unit) {
        val areaSelection = AreaSelection()
        areaSelection.addSubRegionBox(Box(start, dimension, "Main"), false)
        val schematic = LitematicaSchematic.createEmptySchematic(areaSelection, "tarasande and EnZaXD")
        schematic.getSubRegionContainer("Main")?.apply {
            blockProvider.invoke(this)
        }
        schematic.metadata.name = name
        SchematicHolder.getInstance().addSchematic(schematic, true)
    }
}
