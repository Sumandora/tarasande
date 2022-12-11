package net.tarasandedevelopment.tarasande_litematica.generator

import de.evilcodez.mazes.generator.MazeGeneratorType
import de.evilcodez.mazes.utils.DimensionUtils
import de.evilcodez.mazes.utils.TileExporter
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande_litematica.util.LitematicaGenerator
import java.util.Random

val random = Random()

class GeneratorMazes(parent: Any) : Generator(parent, "Mazes") {

    private val algorithm = ValueMode(parent, "Algorithm", false, *MazeGeneratorType.values().map { it.getName() }.toTypedArray())
    private val width = ValueNumber(parent, "Width", 3.0, 20.0, 100.0, 10.0)
    private val length = ValueNumber(parent, "Length", 3.0, 20.0, 100.0, 10.0)
    private val height = ValueNumber(parent, "Height", 1.0, 20.0, 50.0, 1.0)
    private val scale = ValueNumber(parent, "Scale", 1.0, 1.0, 10.0, 1.0)
    private val groundBlocks = object : ValueRegistry<Block>(parent, "Ground blocks", Registries.BLOCK) {
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val wallBlocks = object : ValueRegistry<Block>(parent, "Wall blocks", Registries.BLOCK) {
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    override fun perform() {
        (if (algorithm.anySelected()) MazeGeneratorType.byName(algorithm.selected[0]) else MazeGeneratorType.BACKTRACKING).factory.get().apply {
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
                        if (wallBlocks.list.isNotEmpty()) {
                            if (exportedGrid[x][z]) {
                                for (h in 0 .. height.value.toInt()) {
                                    it.set(x, h, z, wallBlocks.list[random.nextInt(wallBlocks.list.size)].defaultState)
                                }
                            }
                        }

                        if (groundBlocks.list.isNotEmpty()) {
                            it.set(x, 0, z, groundBlocks.list[random.nextInt(groundBlocks.list.size)].defaultState) // ground
                        }
                    }
                }

                finish()
            }
        }
    }
}
