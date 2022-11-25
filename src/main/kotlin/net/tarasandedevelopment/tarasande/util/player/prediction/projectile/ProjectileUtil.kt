package net.tarasandedevelopment.tarasande.util.player.prediction.projectile

import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.*
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.math.random.RandomSplitter
import net.tarasandedevelopment.tarasande.injection.accessor.*
import net.tarasandedevelopment.tarasande.injection.accessor.prediction.IParticleManager
import net.tarasandedevelopment.tarasande.injection.accessor.prediction.ISoundSystem
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import java.util.function.BiConsumer

object ProjectileUtil {

    internal val projectileItems = arrayOf(ProjectileItem(Items.BOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        val velocity = BowItem.getPullProgress(if (MinecraftClient.getInstance().player?.isUsingItem!!) MinecraftClient.getInstance().player?.itemUseTime!! else stack.maxUseTime).toDouble()
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, (velocity * 3.0).toFloat(), 1.0F)
    }, ProjectileItem(Items.SNOWBALL.javaClass, EntityType.SNOWBALL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, 1.5F, 1.0F)
    }, ProjectileItem(Items.EGG.javaClass, EntityType.EGG, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, 1.5F, 1.0F)
    }, ProjectileItem(Items.ENDER_PEARL.javaClass, EntityType.ENDER_PEARL, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, 1.5F, 1.0F)
    }, ProjectileItem(Items.EXPERIENCE_BOTTLE.javaClass, EntityType.EXPERIENCE_BOTTLE, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, -20.0F, 0.7F, 1.0F)
    }, ProjectileItem(Items.SPLASH_POTION.javaClass, EntityType.POTION, false) { _, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, -20.0F, 0.5F, 1.0F)
    }, ProjectileItem(Items.TRIDENT.javaClass, EntityType.TRIDENT, true) { item, persistentProjectileEntity ->
        val riptide = EnchantmentHelper.getRiptide(item)
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, 2.5F + riptide.toFloat() * 0.5F, 1.0F)
    }, ProjectileItem(Items.FISHING_ROD.javaClass, EntityType.FISHING_BOBBER, false) { _, persistentProjectileEntity ->
        val f = MinecraftClient.getInstance().player?.pitch!!
        val g = MinecraftClient.getInstance().player?.yaw!!
        val h = MathHelper.cos(Math.toRadians(-g.toDouble()).toFloat() - Math.PI.toFloat())
        val i = MathHelper.sin(Math.toRadians(-g.toDouble()).toFloat() - Math.PI.toFloat())
        val j = -MathHelper.cos(Math.toRadians(-f.toDouble()).toFloat())
        val k = MathHelper.sin(Math.toRadians(-f.toDouble()).toFloat())

        persistentProjectileEntity.refreshPositionAndAngles(persistentProjectileEntity.x - i.toDouble() * 0.3, persistentProjectileEntity.y + 0.1, persistentProjectileEntity.z - h.toDouble() * 0.3, f, g)

        var vec3d = Vec3d(-i.toDouble(), MathHelper.clamp(-(k / j), -5.0F, 5.0F).toDouble(), -h.toDouble())
        val m = vec3d.length()
        vec3d = vec3d.multiply(0.6 / m + persistentProjectileEntity.random.nextTriangular(0.5, 0.0103365), 0.6 / m + persistentProjectileEntity.random.nextTriangular(0.5, 0.0103365), 0.6 / m + persistentProjectileEntity.random.nextTriangular(0.5, 0.0103365))

        persistentProjectileEntity.velocity = vec3d
        val rotation = RotationUtil.getRotations(vec3d)
        persistentProjectileEntity.also {
            it.yaw = rotation.yaw
            it.prevYaw = rotation.yaw

            it.pitch = rotation.pitch
            it.prevPitch = rotation.pitch
        }
    }, ProjectileItem(Items.CROSSBOW.javaClass, EntityType.ARROW, true) { stack, persistentProjectileEntity ->
        persistentProjectileEntity.setVelocity(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.pitch!!, MinecraftClient.getInstance().player?.yaw!!, 0.0F, CrossbowItem.getSpeed(stack), 1.0F)
    })

    fun predict(itemStack: ItemStack, rotation: Rotation?, predictVelocity: Boolean): ArrayList<Vec3d> {
        val projectileItem = projectileItems.first { it.isSame(itemStack.item) }
        val wasIsClient = MinecraftClient.getInstance().world?.isClient == true
        MinecraftClient.getInstance().world?.isClient = false
        val prevParticlesEnabled = (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_isParticlesEnabled() // race conditions :c
        (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(false)
        val soundSystem = MinecraftClient.getInstance().soundManager.soundSystem as ISoundSystem
        val wasSoundDisabled = soundSystem.tarasande_isDisabled()
        soundSystem.tarasande_setDisabled(true)
        val path = ArrayList<Vec3d>()
        var collided = false

        @Suppress("UNCHECKED_CAST")
        val persistentProjectileEntity = object : PersistentProjectileEntity(projectileItem.entityType as EntityType<PersistentProjectileEntity> /* This just shows how bad minecrafts code base is */, MinecraftClient.getInstance().player, MinecraftClient.getInstance().world) {
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
                if (projectileItem.entityType == EntityType.FISHING_BOBBER)
                    velocity *= 0.9375 // minecraft code says 0.92, but since we are using a PersistentProjectileEntity we have to approximate a FishingBobberEntity
            }

            override fun checkBlockCollision() {
                MinecraftClient.getInstance().world?.isClient = true
                super.checkBlockCollision()
                MinecraftClient.getInstance().world?.isClient = false
            }
        }
        persistentProjectileEntity.setPosition(MinecraftClient.getInstance().player?.getLerpedPos(MinecraftClient.getInstance().tickDelta)?.add(0.0, MinecraftClient.getInstance().player?.standingEyeHeight!! - 0.1, 0.0))
        persistentProjectileEntity.random = object : Random {
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
        }

        val prevRotation = Rotation(MinecraftClient.getInstance().player!!)
        val prevVelocity = MinecraftClient.getInstance().player?.velocity?.add(0.0, 0.0, 0.0)
        if (rotation != null) {
            MinecraftClient.getInstance().player?.yaw = rotation.yaw
            MinecraftClient.getInstance().player?.pitch = rotation.pitch
        }
        if (!predictVelocity) {
            MinecraftClient.getInstance().player?.velocity = Vec3d.ZERO
        }
        projectileItem.setupRoutine.accept(itemStack, persistentProjectileEntity)
        MinecraftClient.getInstance().player?.velocity = prevVelocity
        MinecraftClient.getInstance().player?.yaw = prevRotation.yaw
        MinecraftClient.getInstance().player?.pitch = prevRotation.pitch
        while (!collided) {
            persistentProjectileEntity.tick()
            if (persistentProjectileEntity.pos.let { it.y < MinecraftClient.getInstance().world?.bottomY!! || it == path.lastOrNull() }) break
            path.add(persistentProjectileEntity.pos)
        }
        soundSystem.tarasande_setDisabled(wasSoundDisabled)
        (MinecraftClient.getInstance().particleManager as IParticleManager).tarasande_setParticlesEnabled(prevParticlesEnabled)
        MinecraftClient.getInstance().world?.isClient = wasIsClient
        return path
    }

    class ProjectileItem(val item: Class<Item>, val entityType: EntityType<*>, val persistent: Boolean, val setupRoutine: BiConsumer<ItemStack, PersistentProjectileEntity>) {
        fun isSame(item: Item) = this.item.isInstance(item)
    }
}
