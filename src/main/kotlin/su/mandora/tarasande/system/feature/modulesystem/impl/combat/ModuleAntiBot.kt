package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.*
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.injection.accessor.IEntity
import su.mandora.tarasande.injection.accessor.playerlistentry.IOtherClientPlayerEntity
import su.mandora.tarasande.injection.accessor.playerlistentry.IPlayerListEntry
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.meta.ValueButton
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.kotlinruntime.WeakSet
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import java.util.*

class ModuleAntiBot : Module("Anti bot", "Prevents modules from interacting with bots", ModuleCategory.COMBAT) {
    private val checks = ValueMode(this, "Checks", true, "Ticks existed", "Sound", "Ground", "Invisible", "Sneaked", "Movement", "Line of sight", "Invalid game mode", "Swing", "Player list")
    private val ticksExisted = ValueNumber(this, "Ticks existed", 0.0, 20.0, 100.0, 1.0, isEnabled = { checks.isSelected(0) })
    private val soundDistance = ValueNumber(this, "Sound distance", 0.0, 1.0, 1.0, 0.1, isEnabled = { checks.isSelected(1) })
    private val minVolume = ValueNumber(this, "Min volume", 0.0, 0.0, 1.0, 0.1, isEnabled = { checks.isSelected(1) })
    private val swingMode = ValueMode(this, "Swing mode", true, "Swing main hand", "Swing off hand", isEnabled = { checks.isSelected(8) })

    init {
        swingMode.select(0)
        swingMode.select(1)
    }

    private val groundMode = ValueMode(this, "Ground mode", false, "On ground", "Off ground", isEnabled = { checks.isSelected(2) })
    private val invisibleMode = ValueMode(this, "Invisible mode", true, "Invisible to everyone", "Invisible to self", isEnabled = { checks.isSelected(3) })

    init {
        invisibleMode.select(0)
        invisibleMode.select(1)
    }

    private val movementMode = ValueMode(this, "Movement mode", true, "Relative", "Absolute", isEnabled = { checks.isSelected(5) })

    init {
        movementMode.select(0)
    }

    private val fov = ValueNumber(this, "FOV", 0.0, Rotation.MAXIMUM_DELTA / 2, Rotation.MAXIMUM_DELTA, 1.0, isEnabled = { checks.isSelected(6) })
    private var playerListMode = ValueMode(this, "Player list mode", true, "Unreferenced", "Reused", "Unlisted", "Duplicated", isEnabled = { checks.isSelected(9) })

    init {
        playerListMode.select(0)
    }

    init {
        ValueButton(this, "Reset captured data") {
            onDisable()
        }
    }

    private var manualHit = WeakSet<PlayerEntity>()

    private var passedSound = WeakSet<PlayerEntity>()
    private var passedGround = WeakSet<PlayerEntity>()
    private var passedInvisible = WeakSet<PlayerEntity>()
    private var passedSneak = WeakSet<PlayerEntity>()
    private var passedMovement = WeakSet<PlayerEntity>()
    private var passedLineOfSight = WeakSet<PlayerEntity>()

    private var invalidGameMode = HashSet<UUID>()

    private var passedSwing = WeakSet<PlayerEntity>()

    override fun onDisable() {
        manualHit = WeakSet()

        passedSound = WeakSet()
        passedGround = WeakSet()
        passedInvisible = WeakSet()
        passedSneak = WeakSet()
        passedMovement = WeakSet()
        passedLineOfSight = WeakSet()

        invalidGameMode = HashSet()

        passedSwing = WeakSet()
    }

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                if (mc.world == null) return@registerEvent
                when (event.packet) {
                    is PlayerRespawnS2CPacket -> {
                        if (event.packet.isNewWorld())
                            onDisable()
                    }

                    is PlaySoundS2CPacket -> {
                        if (checks.isSelected(1))
                            if (event.packet.volume >= minVolume.value)
                                for (entity in mc.world?.entities!!)
                                    if (entity is PlayerEntity && !passedSound.contains(entity) && entity.pos?.squaredDistanceTo(Vec3d(event.packet.x, event.packet.y, event.packet.z))!! <= soundDistance.value * soundDistance.value)
                                        passedSound.add(entity)
                    }

                    is EntityPositionS2CPacket -> {
                        val entity = mc.world!!.getEntityById(event.packet.id)
                        if (entity is PlayerEntity)
                            if (checks.isSelected(5) && movementMode.isSelected(1))
                                if (!passedMovement.contains(entity))
                                    if (event.packet.x != entity.trackedPosition.pos.x || event.packet.y != entity.trackedPosition.pos.y || event.packet.z != entity.trackedPosition.pos.z)
                                        passedMovement.add(entity)
                    }

                    is EntityS2CPacket -> {
                        val entity = event.packet.getEntity(mc.world)
                        if (entity is PlayerEntity) {
                            if (checks.isSelected(5) && movementMode.isSelected(0))
                                if (!passedMovement.contains(entity))
                                    if (event.packet.deltaX != 0.toShort() || event.packet.deltaY != 0.toShort() || event.packet.deltaZ != 0.toShort())
                                        passedMovement.add(entity)

                            if (checks.isSelected(2))
                                if (!passedGround.contains(entity))
                                    if (groundMode.isSelected(0) && event.packet.isOnGround || groundMode.isSelected(1) && !event.packet.isOnGround)
                                        passedGround.add(entity)

                            if (checks.isSelected(6))
                                if (!passedLineOfSight.contains(entity)) {
                                    val serverPosition = entity.trackedPosition.withDelta(event.packet.deltaX.toLong(), event.packet.deltaY.toLong(), event.packet.deltaZ.toLong())

                                    if (Rotation(mc.player!!.lastYaw, mc.player!!.lastPitch).fov(RotationUtil.getRotations(mc.player?.eyePos!!, serverPosition)) <= fov.value)
                                        passedLineOfSight.add(entity)
                                }
                        }
                    }

                    is EntityAnimationS2CPacket -> {
                        if (checks.isSelected(8))
                            if ((event.packet.animationId == EntityAnimationS2CPacket.SWING_MAIN_HAND && swingMode.isSelected(0)) || (event.packet.animationId == EntityAnimationS2CPacket.SWING_OFF_HAND && swingMode.isSelected(1))) {
                                val entity = mc.world?.getEntityById(event.packet.id)
                                if (entity != null && entity is PlayerEntity)
                                    passedSwing.add(entity)
                            }
                    }
                }
            }
        }

        registerEvent(EventEntityFlag::class.java) { event ->
            if (checks.isSelected(4))
                if (event.entity is PlayerEntity && !passedSneak.contains(event.entity))
                    if (event.flag == Entity.SNEAKING_FLAG_INDEX)
                        if (event.enabled)
                            passedSneak.add(event.entity)
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (checks.isSelected(3)) {
                    for (player in mc.world?.players ?: return@registerEvent) {
                        if (!passedInvisible.contains(player))
                            if (run {
                                    if (invisibleMode.isSelected(0) && (player as IEntity).tarasande_forceGetFlag(Entity.INVISIBLE_FLAG_INDEX))
                                        return@run false
                                    if (invisibleMode.isSelected(1) && player.isInvisibleTo(mc.player))
                                        return@run false
                                    return@run true
                                }) passedInvisible.add(player)
                    }
                }
            }
        }

        registerEvent(EventInvalidGameMode::class.java) { event ->
            if (checks.isSelected(7))
                invalidGameMode.add(event.uuid)
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state == EventAttackEntity.State.PRE && event.entity is PlayerEntity) {
                manualHit.add(event.entity)
            }
        }

        registerEvent(EventIsEntityAttackable::class.java) { event ->
            if (event.attackable) {
                if (event.entity is PlayerEntity)
                    event.attackable = !isBot(event.entity)
            }
        }
    }

    fun isBot(playerEntity: PlayerEntity): Boolean {
        if (!enabled.value) return false
        if (manualHit.contains(playerEntity)) return false // The user did try to hit this entity, why would they try to attack a bot?

        if (checks.isSelected(0) && playerEntity.age < ticksExisted.value) return true
        if (checks.isSelected(1) && !passedSound.contains(playerEntity)) return true
        if (checks.isSelected(2) && !passedGround.contains(playerEntity)) return true
        if (checks.isSelected(3) && !passedInvisible.contains(playerEntity)) return true
        if (checks.isSelected(4) && !passedSneak.contains(playerEntity)) return true
        if (checks.isSelected(5) && !passedMovement.contains(playerEntity)) return true
        if (checks.isSelected(6) && !passedLineOfSight.contains(playerEntity)) return true

        if (checks.isSelected(7) && invalidGameMode.contains(playerEntity.gameProfile.id)) return true

        if (checks.isSelected(8) && !passedSwing.contains(playerEntity)) return true

        if (checks.isSelected(9)) {
            if (playerEntity is OtherClientPlayerEntity) {
                val weakRef = (playerEntity as IOtherClientPlayerEntity).tarasande_getPlayerListEntry() ?: return true // This seems to happen when other mods add fake players

                val playerListEntry = weakRef.get()
                if (playerListEntry == null) {
                    if (playerListMode.isSelected(0))
                        return true
                } else {
                    if (playerListMode.isSelected(0) && (playerListEntry as IPlayerListEntry).tarasande_isRemoved()) return true
                    if (playerListMode.isSelected(1) && (playerListEntry as IPlayerListEntry).tarasande_getOwners().size > 1) return true
                    if (playerListMode.isSelected(2) && !(playerListEntry as IPlayerListEntry).tarasande_isListed()) return true
                    if (playerListMode.isSelected(3) && (playerListEntry as IPlayerListEntry).tarasande_getDuplicates().any { !(it as IPlayerListEntry).tarasande_isRemoved() }) return true
                }
            }
        }

        return false
    }

}