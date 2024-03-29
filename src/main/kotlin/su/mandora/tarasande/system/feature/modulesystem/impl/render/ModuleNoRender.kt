package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.entity.EntityType
import net.minecraft.particle.ParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoRender : Module("No render", "Disables rendering of certain things.", ModuleCategory.RENDER) {

    inner class NoRenderTypeOverlay : NoRenderType("Overlay") {
        val portalOverlay = ValueBooleanNoRender(this, "Portal overlay", false)
        val spyglassOverlay = ValueBooleanNoRender(this, "Spyglass overlay", false)
        val noNausea = ValueBooleanNoRender(this, "Nausea overlay", false)
        val pumpkinOverlay = ValueBooleanNoRender(this, "Pumpkin overlay", false)
        val powderedSnowOverlay = ValueBooleanNoRender(this, "Powdered snow overlay", false)
        val fireOverlay = ValueBooleanNoRender(this, "Fire overlay", false)
        val waterOverlay = ValueBooleanNoRender(this, "Water overlay", false)
        val inWallOverlay = ValueBooleanNoRender(this, "In wall overlay", false)
        val vignette = ValueBooleanNoRender(this, "Vignette", false)
        val totemAnimation = ValueBooleanNoRender(this, "Totem animation", false)
        val eatParticles = ValueBooleanNoRender(this, "Eat particles", false)
        val enchantmentTableText = ValueBooleanNoRender(this, "Enchantment table text", false)
    }

    val overlay = NoRenderTypeOverlay()

    inner class NoRenderTypeHUD : NoRenderType("Hud") {

        val bossBar = ValueBooleanNoRender(this, "Boss bar", false)
        val scoreboard = ValueBooleanNoRender(this, "Scoreboard", false)
        val crosshair = ValueBooleanNoRender(this, "Cross hair", false)
        val heldItemName = ValueBooleanNoRender(this, "Held item name", false)
        val potionIcons = ValueMode(this, "Potion icons", false, "Off", "Remove", "Force")
    }

    val hud = NoRenderTypeHUD()

    inner class NoRenderTypeWorld : NoRenderType("World") {
        val weather = ValueBooleanNoRender(this, "Weather", false)
        val fog = ValueBooleanNoRender(this, "Fog", false)
        val enchantmentTableBook = ValueBooleanNoRender(this, "Enchantment table book", false)
        val signText = ValueBooleanNoRender(this, "Sign text", false)
        val blockBreakParticles = ValueBooleanNoRender(this, "Block break particles", false)
        val fallingBlocks = ValueBooleanNoRender(this, "Falling blocks", false)
        val caveCulling = ValueBooleanNoRender(this, "Cave culling", false)
        val mapMarkers = ValueBooleanNoRender(this, "Map markers", false)
        val fireworkExplosions = ValueBooleanNoRender(this, "Firework explosions", false)
        val particles = object : ValueRegistry<ParticleType<*>>(this, "Particles", Registries.PARTICLE_TYPE, true) {
            override fun getTranslationKey(key: Any?) = Registries.PARTICLE_TYPE.getId(key as ParticleType<*>?)!!.path
        }
        val barrierInvisibility = object : ValueBooleanNoRender(this, "Barrier invisibility", true) {
            override fun should(): Boolean {
                return !super.should()
            }
        }
    }

    val world = NoRenderTypeWorld()

    inner class NoRenderTypeEntity : NoRenderType("Entity") {

        val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true) {
            override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        }
        val armor = ValueBooleanNoRender(this, "Armor", false)
        val mobInSpawner = ValueBooleanNoRender(this, "Mob in spawner", false)
        val deadEntities = ValueBooleanNoRender(this, "Dead entities", false)
        val entityNameTags = object : ValueRegistry<EntityType<*>>(this, "Entity Name tags", Registries.ENTITY_TYPE, true) {
            override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
        }
    }

    val entity = NoRenderTypeEntity()

    init {
        for (overlay in arrayOf(overlay, hud, world, entity)) {
            ValueButtonOwnerValues(this, overlay.name, overlay)
        }

        registerEvent(EventChunkOcclusion::class.java) {event ->
            if (this.world.caveCulling.should()) {
                event.cancelled = true
            }
        }

        registerEvent(EventParticle::class.java) {event ->
            if (world.weather.should() && event.effect == ParticleTypes.RAIN) {
                event.cancelled = true
            }

            if (world.fireworkExplosions.value && event.effect == ParticleTypes.FIREWORK) {
                event.cancelled = true
            }

            if (world.particles.isSelected(event.effect.type)) {
                event.cancelled = true
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state == EventRender3D.State.PRE && overlay.noNausea.value)
                mc.player?.nauseaIntensity = 0F
        }

        registerEvent(EventFogStart::class.java, 9999) {event ->
            if (world.fog.should()) {
                event.distance *= 9999F
            }
        }
        registerEvent(EventFogEnd::class.java, 9999) {event ->
            if (world.fog.should()) {
                event.distance *= 9999F
            }
        }
    }

    open class NoRenderType(val name: String)
    open inner class ValueBooleanNoRender(owner: Any, name: String, value: Boolean) : ValueBoolean(owner, name, value) {
        open fun should() = value && this@ModuleNoRender.enabled.value
    }
}
