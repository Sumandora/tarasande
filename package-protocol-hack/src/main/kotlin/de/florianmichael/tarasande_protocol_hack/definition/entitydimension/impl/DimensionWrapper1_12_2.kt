package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.function.ToAxisFunction
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.type.EntityDimensionDynamic
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.type.EntityDimensionFixed
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper.HierarchyDimensionWrapper
import net.minecraft.entity.*
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.decoration.AbstractDecorationEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.decoration.LeashKnotEntity
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.*
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity

class DimensionWrapper1_12_2 : HierarchyDimensionWrapper() {

    override fun init() {
        registerDimension(ExperienceOrbEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        registerDimension(WitherSkeletonEntity::class.java, EntityDimensionFixed(0.72f, 2.535f))
        registerDimension(AbstractSkeletonEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        registerDimension(ArmorStandEntity::class.java, EntityDimensionFixed(0.5f, 1.975f))
        registerDimension(OcelotEntity::class.java, EntityDimensionFixed(0.6f, 0.7f))
        registerDimension(MooshroomEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        registerDimension(RabbitEntity::class.java, EntityDimensionFixed(0.6f, 0.7f))
        registerDimension(PigEntity::class.java, EntityDimensionFixed(0.9f, 0.9f))
        registerDimension(WitherSkullEntity::class.java, EntityDimensionFixed(0.3125f, 0.3125f))
        registerDimension(SmallFireballEntity::class.java, EntityDimensionFixed(0.3125f, 0.3125f))
        registerDimension(AbstractFireballEntity::class.java, EntityDimensionFixed(1.0f, 1.0f))
        registerDimension(IronGolemEntity::class.java, EntityDimensionFixed(1.4f, 2.9f))
        registerDimension(MinecartEntity::class.java, EntityDimensionFixed(0.98f, 0.7f))
        registerDimension(ItemEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        registerDimension(FireworkRocketEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        registerDimension(FallingBlockEntity::class.java, EntityDimensionFixed(0.98f, 0.98f))
        registerDimension(EyeOfEnderEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        registerDimension(EndCrystalEntity::class.java, EntityDimensionFixed(2.0f, 2.0f))
        registerDimension(BoatEntity::class.java, EntityDimensionFixed(1.5f, 0.6f))
        registerDimension(WitherEntity::class.java, EntityDimensionFixed(0.9f, 3.5f))
        registerDimension(EnderDragonEntity::class.java, EntityDimensionFixed(16.0f, 8.0f))
        registerDimension(ArrowEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        registerDimension(PlayerEntity::class.java, EntityDimensionFixed(0.6f, 1.8f))
        registerDimension(WolfEntity::class.java, EntityDimensionFixed(0.6f, 0.8f))
        registerDimension(VillagerEntity::class.java, EntityDimensionFixed(0.6f, 1.8f))
        registerDimension(SquidEntity::class.java, EntityDimensionFixed(0.95f, 0.95f))
        registerDimension(SheepEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        registerDimension(HorseEntity::class.java, EntityDimensionFixed(1.4f, 1.6f))
        registerDimension(CowEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        registerDimension(ChickenEntity::class.java, EntityDimensionFixed(0.4f, 0.7f))
        registerDimension(BatEntity::class.java, EntityDimensionFixed(0.5f, 0.9f))
        registerDimension(ZombieEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        registerDimension(WitchEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        registerDimension(SnowGolemEntity::class.java, EntityDimensionFixed(0.7f, 1.9f))
        registerDimension(SlimeEntity::class.java, EntityDimensionDynamic(object : ToAxisFunction<SlimeEntity> {
            override fun getAxisLength(t: Entity, pose: EntityPose) = 0.51000005f * (t as SlimeEntity).size.toFloat()
        }, object : ToAxisFunction<SlimeEntity> {
            override fun getAxisLength(t: Entity, pose: EntityPose) = 0.51000005f * (t as SlimeEntity).size.toFloat()
        }))
        registerDimension(SilverfishEntity::class.java, EntityDimensionFixed(0.4f, 0.3f))
        registerDimension(GuardianEntity::class.java, EntityDimensionFixed(0.85f, 0.85f))
        registerDimension(GiantEntity::class.java, EntityDimensionFixed(0.6f * 6.0f, 1.8f * 6.0f))
        registerDimension(GhastEntity::class.java, EntityDimensionFixed(4.0f, 4.0f))
        registerDimension(EndermiteEntity::class.java, EntityDimensionFixed(0.4f, 0.3f))
        registerDimension(EndermanEntity::class.java, EntityDimensionFixed(0.6f, 2.9f))
        registerDimension(CaveSpiderEntity::class.java, EntityDimensionFixed(0.7f, 0.5f))
        registerDimension(SpiderEntity::class.java, EntityDimensionFixed(1.4f, 0.9f))
        registerDimension(TntEntity::class.java, EntityDimensionFixed(0.98f, 0.98f))
        registerDimension(LeashKnotEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        registerDimension(AbstractDecorationEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        registerDimension(ProjectileEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        registerDimension(Entity::class.java, EntityDimensionFixed(0.6f, 1.8f))

        registerEyeHeight(ArrowEntity::class.java) {
            return@registerEyeHeight 0.0f
        }
        registerEyeHeight(PlayerEntity::class.java) { e ->
            var f = 1.62f
            if ((e as PlayerEntity).isSleeping) {
                f = 0.2f
            }
            if (e.isSneaking) {
                f -= 0.08f
            }
            f
        }
        registerEyeHeight(WolfEntity::class.java) { e -> 0.8f * e.height }
        registerEyeHeight(VillagerEntity::class.java) { e ->
            var f = 1.62f
            if ((e as VillagerEntity).isBaby) {
                f = (f.toDouble() - 0.81).toFloat()
            }
            f
        }
        registerEyeHeight(SquidEntity::class.java) { e -> 0.5f * e.height }
        registerEyeHeight(SheepEntity::class.java) { e -> 0.95f * e.height }
        registerEyeHeight(HorseEntity::class.java) { e -> e.height }
        registerEyeHeight(CowEntity::class.java) { e -> e.height }
        registerEyeHeight(ChickenEntity::class.java) { e -> e.height }
        registerEyeHeight(BatEntity::class.java) { e -> e.height / 2.0f }
        registerEyeHeight(ZombieEntity::class.java) { e ->
            var f = 1.74f
            if ((e as ZombieEntity).isBaby) {
                f = (f.toDouble() - 0.81).toFloat()
            }
            f
        }
        registerEyeHeight(WitchEntity::class.java) { 1.62f }
        registerEyeHeight(SnowGolemEntity::class.java) { 1.7f }
        registerEyeHeight(SlimeEntity::class.java) { e -> 0.625f * e.height }
        registerEyeHeight(WitherSkeletonEntity::class.java) { e -> e.height * 0.85f }
        registerEyeHeight(SkeletonEntity::class.java) { 1.74f }
        registerEyeHeight(SilverfishEntity::class.java) { 0.1f }
        registerEyeHeight(GuardianEntity::class.java) { e -> e.height * 0.5f }
        registerEyeHeight(GuardianEntity::class.java) { e -> e.height * 0.5f }
        registerEyeHeight(GiantEntity::class.java) { 10.440001f }
        registerEyeHeight(GhastEntity::class.java) { 2.6f }
        registerEyeHeight(EndermiteEntity::class.java) { 0.1f }
        registerEyeHeight(EndermanEntity::class.java) { 2.55f }
        registerEyeHeight(CaveSpiderEntity::class.java) { 0.45f }
        registerEyeHeight(SpiderEntity::class.java) { 0.65f }
        registerEyeHeight(TntEntity::class.java) { 0F }
        registerEyeHeight(ArmorStandEntity::class.java) { e -> e.height * if ((e as ArmorStandEntity).isBaby) 0.5f else 0.9f }
        registerEyeHeight(LeashKnotEntity::class.java) { -0.0625f }
        registerEyeHeight(Entity::class.java) { e -> e.height * 0.85f }
    }
}
