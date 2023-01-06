package de.florianmichael.clampclient.injection.instrumentation_1_8;

import com.google.common.collect.Lists;
import de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision.BlockModelEmulator;
import de.florianmichael.clampclient.injection.instrumentation_1_8.util.MathHelper_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.wrapper.BoxWrapper;
import de.florianmichael.clampclient.injection.instrumentation_1_8.wrapper.WorldBorderWrapper;
import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import java.util.List;

@SuppressWarnings("deprecation")
public class _1_8_PlayerAndLivingEntityMovementEmulation {

    private final LivingEntity original;

    public _1_8_PlayerAndLivingEntityMovementEmulation(final LivingEntity original) {
        this.original = original;
    }

    // Living Entity

    public void moveFlying(float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper_1_8.sqrt_float(f);
            if (f < 1.0F) f = 1.0F;

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper_1_8.sin(original.getYaw() * (float)Math.PI / 180.0F);
            float f2 = MathHelper_1_8.cos(original.getYaw() * (float)Math.PI / 180.0F);
            original.getVelocity().x += (double)(strafe * f2 - forward * f1);
            original.getVelocity().z += (double)(forward * f2 + strafe * f1);
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
                            BlockModelEmulator.getTransformerByBlock(iblockstate.getBlock()).onEntityCollidedWithBlock(original.world, blockpos2, iblockstate, original);
                        } catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                    }
                }
            }
        }
    }

    public void moveEntityWithHeading(float strafe, float forward) {
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

        if (this.isOnLadder())
        {
            float f6 = 0.15F;
            original.getVelocity().x = MathHelper.clamp(original.getVelocity().x, (double)(-f6), (double)f6);
            original.getVelocity().z = MathHelper.clamp(original.getVelocity().z, (double)(-f6), (double)f6);
            original.fallDistance = 0.0F;

            if (original.getVelocity().y < -0.15D) {
                original.getVelocity().y = -0.15D;
            }

            boolean flag = original.isSneaking() && original instanceof PlayerEntity;
            if (flag && original.getVelocity().y < 0.0D) {
                original.getVelocity().y = 0.0D;
            }
        }

        moveEntity(original.getVelocity().x, original.getVelocity().y, original.getVelocity().z);

        if (original.horizontalCollision && this.isOnLadder()) {
            original.getVelocity().y = 0.2D;
        }

        if (!original.world.isChunkLoaded(new BlockPos((int)original.getPos().x, 0, (int)original.getPos().z))) {
            if (original.getPos().y > 0)
                original.getVelocity().y = -0.1;
            else
                original.getVelocity().y = 0;
        } else original.getVelocity().y -= 0.08D;

        original.getVelocity().y *= 0.98F;
        original.getVelocity().x *= (double)f4;
        original.getVelocity().z *= (double)f4;

        original.updateLimbs((LivingEntity) (Object)original, original instanceof Flutterer);
    }

    private void resetPositionToBB() {
        original.getPos().x = (original.getBoundingBox().minX + original.getBoundingBox().maxX) / 2.0D;
        original.getPos().y = original.getBoundingBox().minY;
        original.getPos().z = (original.getBoundingBox().minZ + original.getBoundingBox().maxZ) / 2.0D;
    }

    public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
        double d0 = WorldBorderWrapper.minX(worldBorderIn);
        double d1 = WorldBorderWrapper.minZ(worldBorderIn);
        double d2 = WorldBorderWrapper.maxX(worldBorderIn);
        double d3 = WorldBorderWrapper.maxZ(worldBorderIn);

        if (((IEntity_Protocol)entityIn).protocolhack_isOutsideBorder()) {
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
        final boolean flag = ((IEntity_Protocol)entityIn).protocolhack_isOutsideBorder();
        final boolean flag1 = this.isInsideBorder(worldborder, entityIn);
        final BlockState iblockstate = Blocks.STONE.getDefaultState();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (world.isPosLoaded(k1, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        final BlockPos mutableBlockPos = new BlockPos(k1, i2, l1);

                        if (flag && flag1) {
                            ((IEntity_Protocol)entityIn).protocolhack_setOutsideBorder(false);
                        } else if (!flag && !flag1) {
                            ((IEntity_Protocol)entityIn).protocolhack_setOutsideBorder(true);
                        }

                        BlockState iblockstate1 = iblockstate;

                        if (worldborder.contains(mutableBlockPos) || !flag1) {
                            iblockstate1 = world.getBlockState(mutableBlockPos);
                        }

                        BlockModelEmulator.getTransformerByBlock(iblockstate1.getBlock()).addCollisionBoxesToList(world, mutableBlockPos, iblockstate1, bb, list, entityIn);
                    }
                }
            }
        }

        /*double d0 = 0.25D;
        List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list1.size(); ++j2)
        {
            if (entityIn.riddenByEntity != list1 && entityIn.ridingEntity != list1)
            {
                AxisAlignedBB axisalignedbb = ((Entity)list1.get(j2)).getCollisionBoundingBox();

                if (axisalignedbb != null && axisalignedbb.intersectsWith(bb))
                {
                    list.add(axisalignedbb);
                }

                axisalignedbb = entityIn.getCollisionBox((Entity)list1.get(j2));

                if (axisalignedbb != null && axisalignedbb.intersectsWith(bb))
                {
                    list.add(axisalignedbb);
                }
            }
        }*/

        return list;
    }

    private void moveEntity(double x, double y, double z) {
        if (original.noClip) {
            original.setBoundingBox(original.getBoundingBox().offset(x,y,z));
            this.resetPositionToBB();
        } else{
            original.world.getProfiler().push("move");
            double d0 = original.getPos().x;
            double d1 = original.getPos().y;
            double d2 = original.getPos().z;

            if (((IEntity_Protocol) original).protocolhack_isInWeb()) {
                ((IEntity_Protocol) original).protocolhack_setInWeb(false);
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                original.getVelocity().x = 0.0D;
                original.getVelocity().y = 0.0D;
                original.getVelocity().z = 0.0D;
            }

            double d3 = x;
            double d4 = y;
            double d5 = z;

            boolean flag = original.isOnGround() && original.isSneaking() && (Object) original instanceof PlayerEntity;

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

            if (original.stepHeight > 0.0F && flag1 && (d3 != x || d5 != z)) {
                double d11 = x;
                double d7 = y;
                double d8 = z;
                Box axisalignedbb3 = original.getBoundingBox();
                original.setBoundingBox(axisalignedbb);
                y = (double)original.stepHeight;
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
            }

            this.resetPositionToBB();

            original.horizontalCollision = d3 != x || d5 != z;
            original.verticalCollision = d4 != y;
            original.setOnGround(original.verticalCollision && d4 < 0.0D);
            original.field_36331 = original.isOnGround();
            // TODO this.collidedSoftly = this.isCollidedHorizontally || this.isCollidedVertically;

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

            this.updateFallState(y, original.isOnGround(), block1, blockpos);

            if (d3 != x) original.getVelocity().x = 0.0D;
            if (d5 != z) original.getVelocity().z = 0.0D;
            if (d4 != y) BlockModelEmulator.getTransformerByBlock(block1).onLanded(original.world, original);

            try {
                this.doBlockCollisions();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            boolean flag2 = original.isWet();

            original.world.getProfiler().pop();
        }
    }

    public void fall(float distance, float damageMultiplier) {
        if (!((PlayerEntity) (Object) original).getAbilities().allowFlying) {
            if (distance >= 2.0F) {
                //this.addStat(StatList.distanceFallenStat, (int)Math.round((double)distance * 100.0D));
            }
            superfall(distance, damageMultiplier);
        }
    }

    public void superfall(float distance, float damageMultiplier) {
        StatusEffectInstance potioneffect = original.getStatusEffect(StatusEffects.JUMP_BOOST);
        float f = potioneffect != null ? (float)(potioneffect.getAmplifier() + 1) : 0.0F;
        int i = MathHelper.ceil((distance - 3.0F - f) * damageMultiplier);

        /*if (i > 0)
        {
            this.playSound(this.getFallSoundString(i), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, (float)i);
            int j = MathHelper.floor_double(this.posX);
            int k = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int l = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();

            if (block.getMaterial() != Material.air)
            {
                Block.SoundType block$soundtype = block.stepSound;
                this.playSound(block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5F, block$soundtype.getFrequency() * 0.75F);
            }
        }*/
    }

    private void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
        if (onGroundIn) {
            if (original.fallDistance > 0.0F) {
                if (blockIn != null) {
                    BlockModelEmulator.getTransformerByBlock(blockIn).onFallenUpon(original.world, pos, original, original.fallDistance);
                } else {
                    fall(original.fallDistance, 1.0F);
                }
                original.fallDistance = 0.0F;
            }
        } else if (y < 0.0D) {
            original.fallDistance = (float)((double)original.fallDistance - y);
        }
    }

    public void movePlayerWithHeading(float strafe, float forward) {
        double d0 = original.getPos().x;
        double d1 = original.getPos().y;
        double d2 = original.getPos().z;

        final PlayerAbilities playerAbilities = ((PlayerEntity) original).getAbilities();

        if (playerAbilities.flying && !original.hasVehicle()) {
            double d3 = original.getVelocity().y;
            float f = original.airStrafingSpeed;
            original.airStrafingSpeed = playerAbilities.getFlySpeed() * (float)(original.isSprinting() ? 2 : 1);
            moveEntityWithHeading(strafe, forward);
            original.getVelocity().y = d3 * 0.6D;
            original.airStrafingSpeed = f;
        } else {
            moveEntityWithHeading(strafe, forward);
        }
//        this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    // Client Player Entity

    public void pushOutOfBlocks(double x, double y, double z) {
        if (original.noClip) return;

        final BlockPos blockpos = new BlockPos(x, y, z);
        double d0 = x - (double)blockpos.getX();
        double d1 = z - (double)blockpos.getZ();

        if (!this.protocolhack_isOpenBlockSpace(blockpos)) {
            int i = -1;
            double d2 = 9999.0D;

            if (this.protocolhack_isOpenBlockSpace(blockpos.west()) && d0 < d2) {
                d2 = d0;
                i = 0;
            }

            if (this.protocolhack_isOpenBlockSpace(blockpos.east()) && 1.0D - d0 < d2) {
                d2 = 1.0D - d0;
                i = 1;
            }

            if (this.protocolhack_isOpenBlockSpace(blockpos.north()) && d1 < d2) {
                d2 = d1;
                i = 4;
            }

            if (this.protocolhack_isOpenBlockSpace(blockpos.south()) && 1.0D - d1 < d2) {
                d2 = 1.0D - d1;
                i = 5;
            }

            float f = 0.1F;

            Vec3d motion = original.getVelocity();
            if (i == 0) motion.x = (double)(-f);
            if (i == 1) motion.x = (double)f;
            if (i == 4) motion.z = (double)(-f);
            if (i == 5) motion.z = (double)f;
            original.setVelocity(motion);
        }
    }

    private boolean protocolhack_isOpenBlockSpace(BlockPos pos) {
        return isNotNormalCube(pos) && isNotNormalCube(pos.up());
    }

    private boolean isNotNormalCube(final BlockPos pos) {
        final BlockState blockState = original.world.getBlockState(pos);
        return !blockState.isOpaque() || !blockState.isFullCube(original.world, pos) || blockState.emitsRedstonePower();
    }
}
