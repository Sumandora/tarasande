package su.mandora.tarasande_rejected_features.module

import net.minecraft.block.HoneyBlock
import net.minecraft.block.SlimeBlock
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.BlockPos
import su.mandora.tarasande.util.player.prediction.Input
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.sqrt

class ModuleDropper : Module("Dropper", "Tries to predict perfect Dropper plays", ModuleCategory.MISC /* C -> T */) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)
    private val colorSelected = ValueColor(this, "Color (selected)", 0.0, 1.0, 1.0, 1.0)

    private val movements = ArrayList<Pair<Boolean, ArrayList<Vec3d>>>()
    private var lastMovement: Vec2f? = null

    private val willSurvive = { prediction: Pair<ClientPlayerEntity, ArrayList<Vec3d>> ->
        !prediction.first.isOnGround || prediction.first.isTouchingWater || mc.world?.getBlockState(BlockPos(prediction.second.last()).add(0, -1, 0))?.let { it.block is SlimeBlock || it.block is HoneyBlock } == true
    }

    init {
        registerEvent(EventInput::class.java) { event ->
            if(event.input == mc.player?.input) {
                movements.clear()
                if(mc.player?.isOnGround == false && PredictionEngine.predictState(ticks.value.toInt(), input = event.input, abortWhen = { it.isOnGround }).first.isOnGround) {
                    val possibleInputs = ArrayList<Pair<Vec2f, Pair<ClientPlayerEntity, ArrayList<Vec3d>>>>()
                    var foundBest = false
                    for (it in PredictionEngine.allInputs) {
                        val input = Input(it.movementForward, it.movementSideways)
                        val prediction = PredictionEngine.predictState(ticks.value.toInt(), input = input, abortWhen = { it.isOnGround })
                        if(prediction.second.isNotEmpty()) {
                            possibleInputs.add(Vec2f(input.movementForward, input.movementSideways) to prediction)
                            if(willSurvive(prediction)) {
                                foundBest = true
                                movements.add(Pair(true, prediction.second))
                            } else {
                                movements.add(Pair(false, prediction.second))
                            }
                        }
                    }
                    val best =
                        if(foundBest) {
                            possibleInputs.removeAll { !willSurvive(it.second) }

                            val best = if(lastMovement != null) {
                                possibleInputs.maxBy { sqrt(Vec2f(it.first.x, it.first.y).distanceSquared(lastMovement)) }
                            } else {
                                possibleInputs.minBy { Vec2f(it.first.x, it.first.y).length() }
                            }

                            best
                        } else {
                            possibleInputs.maxBy { it.second.second.size }
                        }

                    val bestInput = best.first
                    event.movementForward = bestInput.x
                    event.movementSideways = bestInput.y
                    lastMovement = bestInput
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            for(movement in movements) {
                RenderUtil.renderPath(event.matrices, movement.second, (if(movement.first) colorSelected else color).getColor().rgb)
            }
        }
    }

}