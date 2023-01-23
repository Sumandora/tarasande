package de.florianmichael.tarasande_protocol_hack.definition.entitydimension.impl

import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.ToAxisFunction
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

    override fun initDimensions() {
        register(ExperienceOrbEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        register(WitherSkeletonEntity::class.java, EntityDimensionFixed(0.72f, 2.535f))
        register(AbstractSkeletonEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        register(ArmorStandEntity::class.java, EntityDimensionFixed(0.5f, 1.975f))
        register(OcelotEntity::class.java, EntityDimensionFixed(0.6f, 0.7f))
        register(MooshroomEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        register(RabbitEntity::class.java, EntityDimensionFixed(0.6f, 0.7f))
        register(PigEntity::class.java, EntityDimensionFixed(0.9f, 0.9f))
        register(WitherSkullEntity::class.java, EntityDimensionFixed(0.3125f, 0.3125f))
        register(SmallFireballEntity::class.java, EntityDimensionFixed(0.3125f, 0.3125f))
        register(AbstractFireballEntity::class.java, EntityDimensionFixed(1.0f, 1.0f))
        register(IronGolemEntity::class.java, EntityDimensionFixed(1.4f, 2.9f))
        register(MinecartEntity::class.java, EntityDimensionFixed(0.98f, 0.7f))
        register(ItemEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        register(FireworkRocketEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        register(FallingBlockEntity::class.java, EntityDimensionFixed(0.98f, 0.98f))
        register(EyeOfEnderEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        register(EndCrystalEntity::class.java, EntityDimensionFixed(2.0f, 2.0f))
        register(BoatEntity::class.java, EntityDimensionFixed(1.5f, 0.6f))
        register(WitherEntity::class.java, EntityDimensionFixed(0.9f, 3.5f))
        register(EnderDragonEntity::class.java, EntityDimensionFixed(16.0f, 8.0f))
        register(ArrowEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        register(PlayerEntity::class.java, EntityDimensionFixed(0.6f, 1.8f))
        register(WolfEntity::class.java, EntityDimensionFixed(0.6f, 0.8f))
        register(VillagerEntity::class.java, EntityDimensionFixed(0.6f, 1.8f))
        register(SquidEntity::class.java, EntityDimensionFixed(0.95f, 0.95f))
        register(SheepEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        register(HorseEntity::class.java, EntityDimensionFixed(1.4f, 1.6f))
        register(CowEntity::class.java, EntityDimensionFixed(0.9f, 1.3f))
        register(ChickenEntity::class.java, EntityDimensionFixed(0.4f, 0.7f))
        register(BatEntity::class.java, EntityDimensionFixed(0.5f, 0.9f))
        register(ZombieEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        register(WitchEntity::class.java, EntityDimensionFixed(0.6f, 1.95f))
        register(SnowGolemEntity::class.java, EntityDimensionFixed(0.7f, 1.9f))
        register(SlimeEntity::class.java, EntityDimensionDynamic(object : ToAxisFunction<SlimeEntity> {
            override fun getAxisLength(t: Entity, pose: EntityPose) = 0.51000005f * (t as SlimeEntity).size.toFloat()
        }, object : ToAxisFunction<SlimeEntity> {
            override fun getAxisLength(t: Entity, pose: EntityPose) = 0.51000005f * (t as SlimeEntity).size.toFloat()
        }))
        register(SilverfishEntity::class.java, EntityDimensionFixed(0.4f, 0.3f))
        register(GuardianEntity::class.java, EntityDimensionFixed(0.85f, 0.85f))
        register(GiantEntity::class.java, EntityDimensionFixed(0.6f * 6.0f, 1.8f * 6.0f))
        register(GhastEntity::class.java, EntityDimensionFixed(4.0f, 4.0f))
        register(EndermiteEntity::class.java, EntityDimensionFixed(0.4f, 0.3f))
        register(EndermanEntity::class.java, EntityDimensionFixed(0.6f, 2.9f))
        register(CaveSpiderEntity::class.java, EntityDimensionFixed(0.7f, 0.5f))
        register(SpiderEntity::class.java, EntityDimensionFixed(1.4f, 0.9f))
        register(TntEntity::class.java, EntityDimensionFixed(0.98f, 0.98f))
        register(LeashKnotEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        register(AbstractDecorationEntity::class.java, EntityDimensionFixed(0.5f, 0.5f))
        register(ProjectileEntity::class.java, EntityDimensionFixed(0.25f, 0.25f))
        register(Entity::class.java, EntityDimensionFixed(0.6f, 1.8f))
    }
}
