package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.Particle
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.particle.ParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventChunkOcclusion
import net.tarasandedevelopment.tarasande.event.EventParticle
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueButton
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueRegistry

class ModuleNoRender : Module("No render", "", ModuleCategory.RENDER) {

    inner class Overlay : NoRenderType("Overlay") {

        val hurtCam = NoRenderBooleanValue(this, "Hurt cam", false)
        val portalOverlay = NoRenderBooleanValue(this, "Portal overlay", false)
        val spyglassOverlay = NoRenderBooleanValue(this, "Spyglass overlay", false)
        val noNausea = NoRenderBooleanValue(this, "Nausea overlay", false)
        val pumpkinOverlay = NoRenderBooleanValue(this, "Pumpkin overlay", false)
        val powderedSnowOverlay = NoRenderBooleanValue(this, "Powdered snow overlay", false)
        val fireOverlay = NoRenderBooleanValue(this, "Fire overlay", false)
        val waterOverlay = NoRenderBooleanValue(this, "Water overlay", false)
        val inWallOverlay = NoRenderBooleanValue(this, "In wall overlay", false)
        val vignette = NoRenderBooleanValue(this, "Vignette", false)
        val totemAnimation = NoRenderBooleanValue(this, "Totem animation", false)
        val eatParticles = NoRenderBooleanValue(this, "Eat particles", false)
    }
    val overlay = Overlay()

    inner class HUD : NoRenderType("Hud") {

        val bossBar = NoRenderBooleanValue(this, "Boss bar", false)
        val scoreboard = NoRenderBooleanValue(this, "Scoreboard", false)
        val crosshair = NoRenderBooleanValue(this, "Cross hair", false)
        val heldItemName = NoRenderBooleanValue(this, "Held item name", false)
        val potionIcons = NoRenderBooleanValue(this, "Potion icons", false)
    }
    val hud = HUD()

    inner class World : NoRenderType("World") {

        val weather = NoRenderBooleanValue(this, "Weather", false)
        val fog = NoRenderBooleanValue(this, "Fog", false)
        val enchantmentTableBook = NoRenderBooleanValue(this, "Enchantment table book", false)
        val signText = NoRenderBooleanValue(this, "Sign text", false)
        val blockBreakParticles = NoRenderBooleanValue(this, "Block break particles", false)
        val skylightUpdates = NoRenderBooleanValue(this, "Skylight updates", false)
        val fallingBlocks = NoRenderBooleanValue(this, "Falling blocks", false)
        val caveCulling = NoRenderBooleanValue(this, "Cave culling", false)
        val mapMarkers = NoRenderBooleanValue(this, "Map markers", false)
        val banners = ValueMode(this, "Banners", false, "All", "Pillar", "None")
        val fireworkExplosions = NoRenderBooleanValue(this, "Firework explosions", false)
        val particles = object : ValueRegistry<ParticleType<*>>(this, "Particles", Registry.PARTICLE_TYPE) {
            override fun getTranslationKey(key: Any?) = Registry.PARTICLE_TYPE.getId(key as ParticleType<*>?)!!.path
        }
        val barrierInvisibility = NoRenderBooleanValue(this, "Barrier invisibility", true)
    }
    val world = World()

    inner class Entity : NoRenderType("Entity") {

        val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registry.ENTITY_TYPE) {
            override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        }
        val armor = NoRenderBooleanValue(this, "Armor", false)
        val mobInSpawner = NoRenderBooleanValue(this, "Mob in spawner", false)
        val deadEntities = NoRenderBooleanValue(this, "Dead entities", false)

        fun noEntity(entity: net.minecraft.entity.Entity): Boolean {
            return !TarasandeMain.get().disabled && isEnabled() && entities.list.contains(entity.type)
        }
    }
    val entity = Entity()

    init {
        for (overlay in arrayOf(overlay, hud, world, entity)) {
            object : ValueButton(this, overlay.name) {
                override fun onChange() {
                    MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, overlay.name, overlay))
                }
            }
        }

        registerEvent(EventChunkOcclusion::class.java) {
            if (this.world.caveCulling.should()) {
                it.cancelled = true
            }
        }

        registerEvent(EventParticle::class.java) {
            if (world.weather.should() && it.effect == ParticleTypes.RAIN) {
                it.cancelled = true
            }

            if (world.fireworkExplosions.value && it.effect == ParticleTypes.FIREWORK) {
                it.cancelled = true
            }

            if (world.particles.list.contains(it.effect.type)) {
                it.cancelled = true
            }
        }
    }

    open class NoRenderType(val name: String)
    inner class NoRenderBooleanValue(owner: Any, name: String, value: Boolean) : ValueBoolean(owner, name, value) {
        fun should() = value && this@ModuleNoRender.enabled && !TarasandeMain.get().disabled
    }
}
