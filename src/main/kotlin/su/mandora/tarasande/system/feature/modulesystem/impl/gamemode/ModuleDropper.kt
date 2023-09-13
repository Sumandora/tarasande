package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_TPS
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.prediction.Input
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.sqrt

class ModuleDropper : Module("Dropper", "Tries to predict perfect Dropper plays", ModuleCategory.GAMEMODE) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, DEFAULT_TPS.toDouble(), 100.0, 1.0)
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)
    private val colorSelected = ValueColor(this, "Color (selected)", 0.0, 1.0, 1.0, 1.0)

    private val preferredLandingBlocks = object : ValueRegistry<Block>(this, "Preferred landing blocks", Registries.BLOCK, true, Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK, Blocks.HAY_BLOCK) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, net.minecraft.util.math.BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    private val preferredSubmergedBlocks = object : ValueRegistry<Block>(this, "Preferred submerged blocks", Registries.BLOCK, true, Blocks.WATER) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, net.minecraft.util.math.BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    private val preferInAirFluids = ValueBoolean(this, "Prefer in-air fluids", true)
    private val onlyWhenStandingStill = ValueBoolean(this, "Only when standing still", true)
    private val lockRotation = ValueBoolean(this, "Lock rotation", true)

    private val movements = ArrayList<Pair<Boolean, ArrayList<Vec3d>>>()
    private var lastMovement: Vec2f? = null

    private val willSurvive = { prediction: Pair<ClientPlayerEntity, ArrayList<Vec3d>> ->
        val submergedPos = mc.world?.getBlockState(BlockPos(prediction.second.last()))
        val landPos = mc.world?.getBlockState(BlockPos(prediction.second.last()).add(0, -1, 0))

        !prediction.first.isOnGround ||
                preferredSubmergedBlocks.any { it == submergedPos?.block } ||
                preferredLandingBlocks.any { it == landPos?.block } ||
                (preferInAirFluids.value && prediction.second.any { mc.world?.getBlockState(BlockPos(it))?.block is FluidBlock })
    }

    private var targetPos: Vec3d? = null
    private var rotation: Rotation? = null

    override fun onEnable() {
        targetPos = mc.player?.pos
    }

    override fun onDisable() {
        targetPos = null
    }

    init {
        registerEvent(EventJump::class.java) { event ->
            if (event.state == EventJump.State.PRE)
                targetPos = mc.player?.pos
        }
        registerEvent(EventInput::class.java) { event ->
            if (event.input == mc.player?.input) {
                movements.clear()
                if (mc.player?.isOnGround == false && PredictionEngine.predictState(ticks.value.toInt(), input = event.input, abortWhen = { it.isOnGround }).first.isOnGround) {
                    val possibleInputs = ArrayList<Pair<Vec2f, Pair<ClientPlayerEntity, ArrayList<Vec3d>>>>()
                    var foundBest = false
                    for (it in PredictionEngine.allInputs) {
                        val input = Input(it.movementForward, it.movementSideways)
                        val prediction = PredictionEngine.predictState(ticks.value.toInt(), input = input, abortWhen = { it.isOnGround })
                        if (prediction.second.isNotEmpty()) {
                            possibleInputs.add(Vec2f(input.movementForward, input.movementSideways) to prediction)
                            if (willSurvive(prediction)) {
                                foundBest = true
                                movements.add(Pair(true, prediction.second))
                            } else {
                                movements.add(Pair(false, prediction.second))
                            }
                        }
                    }
                    if(!foundBest)
                        if(onlyWhenStandingStill.value && PlayerUtil.isPlayerMoving())
                            return@registerEvent

                    val best =
                        if (foundBest) {
                            possibleInputs.removeAll { !willSurvive(it.second) }

                            val best = if (lastMovement != null) {
                                possibleInputs.minBy { sqrt(Vec2f(it.first.x, it.first.y).distanceSquared(lastMovement)) }
                            } else {
                                possibleInputs.maxBy { (Vec2f(it.first.x, it.first.y) - Vec2f(1F, 0F)).length() }
                            }

                            best
                        } else {
                            val longest = possibleInputs.maxBy { it.second.second.size }
                            val neutral = possibleInputs.firstOrNull { it.first.lengthSquared() == 0F }
                            if (neutral?.second?.second?.size == longest.second.second.size)
                                neutral
                            else
                                possibleInputs
                                    .filter { it.second.second.size == longest.second.second.size }
                                    .minBy { (it.second.first.pos - (targetPos ?: mc.player?.pos!!)).length() }
                        }

                    val bestInput = best.first
                    event.input.setMovementForward(bestInput.x)
                    event.input.setMovementForward(bestInput.y)
                    lastMovement = bestInput
                }
            }
        }

        registerEvent(EventRotation::class.java) { event ->
            if(lockRotation.value && movements.any { it.first } && rotation != null) {
                event.rotation = rotation!!
            } else {
                rotation = event.rotation
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            for (movement in movements) {
                RenderUtil.renderPath(event.matrices, movement.second, (if (movement.first) colorSelected else color).getColor().rgb)
            }
        }
    }

}