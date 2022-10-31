package net.tarasandedevelopment.tarasande.features.module.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.network.packet.s2c.play.EntityS2CPacket
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.meta.ValueButton
import java.util.*

class ModuleAntiBot : Module("Anti bot", "Prevents modules from interacting with bots", ModuleCategory.COMBAT) {
    private val checks = ValueMode(this, "Checks", true, "Ticks existed", "Sound", "Ground", "Invisible", "Sneaked", "Movement", "Line of sight", "Invalid gamemode", "Swing")
    private val ticksExisted = object : ValueNumber(this, "Ticks existed", 0.0, 20.0, 100.0, 1.0) {
        override fun isEnabled() = checks.isSelected(0)
    }
    private val soundDistance = object : ValueNumber(this, "Sound distance", 0.0, 1.0, 1.0, 0.1) {
        override fun isEnabled() = checks.isSelected(1)
    }
    private val groundMode = object : ValueMode(this, "Ground mode", false, "On ground", "Off ground") {
        override fun isEnabled() = checks.isSelected(2)
    }
    private val invisibleMode = object : ValueMode(this, "Invisible mode", true, "Invisible to everyone", "Invisible to self") {
        override fun isEnabled() = checks.isSelected(3)
    }
    private val fov = object : ValueNumber(this, "FOV", 0.0, Rotation.MAXIMUM_DELTA / 2, Rotation.MAXIMUM_DELTA, 1.0) {
        override fun isEnabled() = checks.isSelected(6)
    }

    init {
        object : ValueButton(this, "Reset captured data") {
            override fun onChange() = onDisable()
        }
    }

    private val manualHit = HashSet<PlayerEntity>()

    private val passedSound = HashSet<PlayerEntity>()
    private val passedGround = HashSet<PlayerEntity>()
    private val passedInvisible = HashSet<PlayerEntity>()
    private val passedSneak = HashSet<PlayerEntity>()
    private val passedMovement = HashSet<PlayerEntity>()
    private val passedLineOfSight = HashSet<PlayerEntity>()

    private val invalidGameMode = HashSet<UUID>()

    private val passedSwing = HashSet<PlayerEntity>()

    override fun onDisable() {
        manualHit.clear()

        passedSound.clear()
        passedGround.clear()
        passedInvisible.clear()
        passedSneak.clear()
        passedMovement.clear()
        passedLineOfSight.clear()

        invalidGameMode.clear()

        passedSwing.clear()
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                if (mc.world == null) return@registerEvent
                when (event.packet) {
                    is PlayerRespawnS2CPacket -> {
                        onDisable() // prevent memory leak
                    }

                    is PlaySoundS2CPacket -> {
                        for (entity in mc.world?.entities!!) {
                            if (entity is PlayerEntity && !passedSound.contains(entity) && entity.pos?.squaredDistanceTo(Vec3d(event.packet.x, event.packet.y, event.packet.z))!! <= soundDistance.value * soundDistance.value) {
                                passedSound.add(entity)
                            }
                        }
                    }

                    is EntityS2CPacket -> {
                        val entity = event.packet.getEntity(mc.world)
                        if (entity is PlayerEntity) {
                            if (!passedMovement.contains(entity)) {
                                if (event.packet.deltaX != 0.toShort() || event.packet.deltaY != 0.toShort() || event.packet.deltaZ != 0.toShort())
                                    passedMovement.add(entity)
                            }
                            if (!passedGround.contains(entity)) {
                                if (groundMode.isSelected(0) && event.packet.isOnGround || groundMode.isSelected(1) && !event.packet.isOnGround)
                                    passedGround.add(entity)
                            }
                            if (!passedLineOfSight.contains(entity)) {
                                val serverPosition = entity.trackedPosition.withDelta(event.packet.deltaX.toLong(), event.packet.deltaY.toLong(), event.packet.deltaZ.toLong())

                                if (Rotation(mc.player!!.lastYaw, mc.player!!.lastPitch).fov(RotationUtil.getRotations(mc.player?.eyePos!!, serverPosition)) <= fov.value)
                                    passedLineOfSight.add(entity)
                            }
                        }
                    }

                    is EntityAnimationS2CPacket -> {
                        if (event.packet.animationId == EntityAnimationS2CPacket.SWING_MAIN_HAND || event.packet.animationId == EntityAnimationS2CPacket.SWING_OFF_HAND) {
                            val entity = mc.world?.getEntityById(event.packet.id)
                            if (entity != null && entity is PlayerEntity)
                                passedSwing.add(entity)
                        }
                    }
                }
            }
        }

        registerEvent(EventEntityFlag::class.java) { event ->
            if (event.entity is PlayerEntity && !passedSneak.contains(event.entity))
                if (event.flag == Entity.SNEAKING_FLAG_INDEX)
                    if (event.enabled)
                        passedSneak.add(event.entity)
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (checks.isSelected(2)) {
                    for (player in mc.world?.players ?: return@registerEvent) {
                        if (passedInvisible.contains(player))
                            continue
                        when {
                            invisibleMode.isSelected(0) -> if (!(player as IEntity).tarasande_forceGetFlag(Entity.INVISIBLE_FLAG_INDEX)) passedInvisible.add(player)
                            invisibleMode.isSelected(1) -> if (!player.isInvisibleTo(mc.player)) passedInvisible.add(player)
                        }
                    }
                }
            }
        }

        registerEvent(EventIsEntityAttackable::class.java) { event ->
            event.attackable = event.attackable && !isBot(event.entity)
        }

        registerEvent(EventInvalidGameMode::class.java) { event ->
            invalidGameMode.add(event.uuid)
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state == EventAttackEntity.State.PRE && event.entity is PlayerEntity) {
                manualHit.add(event.entity)
            }
        }
    }

    fun isBot(entity: Entity): Boolean {
        if (!enabled) return false
        if (entity is PlayerEntity) {
            if (manualHit.contains(entity)) return false // The user did try to hit this entity, why would they try to attack a bot?

            if (checks.isSelected(0) && entity.age < ticksExisted.value) return true
            if (checks.isSelected(1) && !passedSound.contains(entity)) return true
            if (checks.isSelected(2) && !passedGround.contains(entity)) return true
            if (checks.isSelected(3) && !passedInvisible.contains(entity)) return true
            if (checks.isSelected(4) && !passedSneak.contains(entity)) return true
            if (checks.isSelected(5) && !passedMovement.contains(entity)) return true
            if (checks.isSelected(6) && !passedLineOfSight.contains(entity)) return true

            if (checks.isSelected(7) && invalidGameMode.contains(entity.gameProfile.id)) return true

            if (checks.isSelected(8) && !passedSwing.contains(entity)) return true
        }
        return false
    }

}