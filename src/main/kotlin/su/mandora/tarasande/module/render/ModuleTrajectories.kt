package su.mandora.tarasande.module.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.BowItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.math.random.RandomSplitter
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.mixin.accessor.ICrossbowItem
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IParticleManager
import su.mandora.tarasande.mixin.accessor.IWorld
import su.mandora.tarasande.util.math.rotation.RotationUtil
import java.util.function.BiConsumer
import java.util.function.Consumer

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private val projectileItems = arrayOf(ProjectileItem(Items.BOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        val velocity = BowItem.getPullProgress(if (mc.player?.isUsingItem!!) mc.player?.itemUseTime!! else stack.maxUseTime).toDouble()
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, (velocity * 3.0).toFloat(), 1.0f)
    }, ProjectileItem(Items.SNOWBALL.javaClass, EntityType.SNOWBALL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.EGG.javaClass, EntityType.EGG, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.ENDER_PEARL.javaClass, EntityType.ENDER_PEARL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f)
    }, ProjectileItem(Items.EXPERIENCE_BOTTLE.javaClass, EntityType.EXPERIENCE_BOTTLE, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, -20.0f, 0.7f, 1.0f)
    }, ProjectileItem(Items.SPLASH_POTION.javaClass, EntityType.POTION, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, -20.0f, 0.5f, 1.0f)
    }, ProjectileItem(Items.TRIDENT.javaClass, EntityType.TRIDENT, true) { item, persistentProjectileEntity ->
        val riptide = EnchantmentHelper.getRiptide(item)
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 2.5f + riptide.toFloat() * 0.5f, 1.0f)
    }, ProjectileItem(Items.FISHING_ROD.javaClass, EntityType.FISHING_BOBBER, false) { _, persistentProjectileEntity ->
        val f: Float = mc.player?.pitch!!
        val g: Float = mc.player?.yaw!!
        val h = MathHelper.cos(-g * (Math.PI.toFloat() / 180) - Math.PI.toFloat())
        val i = MathHelper.sin(-g * (Math.PI.toFloat() / 180) - Math.PI.toFloat())
        val j = -MathHelper.cos(-f * (Math.PI.toFloat() / 180))
        val k = MathHelper.sin(-f * (Math.PI.toFloat() / 180))

        persistentProjectileEntity.refreshPositionAndAngles(persistentProjectileEntity.x - i.toDouble() * 0.3, persistentProjectileEntity.y + 0.1, persistentProjectileEntity.z - h.toDouble() * 0.3, f, g)

        var vec3d = Vec3d(-i.toDouble(), MathHelper.clamp(-(k / j), -5.0f, 5.0f).toDouble(), -h.toDouble())
        val m = vec3d.length()
        vec3d = vec3d.multiply(0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045, 0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045, 0.6 / m + 0.5 + (persistentProjectileEntity as IEntity).tarasande_getRandom().nextGaussian() * 0.0045)

        persistentProjectileEntity.velocity = vec3d
        val rotation = RotationUtil.getRotations(vec3d)
        persistentProjectileEntity.also {
            it.yaw = rotation.yaw
            it.prevYaw = rotation.yaw
        }
        persistentProjectileEntity.also {
            it.pitch = rotation.pitch
            it.prevPitch = rotation.pitch
        }
    }, ProjectileItem(Items.CROSSBOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, (stack.item as ICrossbowItem).tarasande_invokeGetSpeed(stack), 1.0f)

    })

    private fun predict(itemStack: ItemStack): ArrayList<Vec3d> {
        val projectileItem = projectileItems.first { it.isSame(itemStack.item) }
        (mc.world as IWorld).tarasande_setIsClient(false)
        val path = ArrayList<Vec3d>()
        var collided = false

        @Suppress("UNCHECKED_CAST")
        val persistentProjectileEntity = object : PersistentProjectileEntity(projectileItem.entityType as EntityType<PersistentProjectileEntity> /* This just shows how bad minecrafts code base is */, mc.player, mc.world) {
            override fun asItemStack(): ItemStack? = null
            override fun onEntityHit(entityHitResult: EntityHitResult?) {
                collided = true
            }

            override fun onHit(target: LivingEntity?) {
                collided = true
            }

            override fun tick() {
                super.tick()
                if (!projectileItem.persistent) addVelocity(0.0, 0.02, 0.0)
                if (projectileItem.entityType == EntityType.FISHING_BOBBER) velocity = velocity.multiply(0.92)
            }
        }
        persistentProjectileEntity.setPosition(mc.player?.getLerpedPos(mc.tickDelta)?.add(0.0, mc.player?.standingEyeHeight!! - 0.1, 0.0))
        (persistentProjectileEntity as IEntity).tarasande_setRandom(object : Random {
            override fun split(): Random {
                return this
            }

            override fun nextSplitter(): RandomSplitter {
                val this2 = this // Kotlin is great
                return object : RandomSplitter {
                    override fun split(seed: String?): Random {
                        return this2
                    }

                    override fun split(x: Int, y: Int, z: Int): Random {
                        return this2
                    }

                    override fun addDebugInfo(info: StringBuilder?) {
                    }
                }
            }

            override fun setSeed(seed: Long) {
            }

            override fun nextInt(): Int {
                return 0
            }

            override fun nextInt(bound: Int): Int {
                return 0
            }

            override fun nextLong(): Long {
                return 0L
            }

            override fun nextBoolean(): Boolean {
                return false
            }

            override fun nextFloat(): Float {
                return 0.0F
            }

            override fun nextDouble(): Double {
                return 0.0
            }

            override fun nextGaussian(): Double {
                return 0.0
            }
        })

        projectileItem.setupRoutine.accept(itemStack, persistentProjectileEntity)
        while (!collided) {
            val prevParticlesEnabled = (mc.particleManager as IParticleManager).tarasande_areParticlesEnabled() // race conditions :c
            (mc.particleManager as IParticleManager).tarasande_setParticlesEnabled(false)
            persistentProjectileEntity.tick()
            (mc.particleManager as IParticleManager).tarasande_setParticlesEnabled(prevParticlesEnabled)
            if (persistentProjectileEntity.pos.let { it.y < mc.world?.bottomY!! || it == path.lastOrNull() }) break
            path.add(persistentProjectileEntity.pos)
        }
        (mc.world as IWorld).tarasande_setIsClient(true)
        return path
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventRender3D) {
            var stack: ItemStack? = null; Hand.values().forEach { hand ->
                if (projectileItems.any {
                        mc.player?.getStackInHand(hand)?.item?.let { item ->
                            it.isSame(item)
                        } == true
                    }) stack = mc.player?.getStackInHand(hand)
            }

            if (stack != null) {
                RenderSystem.enableBlend()
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                RenderSystem.disableCull()
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                RenderSystem.disableDepthTest()
                event.matrices.push()
                val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
                event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                val bufferBuilder = Tessellator.getInstance().buffer
                bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
                val matrix = event.matrices.peek()?.positionMatrix!!
                val path = predict(stack!!)
                for (vec in path) {
                    bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, 1f).next()
                }
                BufferRenderer.drawWithShader(bufferBuilder.end())
                event.matrices.pop()
                RenderSystem.enableDepthTest()
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                RenderSystem.enableCull()
                RenderSystem.disableBlend()
            }
        }
    }

    inner class ProjectileItem(val item: Class<Item>, val entityType: EntityType<*>, val persistent: Boolean, val setupRoutine: BiConsumer<ItemStack, PersistentProjectileEntity>) {
        fun isSame(item: Item) = this.item.isInstance(item)
    }
}