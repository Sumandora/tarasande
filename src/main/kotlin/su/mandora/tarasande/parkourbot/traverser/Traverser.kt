package su.mandora.tarasande.parkourbot.traverser

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.Input
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.rotation.RotationUtil
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.max

class Traverser(private val path: CopyOnWriteArrayList<BlockPos>) {

    private val stop = Movement(null, Input())

    private var state = State.PREPARING
    private var traversingTicks = 0
    private var traversingDidJump = false

    fun updateMovement(): Movement {
        if(path.size < 5 /* Little buffer */) {
            return stop
        }
        val currentObstacle = path[0]
        if(Vec3d.ofCenter(currentObstacle).subtract(MinecraftClient.getInstance().player!!.pos).horizontalLengthSquared() < 1.0) {
            path.removeAt(0)
        }

        val collisionShape = MinecraftClient.getInstance().world?.getBlockState(currentObstacle)?.getCollisionShape(MinecraftClient.getInstance().world, currentObstacle)
        val aimPoint = Vec3d.of(currentObstacle).add(collisionShape?.getClosestPointTo(MinecraftClient.getInstance().player?.eyePos?.subtract(currentObstacle.x.toDouble(), currentObstacle.y.toDouble(), currentObstacle.z.toDouble()))?.get())
        val rotations = RotationUtil.getRotations(MinecraftClient.getInstance().player!!.pos, aimPoint)
        val yawDelta = MathHelper.wrapDegrees(rotations.yaw - MinecraftClient.getInstance().player!!.yaw)

        when (state) {
            State.PREPARING -> {
                if((isOnEdge() || MinecraftClient.getInstance().player?.horizontalCollision!!)) {
                    TarasandeMain.get().log.println("Preparing -> Traversing")
                    state = State.TRAVERSING
                }
                traversingTicks = 0
                traversingDidJump = false
            }
            State.TRAVERSING -> {
                if(MinecraftClient.getInstance().player?.isOnGround!!) {
                    if(traversingDidJump && (abs(yawDelta) > 15.0 || MinecraftClient.getInstance().player?.horizontalSpeed!! <= 0.24)) {
                        TarasandeMain.get().log.println("Traversing -> Preparing")
                        state = State.PREPARING
                    }
                } else {
                    traversingDidJump = true
                }
                traversingTicks++
            }
        }

        val input = object : Input() {
            init {
                when(state) {
                    State.PREPARING -> {
                        this.pressingBack = true
                        this.movementForward = -0.3f
                        this.sneaking = true
                    }
                    State.TRAVERSING -> {
                        this.pressingForward = true
                        this.movementForward = 1.0f
                        this.jumping = isOnEdge() && traversingTicks > 2
                    }
                }
            }
        }

        return Movement(aimPoint, input)
    }

    fun isOnEdge() =
        MinecraftClient.getInstance().world?.isSpaceEmpty(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.boundingBox?.offset(MinecraftClient.getInstance().player?.velocity?.x!!,  -MinecraftClient.getInstance().player?.stepHeight?.toDouble()!!, MinecraftClient.getInstance().player?.velocity?.z!!))!! ||
        MinecraftClient.getInstance().world?.isSpaceEmpty(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.boundingBox?.offset(0.0, -MinecraftClient.getInstance().player?.stepHeight?.toDouble()!!, MinecraftClient.getInstance().player?.velocity?.z!!))!! ||
        MinecraftClient.getInstance().world?.isSpaceEmpty(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.boundingBox?.offset(MinecraftClient.getInstance().player?.velocity?.x!!,  -MinecraftClient.getInstance().player?.stepHeight?.toDouble()!!, 0.0))!!

    enum class State {
        PREPARING, TRAVERSING
    }
}

class Movement(val aimPoint: Vec3d?, val input: Input)