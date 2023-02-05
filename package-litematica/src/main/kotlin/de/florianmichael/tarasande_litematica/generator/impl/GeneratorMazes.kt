package de.florianmichael.tarasande_litematica.generator.impl

import de.evilcodez.mazes.generator.MazeGeneratorType
import de.evilcodez.mazes.utils.DimensionUtils
import de.evilcodez.mazes.utils.TileExporter
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import de.florianmichael.tarasande_litematica.generator.Generator
import de.florianmichael.tarasande_litematica.util.LitematicaGenerator
import java.util.*

val random = Random()

class GeneratorMazes : Generator("Mazes") {

    private val algorithm = ValueMode(this, "Algorithm", false, *MazeGeneratorType.values().map { it.getName() }.toTypedArray())
    private val width = ValueNumber(this, "Width", 3.0, 20.0, 100.0, 10.0)
    private val length = ValueNumber(this, "Length", 3.0, 20.0, 100.0, 10.0)
    private val height = ValueNumber(this, "Height", 1.0, 20.0, 50.0, 1.0)
    private val scale = ValueNumber(this, "Scale", 1.0, 1.0, 10.0, 1.0)
    private val groundBlocks = object : ValueRegistry<Block>(this, "Ground blocks", Registries.BLOCK, true) {
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val wallBlocks = object : ValueRegistry<Block>(this, "Wall blocks", Registries.BLOCK, true) {
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    override fun perform() {
        (if (algorithm.anySelected()) MazeGeneratorType.byName(algorithm.getSelected()) else MazeGeneratorType.BACKTRACKING).factory.get().apply {
            val mcDimension = DimensionUtils.toMinecraftDimension(width.value.toInt(), length.value.toInt(), scale.value.toInt())
            if (mcDimension[0] < 1 || mcDimension[1] < 1) {
                mcDimension[0] = 1
                mcDimension[1] = 1
                return
            }
            val exportedGrid = TileExporter.export2D(generate(mcDimension[0], mcDimension[1]), scale.value.toInt())
            val dimension = BlockPos(exportedGrid.size, height.value.toInt(), exportedGrid[0].size)

            LitematicaGenerator.create(name, dimension) { // x and z
                for (x in 0 until dimension.x) {
                    for (z in 0 until dimension.z) {
                        if (wallBlocks.anySelected()) {
                            if (exportedGrid[x][z]) {
                                for (h in 0 .. height.value.toInt()) {
                                    it.set(x, h, z, wallBlocks.randomOrNull()!!.defaultState)
                                }
                            }
                        }

                        if (groundBlocks.anySelected()) {
                            it.set(x, 0, z, groundBlocks.randomOrNull()!!.defaultState) // ground
                        }
                    }
                }

                finish()
            }
        }
    }
}
