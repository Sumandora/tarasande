package de.florianmichael.clampclient.injection.instrumentation_1_8;

import com.google.common.collect.Lists;
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.BlockModelDefinition;
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.LegacyConstants_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.MathHelper_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.wrapper.BoxWrapper;
import de.florianmichael.clampclient.injection.instrumentation_1_8.wrapper.WorldBorderWrapper;
import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventCollisionShape;
import de.florianmichael.tarasande_protocol_hack.tarasande.module.ModuleNoWebSettingsKt;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.*;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleNoWeb;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSafeWalk;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoFall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author FlorianMichael as Jesse
 * <p>
 * This class emulates the complete 1.8 movement, this emulation is only used with <= 1.8, if the protocol version is
 * higher than > 1.8, then part differences in the code are fixed again, example for this is the Soulsand which
 * was changed from the 1.14, but is replaced with the 1.8 emulation.
 */
@SuppressWarnings({"deprecation", "RedundantCast", "UnnecessaryUnboxing", "PointlessArithmeticExpression", "DataFlowIssue", "DuplicatedCode", "ConstantValue"})
public class ClientPlayerMovement_1_8 {

    private final LivingEntity original;

    public ClientPlayerMovement_1_8(final LivingEntity original) {
        this.original = original;
    }

    // Living Entity

    private final Random rand = new Random();

    private float distanceWalkedModified;
    private float distanceWalkedOnStepModified;

    private int nextStepDistance;

    public void moveFlying(float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper_1_8.sqrt_float(f);
            if (f < 1.0F) f = 1.0F;

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(original.getYaw());
            EventDispatcher.INSTANCE.call(eventVelocityYaw);
            float f1 = MathHelper_1_8.sin(eventVelocityYaw.getYaw() * (float) Math.PI / 180.0F);
            float f2 = MathHelper_1_8.cos(eventVelocityYaw.getYaw() * (float) Math.PI / 180.0F);
            original.setVelocity(original.getVelocity().add((double) (strafe * f2 - forward * f1), 0.0D, (double) (forward * f2 + strafe * f1)));
        }
    }

    private float getAIMoveSpeed() {
        return (float) original.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    public boolean isOnLadder() {
        int i = MathHelper.floor(original.getPos().x);
        int j = MathHelper.floor(original.getBoundingBox().minY);
        int k = MathHelper.floor(original.getPos().z);
        Block block = original.world.getBlockState(new BlockPos(i, j, k)).getBlock();
        return (block == Blocks.LADDER || block == Blocks.VINE) && !original.isSpectator();
    }

    // Spigot TM, this should be in the BlockModelEmulator, but how do I look?
    public float getMaterialsFriction(Block material) {
        if (material == Blocks.SLIME_BLOCK) return (float) (0.8);
        if (material == Blocks.ICE || material == Blocks.PACKED_ICE) return (float) (0.98);

        return (float) (0.60);
    }

    private void doBlockCollisions() {
        BlockPos blockpos = new BlockPos(original.getBoundingBox().minX + 0.001D, original.getBoundingBox().minY + 0.001D, original.getBoundingBox().minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(original.getBoundingBox().maxX - 0.001D, original.getBoundingBox().maxY - 0.001D, original.getBoundingBox().maxZ - 0.001D);

        if (original.world.isRegionLoaded(blockpos, blockpos1)) {
            for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                        BlockPos blockpos2 = new BlockPos(i, j, k);
                        BlockState iblockstate = original.world.getBlockState(blockpos2);

                        try {
                            EventBlockCollision eventBlockCollision = new EventBlockCollision(iblockstate, blockpos2, original);
                            EventDispatcher.INSTANCE.call(eventBlockCollision);
                            BlockModelDefinition.getTransformerByBlock(iblockstate.getBlock()).onEntityCollidedWithBlock(original.world, blockpos2, iblockstate, original);
                        } catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                    }
                }
            }
        }
    }

    public boolean handleMaterialAcceleration(Box bb, Material materialIn, Entity entityIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.floor(bb.maxX + 1.0D);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.floor(bb.maxY + 1.0D);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.floor(bb.maxZ + 1.0D);

        if (!original.world.isRegionLoaded(i, k, i1, j, l, j1)) {
            return false;
        } else {
            boolean flag = false;
            Vec3d vec3 = new Vec3d(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        final BlockPos mutabeBlockPos = new BlockPos(k1, l1, i2);
                        final BlockState iblockstate = original.world.getBlockState(mutabeBlockPos);
                        final Block block = iblockstate.getBlock();

                        if (iblockstate.getMaterial() == materialIn) {

                            double d0 = (double) ((float) (l1 + 1) - LegacyConstants_1_8.getLiquidHeightPercent(((Integer) iblockstate.get(FluidBlock.LEVEL)).intValue()));

                            if ((double) l >= d0) {
                                flag = true;
                                vec3 = BlockModelDefinition.getTransformerByBlock(block).modifyAcceleration(original.world, mutabeBlockPos, entityIn, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.length() > 0.0D) {
                vec3 = vec3.normalize();
                double d1 = 0.014D;
                entityIn.setVelocity(entityIn.getVelocity().add(vec3.getX() * d1, vec3.getY() * d1, vec3.getZ() * d1));
            }

            return flag;
        }
    }

    protected void updateAITick() {
        original.setVelocity(original.getVelocity().add(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected void handleJumpLava() {
        original.setVelocity(original.getVelocity().add(0.0D, 0.03999999910593033D, 0.0D));
    }

    // This method doesn't exist in 1.8, it's a pseudo method for handling the jump cooldown
    public void func_c_1() {
        if (((ClientPlayerEntity) original).input.jumping) {
            if (((IEntity_Protocol) original).protocolhack_isInWater()) {
                this.updateAITick();
            } else if (this.isInLava()) {
                this.handleJumpLava();
            } else if (original.isOnGround() && original.jumpingCooldown == 0) {
                ((ClientPlayerEntity) original).jump();
                original.jumpingCooldown = 10;
            }
        } else {
            original.jumpingCooldown = 0;
        }
    }

    public void resetHeight() {
        if (original.isSpectator()) return;
        float f = MathHelper_1_8.sqrt_double(original.getVelocity().x * original.getVelocity().x * 0.20000000298023224D + original.getVelocity().y * original.getVelocity().y + original.getVelocity().z * original.getVelocity().z * 0.20000000298023224D) * 0.2F;
        if (f > 1.0F) f = 1.0F;

        original.playSound(SoundEvents.ENTITY_GENERIC_SPLASH /* USE GETTER */, f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        float f1 = (float) MathHelper.floor(original.getBoundingBox().minY);

        for (int i = 0; (float) i < 1.0F + LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 20.0F; ++i) {
            float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * LegacyConstants_1_8.PLAYER_MODEL_WIDTH;
            float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * LegacyConstants_1_8.PLAYER_MODEL_WIDTH;
            original.world.addParticle(ParticleTypes.BUBBLE, original.getPos().x + (double) f2, (double) (f1 + 1.0F), original.getPos().z + (double) f3, original.getVelocity().x, original.getVelocity().y - (double) (this.rand.nextFloat() * 0.2F), original.getVelocity().z);
        }

        for (int j = 0; (float) j < 1.0F + LegacyConstants_1_8.PLAYER_MODEL_WIDTH * 20.0F; ++j) {
            float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * LegacyConstants_1_8.PLAYER_MODEL_WIDTH;
            float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * LegacyConstants_1_8.PLAYER_MODEL_WIDTH;
            original.world.addParticle(ParticleTypes.SPLASH, original.getPos().x + (double) f4, (double) (f1 + 1.0F), original.getPos().z + (double) f5, original.getVelocity().x, original.getVelocity().y, original.getVelocity().z);
        }
    }

    public void handleWaterMovement() {
        if (handleMaterialAcceleration(original.getBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.WATER, original)) {
            if (!((IEntity_Protocol) original).protocolhack_isInWater() && !original.firstUpdate) {
                this.resetHeight();
            }

            original.fallDistance = 0.0F;
            ((IEntity_Protocol) original).protocolhack_setInWater(true);
            // 1.8 Clients reset fire ticks here, force when?
        } else {
            ((IEntity_Protocol) original).protocolhack_setInWater(false);
        }

        ((IEntity_Protocol) original).protocolhack_isInWater();
    }

    public void moveEntityWithHeading(float strafe, float forward) {
        if (!isInLava() || ((PlayerEntity) original).getAbilities().flying) {
            if (!((IEntity_Protocol) original).protocolhack_isInWater() || ((PlayerEntity) original).getAbilities().flying) {
                float f4 = 0.91F;

                if (original.isOnGround()) {
                    f4 = getMaterialsFriction(original.world.getBlockState(new BlockPos(MathHelper.floor(original.getPos().x), MathHelper.floor(original.getBoundingBox().minY) - 1, MathHelper.floor(original.getPos().z))).getBlock()) * 0.91F;
                }

                float f = 0.16277136F / (f4 * f4 * f4);
                float f5;

                if (original.isOnGround()) {
                    f5 = getAIMoveSpeed() * f;
                } else {
                    f5 = original.airStrafingSpeed;
                }
                this.moveFlying(strafe, forward, f5);
                f4 = 0.91F;

                if (original.isOnGround()) {
                    f4 = getMaterialsFriction(original.world.getBlockState(new BlockPos(MathHelper.floor(original.getPos().x), MathHelper.floor(original.getBoundingBox().minY) - 1, MathHelper.floor(original.getPos().z))).getBlock()) * 0.91F;
                }

                if (this.isOnLadder()) {
                    float f6 = 0.15F;
                    original.setVelocity(new Vec3d(MathHelper.clamp(original.getVelocity().x, (double) (-f6), (double) f6), original.getVelocity().y, MathHelper.clamp(original.getVelocity().z, (double) (-f6), (double) f6)));
                    original.fallDistance = 0.0F;

                    if (original.getVelocity().y < -0.15D) {
                        original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, -0.15D));
                    }

                    boolean flag = original.isSneaking() && original instanceof PlayerEntity;
                    if (flag && original.getVelocity().y < 0.0D) {
                        original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, 0.0D));
                    }
                }

                moveEntity(original.getVelocity().x, original.getVelocity().y, original.getVelocity().z);

                if (original.horizontalCollision && this.isOnLadder()) {
                    original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, 0.2D));
                }

                if (!original.world.isChunkLoaded(new BlockPos((int) original.getPos().x, 0, (int) original.getPos().z))) {
                    if (original.getPos().y > 0)
                        original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, -0.1));
                    else original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, 0));
                } else original.setVelocity(original.getVelocity().subtract(0.0D, 0.08D, 0.0D));

                original.setVelocity(original.getVelocity().multiply((double) f4, 0.98F, (double) f4));
            } else {
                double d0 = original.getPos().y;
                float f1 = 0.8F;
                float f2 = 0.02F;
                float f3 = (float) EnchantmentHelper.getDepthStrider(original);

                if (f3 > 3.0F) {
                    f3 = 3.0F;
                }

                if (!original.isOnGround()) {
                    f3 *= 0.5F;
                }

                if (f3 > 0.0F) {
                    f1 += (0.54600006F - f1) * f3 / 3.0F;
                    f2 += (this.getAIMoveSpeed() * 1.0F - f2) * f3 / 3.0F;
                }

                this.moveFlying(strafe, forward, f2);
                this.moveEntity(original.getVelocity().x, original.getVelocity().y, original.getVelocity().z);
                original.setVelocity(original.getVelocity().multiply((double) f1, 0.800000011920929D, (double) f1));
                original.setVelocity(original.getVelocity().subtract(0.0D, 0.02D, 0.0D));

                if (original.horizontalCollision && this.isOffsetPositionInLiquid(original.getVelocity().x, original.getVelocity().y + 0.6000000238418579D - original.getPos().y + d0, original.getVelocity().z)) {
                    original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, 0.30000001192092896D));
                }
            }
        } else {
            double d1 = original.getPos().y;
            this.moveFlying(strafe, forward, 0.02F);
            this.moveEntity(original.getVelocity().x, original.getVelocity().y, original.getVelocity().z);
            original.setVelocity(original.getVelocity().multiply(0.5D, 0.5D, 0.5D));
            original.setVelocity(original.getVelocity().subtract(0.0D, 0.02D, 0.0D));

            if (original.horizontalCollision && this.isOffsetPositionInLiquid(original.getVelocity().x, original.getVelocity().y + 0.6000000238418579D - original.getPos().y + d1, original.getVelocity().z)) {
                original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, 0.30000001192092896D));
            }
        }
        original.updateLimbs((LivingEntity) (Object) original, original instanceof Flutterer);
    }

    public boolean isAnyLiquid(final World world, Box bb) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.floor(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.floor(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.floor(bb.maxZ);

        for (int k1 = i; k1 <= j; ++k1) {
            for (int l1 = k; l1 <= l; ++l1) {
                for (int i2 = i1; i2 <= j1; ++i2) {
                    final BlockPos mutableBlockPos = new BlockPos(k1, l1, i2);
                    final BlockState state = world.getBlockState(mutableBlockPos);

                    if (state.getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        return isLiquidPresentInAABB(original.getBoundingBox().offset(x, y, z));
    }

    public boolean isLiquidPresentInAABB(Box bb) {
        return getCollidingBoundingBoxes(original.world, original, bb).isEmpty() && !isAnyLiquid(original.world, bb);
    }

    private void resetPositionToBB() {
        original.setPos(
                (original.getBoundingBox().minX + original.getBoundingBox().maxX) / 2.0D,
                original.getBoundingBox().minY,
                (original.getBoundingBox().minZ + original.getBoundingBox().maxZ) / 2.0D
        );
    }

    public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
        double d0 = WorldBorderWrapper.minX(worldBorderIn);
        double d1 = WorldBorderWrapper.minZ(worldBorderIn);
        double d2 = WorldBorderWrapper.maxX(worldBorderIn);
        double d3 = WorldBorderWrapper.maxZ(worldBorderIn);

        if (((IEntity_Protocol) entityIn).protocolhack_isOutsideBorder()) {
            ++d0;
            ++d1;
            --d2;
            --d3;
        } else {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }

        return entityIn.getPos().x > d0 && entityIn.getPos().x < d2 && entityIn.getPos().z > d1 && entityIn.getPos().z < d3;
    }

    public List<Box> getCollidingBoundingBoxes(World world, Entity entityIn, Box bb) {
        final List<Box> list = Lists.newArrayList();

        final int i = MathHelper.floor(bb.minX);
        final int j = MathHelper.floor(bb.maxX + 1.0D);
        final int k = MathHelper.floor(bb.minY);
        final int l = MathHelper.floor(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor(bb.minZ);
        final int j1 = MathHelper.floor(bb.maxZ + 1.0D);

        final WorldBorder worldborder = world.getWorldBorder();
        final boolean flag = ((IEntity_Protocol) entityIn).protocolhack_isOutsideBorder();
        final boolean flag1 = this.isInsideBorder(worldborder, entityIn);
        final BlockState iblockstate = Blocks.STONE.getDefaultState();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (world.isPosLoaded(k1, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        final BlockPos mutableBlockPos = new BlockPos(k1, i2, l1);

                        if (flag && flag1) {
                            ((IEntity_Protocol) entityIn).protocolhack_setOutsideBorder(false);
                        } else if (!flag && !flag1) {
                            ((IEntity_Protocol) entityIn).protocolhack_setOutsideBorder(true);
                        }

                        BlockState iblockstate1 = iblockstate;

                        if (worldborder.contains(mutableBlockPos) || !flag1) {
                            iblockstate1 = world.getBlockState(mutableBlockPos);
                        }

                        ArrayList<Box> shapes = new ArrayList<>();
                        BlockModelDefinition.getTransformerByBlock(iblockstate1.getBlock()).addCollisionBoxesToList(world, mutableBlockPos, iblockstate1, bb, shapes, entityIn);

                        if(true /*Am I supposed to be unlegit?*/) {
                            // FULL LOTTO INCOMING $$$$
                            VoxelShape shape = VoxelShapes.empty();
                            { // Generate the VoxelShape
                                ArrayList<Box> shapes2 = new ArrayList<>(shapes);
                                if(!shapes2.isEmpty()) {
                                    shape = VoxelShapes.cuboid(shapes2.get(0).offset(-mutableBlockPos.getX(), -mutableBlockPos.getY(), -mutableBlockPos.getZ()));
                                    shapes2.remove(0);

                                    while(!shapes2.isEmpty()) {
                                        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(shapes2.get(0).offset(-mutableBlockPos.getX(), -mutableBlockPos.getY(), -mutableBlockPos.getZ())));
                                        shapes2.remove(0);
                                    }
                                }
                            }

                            EventCollisionShape eventCollisionShape = new EventCollisionShape(mutableBlockPos, shape);
                            EventDispatcher.INSTANCE.call(eventCollisionShape);

                            shape = eventCollisionShape.getCollisionShape().offset(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());

                            if(((IEventCollisionShape) (Object) eventCollisionShape).isDirty()) {
                                if(!shape.isEmpty() && shape != VoxelShapes.empty()) {
                                    list.addAll(shape.getBoundingBoxes());
                                }
                            } else {
                                list.addAll(shapes);
                            }
                        } else {
                            list.addAll(shapes);
                        }
                    }
                }
            }
        }
        return list;
    }

    private void moveEntity(double x, double y, double z) {
        EventMovement eventMovement = new EventMovement(original, new Vec3d(x, y, z));
        EventDispatcher.INSTANCE.call(eventMovement);
        if(eventMovement.getDirty()) {
            original.setVelocity(eventMovement.getVelocity());
            x = eventMovement.getVelocity().x;
            y = eventMovement.getVelocity().y;
            z = eventMovement.getVelocity().z;
        }

        if (original.noClip) {
            original.setBoundingBox(original.getBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        } else {
            original.world.getProfiler().push("move");
            double d0 = original.getPos().x;
            double d1 = original.getPos().y;
            double d2 = original.getPos().z;

            if (((IEntity_Protocol) original).protocolhack_isInWeb()) {
                ((IEntity_Protocol) original).protocolhack_setInWeb(false);
                ModuleNoWeb moduleNoWeb = ManagerModule.INSTANCE.get(ModuleNoWeb.class);
                if(moduleNoWeb.getEnabled().getValue()) {
                    x *= moduleNoWeb.getHorizontalSlowdown().getValue();
                    y *= moduleNoWeb.getVerticalSlowdown().getValue();
                    z *= moduleNoWeb.getHorizontalSlowdown().getValue();
                    if(!ModuleNoWebSettingsKt.removeVelocityReset.getValue()) {
                        original.setVelocity(new Vec3d(0.0D, 0.0D, 0.0D));
                    }
                } else {
                    x *= 0.25D;
                    y *= 0.05000000074505806D;
                    z *= 0.25D;
                    original.setVelocity(new Vec3d(0.0D, 0.0D, 0.0D));
                }
            }

            double d3 = x;
            double d4 = y;
            double d5 = z;

            ModuleSafeWalk moduleSafeWalk = ManagerModule.INSTANCE.get(ModuleSafeWalk.class);

            boolean flag = original.isOnGround() && (original.isSneaking() || (moduleSafeWalk.getEnabled().getValue() && !moduleSafeWalk.getSneak().getValue())) && (Object) original instanceof PlayerEntity;

            if (flag) {
                double d6;
                for (d6 = 0.05D; x != 0.0D && getCollidingBoundingBoxes(original.world, original, original.getBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty(); d3 = x) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }
                }

                for (; z != 0.0D && getCollidingBoundingBoxes(original.world, original, original.getBoundingBox().offset(0.0D, -1.0D, z)).isEmpty(); d5 = z) {
                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }

                for (; x != 0.0D && z != 0.0D && getCollidingBoundingBoxes(original.world, original, original.getBoundingBox().offset(x, -1.0D, z)).isEmpty(); d5 = z) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }

                    d3 = x;

                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }
            }

            List<Box> list1 = getCollidingBoundingBoxes(original.world, original, original.getBoundingBox().stretch(x, y, z));
            Box axisalignedbb = original.getBoundingBox();

            for (Box axisalignedbb1 : list1) {
                y = BoxWrapper.calculateYOffset(axisalignedbb1, original.getBoundingBox(), y);
            }

            original.setBoundingBox(original.getBoundingBox().offset(0.0D, y, 0.0D));
            boolean flag1 = original.isOnGround() || d4 != y && d4 < 0.0D;

            for (Box axisalignedbb2 : list1) {
                x = BoxWrapper.calculateXOffset(axisalignedbb2, original.getBoundingBox(), x);
            }

            original.setBoundingBox(original.getBoundingBox().offset(x, 0.0D, 0.0D));

            for (Box axisalignedbb13 : list1) {
                z = BoxWrapper.calculateZOffset(axisalignedbb13, original.getBoundingBox(), z);
            }

            original.setBoundingBox(original.getBoundingBox().offset(0.0D, 0.0D, z));

            EventStep eventStep = new EventStep(original.stepHeight, EventStep.State.PRE);
            EventDispatcher.INSTANCE.call(eventStep);
            if (eventStep.getStepHeight() > 0.0F && flag1 && (d3 != x || d5 != z)) {
                double d11 = x;
                double d7 = y;
                double d8 = z;
                Box axisalignedbb3 = original.getBoundingBox();
                original.setBoundingBox(axisalignedbb);

                double posY = original.getBoundingBox().minY;

                y = (double) eventStep.getStepHeight();
                List<Box> list = getCollidingBoundingBoxes(original.world, original, original.getBoundingBox().stretch(d3, y, d5));
                Box axisalignedbb4 = original.getBoundingBox();
                Box axisalignedbb5 = axisalignedbb4.stretch(d3, 0.0D, d5);
                double d9 = y;

                for (Box axisalignedbb6 : list) {
                    d9 = BoxWrapper.calculateYOffset(axisalignedbb6, axisalignedbb5, d9);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, d9, 0.0D);
                double d15 = d3;

                for (Box axisalignedbb7 : list) {
                    d15 = BoxWrapper.calculateXOffset(axisalignedbb7, axisalignedbb4, d15);
                }

                axisalignedbb4 = axisalignedbb4.offset(d15, 0.0D, 0.0D);
                double d16 = d5;

                for (Box axisalignedbb8 : list) {
                    d16 = BoxWrapper.calculateZOffset(axisalignedbb8, axisalignedbb4, d16);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d16);
                Box axisalignedbb14 = original.getBoundingBox();
                double d17 = y;

                for (Box axisalignedbb9 : list) {
                    d17 = BoxWrapper.calculateYOffset(axisalignedbb9, axisalignedbb14, d17);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
                double d18 = d3;

                for (Box axisalignedbb10 : list) {
                    d18 = BoxWrapper.calculateXOffset(axisalignedbb10, axisalignedbb14, d18);
                }

                axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
                double d19 = d5;

                for (Box axisalignedbb11 : list) {
                    d19 = BoxWrapper.calculateZOffset(axisalignedbb11, axisalignedbb14, d19);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10) {
                    x = d15;
                    z = d16;
                    y = -d9;
                    original.setBoundingBox(axisalignedbb4);
                } else {
                    x = d18;
                    z = d19;
                    y = -d17;
                    original.setBoundingBox(axisalignedbb14);
                }

                for (Box axisalignedbb12 : list) {
                    y = BoxWrapper.calculateYOffset(axisalignedbb12, original.getBoundingBox(), y);
                }

                original.setBoundingBox(original.getBoundingBox().offset(0.0D, y, 0.0D));

                if (d11 * d11 + d8 * d8 >= x * x + z * z) {
                    x = d11;
                    y = d7;
                    z = d8;
                    original.setBoundingBox(axisalignedbb3);
                }

                eventStep = new EventStep((float) (original.getBoundingBox().minY - posY), EventStep.State.POST);
                EventDispatcher.INSTANCE.call(eventStep);
            }

            this.resetPositionToBB();

            original.horizontalCollision = d3 != x || d5 != z;
            original.verticalCollision = d4 != y;
            original.setOnGround(original.verticalCollision && d4 < 0.0D);
            original.field_36331 = original.isOnGround();
            // this.collidedSoftly doesn't exist anymore
            int i = MathHelper.floor(original.getPos().x);
            int j = MathHelper.floor(original.getPos().y - 0.20000000298023224D);
            int k = MathHelper.floor(original.getPos().z);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = original.world.getBlockState(blockpos).getBlock();

            if (block1 == Blocks.AIR) {
                Block block = original.world.getBlockState(blockpos.down()).getBlock();

                if (block instanceof FenceBlock || block instanceof WallBlock || block instanceof FenceGateBlock) {
                    block1 = block;
                    blockpos = blockpos.down();
                }
            }

            final BlockModelDefinition.BlockModel blockModel = BlockModelDefinition.getTransformerByBlock(block1);

            this.updateFallState(y, original.isOnGround(), block1, blockModel, blockpos);

            if (d3 != x) original.setVelocity(original.getVelocity().withAxis(Direction.Axis.X, 0.0D));
            if (d5 != z) original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Z, 0.0D));

            if (d4 != y) blockModel.onLanded(original.world, original);

            if (this.canTriggerWalking() && !flag && !original.hasVehicle()) {
                double d12 = original.getPos().x - d0;
                double d13 = original.getPos().y - d1;
                double d14 = original.getPos().z - d2;

                if (block1 != Blocks.LADDER) d13 = 0.0D;

                if (block1 != null && original.isOnGround()) {
                    blockModel.onEntityCollidedWithBlock(original.world, blockpos, original);
                }

                distanceWalkedModified = (float) ((double) this.distanceWalkedModified + (double) MathHelper_1_8.sqrt_double(d12 * d12 + d14 * d14) * 0.6D);
                distanceWalkedOnStepModified = (float) ((double) this.distanceWalkedOnStepModified + (double) MathHelper_1_8.sqrt_double(d12 * d12 + d13 * d13 + d14 * d14) * 0.6D);

                original.prevHorizontalSpeed = original.horizontalSpeed;
                original.horizontalSpeed = distanceWalkedModified;
                original.distanceTraveled = distanceWalkedModified;

                if (this.distanceWalkedOnStepModified > (float) this.nextStepDistance && block1 != Blocks.AIR) {
                    this.nextStepDistance = (int) this.distanceWalkedOnStepModified + 1;

                    if (((IEntity_Protocol) original).protocolhack_isInWater()) {
                        float f = MathHelper_1_8.sqrt_double(original.getVelocity().x * original.getVelocity().x * 0.20000000298023224D + original.getVelocity().y * original.getVelocity().y + original.getVelocity().z * original.getVelocity().z * 0.20000000298023224D) * 0.35F;
                        if (f > 1.0F) f = 1.0F;

                        original.playSound(SoundEvents.ENTITY_GENERIC_SWIM /* GETTER */, f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    original.playStepSound(blockpos, block1.getDefaultState());
                }
            }

            try {
                this.doBlockCollisions();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            original.world.getProfiler().pop();
        }
    }

    public void superFall(float distance, float damageMultiplier) {
        final StatusEffectInstance potionEffect = original.getStatusEffect(StatusEffects.JUMP_BOOST);
        float f = potionEffect != null ? (float) (potionEffect.getAmplifier() + 1) : 0.0F;
        int i = MathHelper.ceil((distance - 3.0F - f) * damageMultiplier);

        if (i > 0) {
            original.playSound(original.getFallSound(i), 1.0F, 1.0F);
            // attackEntityFrom is serverside-only and not relevant for us

            int x = MathHelper.floor(original.getX());
            int y = MathHelper.floor(original.getY() - 0.20000000298023224);
            int z = MathHelper.floor(original.getZ());
            BlockState blockState = original.world.getBlockState(new BlockPos(x, y, z));
            if (!blockState.isAir()) {
                BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                original.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
            }
        }
    }

    private void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockModelDefinition.BlockModel blockModel, BlockPos pos) {
        if (!((IEntity_Protocol) original).protocolhack_isInWater()) {
            this.handleWaterMovement();
        }

        if(onGroundIn) {
            ModuleNoFall moduleNoFall = ManagerModule.INSTANCE.get(ModuleNoFall.class);
            if(moduleNoFall.getEnabled().getValue() && moduleNoFall.getMode().isSelected(0) && moduleNoFall.getGroundSpoofMode().isSelected(1))
                return;
        }

        if (onGroundIn) {
            if (original.fallDistance > 0.0F) {
                if (blockIn != null) {
                    blockModel.onFallenUpon(original.world, pos, original, original.fallDistance);
                } else {
                    fall(original.fallDistance, 1.0F);
                }
                original.fallDistance = 0.0F;
            }
        } else if (y < 0.0D) {
            original.fallDistance = (float) ((double) original.fallDistance - y);
        }
    }

    public void movePlayerWithHeading(float strafe, float forward) {
        final PlayerAbilities playerAbilities = ((PlayerEntity) original).getAbilities();

        if (playerAbilities.flying && !original.hasVehicle()) {
            double d3 = original.getVelocity().y;
            float f = original.airStrafingSpeed;
            original.airStrafingSpeed = playerAbilities.getFlySpeed() * (float) (original.isSprinting() ? 2 : 1);
            moveEntityWithHeading(strafe, forward);
            original.setVelocity(original.getVelocity().withAxis(Direction.Axis.Y, d3 * 0.6D));
            original.airStrafingSpeed = f;
        } else {
            moveEntityWithHeading(strafe, forward);
        }
    }

    public boolean isMaterialInBB(Box bb, Material materialIn) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.floor(bb.maxX + 1.0D);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.floor(bb.maxY + 1.0D);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.floor(bb.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final BlockPos mutableBlockPos = new BlockPos(k1, l1, i2);
                    if (original.world.getBlockState(mutableBlockPos).getBlock().getDefaultState().getMaterial() == materialIn) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isInLava() {
        return isMaterialInBB(original.getBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
    }

    // Client Player Entity

    public boolean canTriggerWalking() {
        return !((PlayerEntity) original).getAbilities().flying;
    }

    public void fall(float distance, float damageMultiplier) {
        if (!((PlayerEntity) original).getAbilities().allowFlying) {
            superFall(distance, damageMultiplier);
        }
    }

    public void pushOutOfBlocks(double x, double y, double z) {
        if (original.noClip) return;

        final BlockPos blockpos = new BlockPos(x, y, z);
        double d0 = x - (double) blockpos.getX();
        double d1 = z - (double) blockpos.getZ();

        if (!this.isOpenBlockSpace(blockpos)) {
            int i = -1;
            double d2 = 9999.0D;

            if (this.isOpenBlockSpace(blockpos.west()) && d0 < d2) {
                d2 = d0;
                i = 0;
            }

            if (this.isOpenBlockSpace(blockpos.east()) && 1.0D - d0 < d2) {
                d2 = 1.0D - d0;
                i = 1;
            }

            if (this.isOpenBlockSpace(blockpos.north()) && d1 < d2) {
                d2 = d1;
                i = 4;
            }

            if (this.isOpenBlockSpace(blockpos.south()) && 1.0D - d1 < d2) {
                d2 = 1.0D - d1;
                i = 5;
            }

            float f = 0.1F;

            original.setVelocity(original.getVelocity().add(i == 0 ? (double) (-f) : i == 1 ? (double) f : 0, 0, i == 4 ? (double) (-f) : i == 5 ? (double) f : 0));
        }
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return isNotNormalCube(pos) && isNotNormalCube(pos.up());
    }

    private boolean isNotNormalCube(final BlockPos pos) {
        final BlockState blockState = original.world.getBlockState(pos);
        return !blockState.isOpaque() || !blockState.isFullCube(original.world, pos) || blockState.emitsRedstonePower();
    }
}
