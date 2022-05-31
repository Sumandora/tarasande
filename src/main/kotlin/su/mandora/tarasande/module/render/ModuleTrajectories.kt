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
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IParticleManager
import su.mandora.tarasande.mixin.accessor.IWorld
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private val projectileItems = arrayOf(
        ProjectileItem(Items.BOW.javaClass, EntityType.ARROW, true) { item, persistentProjectileEntity ->
            val velocity = BowItem.getPullProgress(if (mc.player?.isUsingItem!!) mc.player?.itemUseTime!! else item.maxUseTime).toDouble()
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, (velocity * 3.0).toFloat(), 1.0f)
        },
        ProjectileItem(Items.SNOWBALL.javaClass, EntityType.SNOWBALL, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f)
        },
        ProjectileItem(Items.EGG.javaClass, EntityType.EGG, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f);
        },
        ProjectileItem(Items.ENDER_PEARL.javaClass, EntityType.ENDER_PEARL, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 1.5f, 1.0f);
        },
        ProjectileItem(Items.EXPERIENCE_BOTTLE.javaClass, EntityType.EXPERIENCE_BOTTLE, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, -20.0f, 0.7f, 1.0f)
        },
        ProjectileItem(Items.SPLASH_POTION.javaClass, EntityType.POTION, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, -20.0f, 0.5f, 1.0f)
        },
        ProjectileItem(Items.TRIDENT.javaClass, EntityType.TRIDENT, true) { item, persistentProjectileEntity ->
            val riptide = EnchantmentHelper.getRiptide(item)
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 2.5f + riptide.toFloat() * 0.5f, 1.0f)
        },
        ProjectileItem(Items.FISHING_ROD.javaClass, EntityType.FISHING_BOBBER, false) { _, persistentProjectileEntity ->
            persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, 0.5f, 0.0075f / 0.0045f)
        }
    )

    private fun predict(itemStack: ItemStack): ArrayList<Vec3d> {
        val projectileItem = projectileItems.first { it.isSame(itemStack.item) }
        (mc.world as IWorld).setIsClient(false)
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
                if(!projectileItem.persistent)
                    addVelocity(0.0, 0.02, 0.0)
            }
        }
        persistentProjectileEntity.setPosition(mc.player?.getLerpedPos(mc.tickDelta)?.add(0.0, mc.player?.standingEyeHeight!! - 0.1, 0.0))
        (persistentProjectileEntity as IEntity).also {
            it.setRandom(object : Random() {
                override fun next(bits: Int): Int {
                    return 0
                }

                override fun nextGaussian(): Double {
                    return 0.0
                }
            })
        }
        projectileItem.setupRoutine.accept(itemStack, persistentProjectileEntity)
        while (!collided) {
            val prevParticlesEnabled = (mc.particleManager as IParticleManager).areParticlesEnabled() // race conditions :c
            (mc.particleManager as IParticleManager).setParticlesEnabled(false)
            persistentProjectileEntity.tick()
            (mc.particleManager as IParticleManager).setParticlesEnabled(prevParticlesEnabled)
            if (persistentProjectileEntity.pos.let { it.y < mc.world?.bottomY!! || it == path.lastOrNull() }) break
            path.add(persistentProjectileEntity.pos)
        }
        (mc.world as IWorld).setIsClient(true)
        return path
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventRender3D) {
            var stack: ItemStack? = null; Hand.values().forEach { hand -> if(projectileItems.any { mc.player?.getStackInHand(hand)?.item?.let { item -> it.isSame(item) } == true }) stack = mc.player?.getStackInHand(hand) }

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
                bufferBuilder.end()
                BufferRenderer.draw(bufferBuilder)
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