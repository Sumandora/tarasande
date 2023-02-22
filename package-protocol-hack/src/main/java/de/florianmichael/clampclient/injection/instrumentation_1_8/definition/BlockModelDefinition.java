package de.florianmichael.clampclient.injection.instrumentation_1_8.definition;

import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventVelocityMultiplier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FlorianMichael as Jesse
 * <p>
 * This class emulates the movement-important parts of all blocks in 1.8, the visual part (outline shape) is fixed via Mixins in the blocks themselves
 * <p>
 * Notes:
 * - setBlockBoundsForItemRender is actually part from the Block class but not important for us, since it's the rendering (outline shape)
 */
@SuppressWarnings("ALL")
public class BlockModelDefinition {

    public static final Map<Block, BlockModel> blockModelTransformer = new HashMap<>();

    static {
        // Base
        blockModelTransformer.put(Blocks.STONE, new DefaultBlockModel()); // Every Block which doesn't have any custom model data

        for (Block block : CarpetBlockModel.CARPETS_IN_1_8) blockModelTransformer.put(block, new CarpetBlockModel());
        for (Block block : SlabBlockModel.SLABS_IN_1_8) blockModelTransformer.put(block, new SlabBlockModel());
        for (Block block : StairBlockModel.STAIRS_IN_1_8) blockModelTransformer.put(block, new StairBlockModel());
        for (Block block : FenceBlockModel.FENCES_IN_1_8) blockModelTransformer.put(block, new FenceBlockModel());
        for (Block block : PaneBlockModel.PANES_IN_1_8) blockModelTransformer.put(block, new PaneBlockModel());
        for (Block block : DoorBlockModel.DOORS_IN_1_8) blockModelTransformer.put(block, new DoorBlockModel());
        for (Block block : FenceGateBlockModel.FENCE_GATES_IN_1_8) blockModelTransformer.put(block, new FenceGateBlockModel());
        for (Block block : SkullBlockModel.SKULLS_IN_1_8) blockModelTransformer.put(block, new SkullBlockModel());
        for (Block block : SkullBlockModel.PseudoSkullWallBlockModel.WALL_SKULLS_IN_1_8) blockModelTransformer.put(block, new SkullBlockModel.PseudoSkullWallBlockModel());
        for (Block block : TrapdoorBlockModel.TRAPDOORS_IN_1_8) blockModelTransformer.put(block, new TrapdoorBlockModel());
        for (Block block : WallBlockModel.WALLS_IN_1_8) blockModelTransformer.put(block, new WallBlockModel());

        blockModelTransformer.put(Blocks.COBWEB, new CobwebBlockModel());
        blockModelTransformer.put(Blocks.SLIME_BLOCK, new SlimeBlockModel());
        blockModelTransformer.put(Blocks.WATER, new LiquidBlockModel());
        blockModelTransformer.put(Blocks.LADDER, new LadderBlockModel());
        blockModelTransformer.put(Blocks.BREWING_STAND, new BrewingStandBlockModel());
        blockModelTransformer.put(Blocks.CAULDRON, new CauldronBlockModel());
        blockModelTransformer.put(Blocks.END_PORTAL_FRAME, new EndPortalFrameBlockModel());
        blockModelTransformer.put(Blocks.HOPPER, new HopperBlockModel());
        blockModelTransformer.put(Blocks.LILY_PAD, new LilyPadBlockModel());
        blockModelTransformer.put(Blocks.PISTON, new PistonBaseBlockModel());
        blockModelTransformer.put(Blocks.PISTON_HEAD, new PistonExtensionBlockModel());
        blockModelTransformer.put(Blocks.CACTUS, new CactusBlockModel());
        blockModelTransformer.put(Blocks.CAKE, new CakeBlockModel());
        blockModelTransformer.put(Blocks.COCOA, new CocoaBlockModel());
        blockModelTransformer.put(Blocks.FARMLAND, new FarmlandBlockModel());
        blockModelTransformer.put(Blocks.SNOW, new SnowBlockModel());
        blockModelTransformer.put(Blocks.SOUL_SAND, new SoulSandBlockModel());
        blockModelTransformer.put(Blocks.ANVIL, new AnvilBlockModel());
        blockModelTransformer.put(Blocks.RED_BED, new BedBlockModel());
        blockModelTransformer.put(Blocks.DAYLIGHT_DETECTOR, new DaylightDetectorBlockModel());
        blockModelTransformer.put(Blocks.DRAGON_EGG, new DragonEggBlockModel());
        blockModelTransformer.put(Blocks.ENCHANTING_TABLE, new EnchantmentTableBlockModel());
        blockModelTransformer.put(Blocks.ENDER_CHEST, new EnderChestBlockModel());

        blockModelTransformer.put(Blocks.COMPARATOR, new RedstoneComparatorAndRepeaterBlockModel());
        blockModelTransformer.put(Blocks.REPEATER, new RedstoneComparatorAndRepeaterBlockModel());

        blockModelTransformer.put(Blocks.CHEST, new ChestBlockAndTrappedChestBlockModel());
        blockModelTransformer.put(Blocks.TRAPPED_CHEST, new ChestBlockAndTrappedChestBlockModel());

        // These block's doesn't have any collision or model data
        blockModelTransformer.put(Blocks.AIR, new AirBlockModel());
        blockModelTransformer.put(Blocks.OAK_SIGN, new AirBlockModel());
        blockModelTransformer.put(Blocks.OAK_WALL_SIGN, new AirBlockModel());
        blockModelTransformer.put(Blocks.BROWN_MUSHROOM, new AirBlockModel());
        blockModelTransformer.put(Blocks.RED_MUSHROOM, new AirBlockModel());
        blockModelTransformer.put(Blocks.NETHER_WART, new AirBlockModel());
        blockModelTransformer.put(Blocks.TALL_GRASS, new AirBlockModel());
        blockModelTransformer.put(Blocks.STONE_PRESSURE_PLATE, new AirBlockModel());
        blockModelTransformer.put(Blocks.OAK_PRESSURE_PLATE, new AirBlockModel());
        blockModelTransformer.put(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, new AirBlockModel());
        blockModelTransformer.put(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, new AirBlockModel());
        blockModelTransformer.put(Blocks.OAK_BUTTON, new AirBlockModel());
        blockModelTransformer.put(Blocks.STONE_BUTTON, new AirBlockModel());
        blockModelTransformer.put(Blocks.FIRE, new AirBlockModel());
        blockModelTransformer.put(Blocks.LEVER, new AirBlockModel());
        blockModelTransformer.put(Blocks.NETHER_PORTAL, new AirBlockModel());
        blockModelTransformer.put(Blocks.END_PORTAL, new AirBlockModel());
        blockModelTransformer.put(Blocks.REDSTONE_WIRE, new AirBlockModel());
        blockModelTransformer.put(Blocks.SUGAR_CANE, new AirBlockModel());
        blockModelTransformer.put(Blocks.TRIPWIRE, new AirBlockModel());
        blockModelTransformer.put(Blocks.TRIPWIRE_HOOK, new AirBlockModel());
        blockModelTransformer.put(Blocks.VINE, new AirBlockModel());
        blockModelTransformer.put(Blocks.GRASS, new AirBlockModel());
        blockModelTransformer.put(Blocks.LAVA, new AirBlockModel());
        blockModelTransformer.put(Blocks.VOID_AIR, new AirBlockModel());

        /* Torch */ for (Block block : Arrays.asList(
                Blocks.TORCH, Blocks.REDSTONE_TORCH, Blocks.WALL_TORCH,
                Blocks.REDSTONE_WALL_TORCH
        )) blockModelTransformer.put(block, new AirBlockModel());

        /* Rail */ for (Block block : Arrays.asList(
                Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL,
                Blocks.POWERED_RAIL)) blockModelTransformer.put(block, new AirBlockModel());

        /* Banner */ for (Block block : Arrays.asList(
                Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER,
                Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER,
                Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER,
                Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER,
                Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER,
                Blocks.BLACK_BANNER)) blockModelTransformer.put(block, new AirBlockModel());

        /* Banner Wall */ for (Block block : Arrays.asList(
                Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER,
                Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER,
                Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER,
                Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER,
                Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER,
                Blocks.BLACK_WALL_BANNER)) blockModelTransformer.put(block, new AirBlockModel());

        /* Saplings */ for (Block block : Arrays.asList(
                Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING)) blockModelTransformer.put(block, new AirBlockModel());

        /* Crops */ for (Block block : Arrays.asList(Blocks.CARROTS, Blocks.POTATOES)) blockModelTransformer.put(block, new AirBlockModel());

        /* Plants */ for (Block block : Arrays.asList(
                Blocks.WHEAT, Blocks.WEEPING_VINES_PLANT, Blocks.TWISTING_VINES_PLANT,
                Blocks.WEEPING_VINES_PLANT)) blockModelTransformer.put(block, new AirBlockModel());

        /* Bush */ for (Block block : Arrays.asList(
                Blocks.DEAD_BUSH, Blocks.SUNFLOWER, Blocks.DANDELION,
                Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM,
                Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY,
                Blocks.LILAC, Blocks.LARGE_FERN, Blocks.FERN,
                Blocks.PEONY, Blocks.ROSE_BUSH)) blockModelTransformer.put(block, new AirBlockModel());

        /* Stem */ for (Block block : Arrays.asList(Blocks.PUMPKIN_STEM, Blocks.MELON_STEM)) blockModelTransformer.put(block, new AirBlockModel());

        /* Flower Pot*/ for (Block block : Arrays.asList(
                Blocks.FLOWER_POT, Blocks.POTTED_OAK_SAPLING, Blocks.POTTED_SPRUCE_SAPLING,
                Blocks.POTTED_BIRCH_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, Blocks.POTTED_ACACIA_SAPLING,
                Blocks.POTTED_DARK_OAK_SAPLING, Blocks.POTTED_MANGROVE_PROPAGULE, Blocks.POTTED_FERN,
                Blocks.POTTED_DANDELION, Blocks.POTTED_POPPY, Blocks.POTTED_BLUE_ORCHID,
                Blocks.POTTED_ALLIUM, Blocks.POTTED_AZURE_BLUET, Blocks.POTTED_RED_TULIP,
                Blocks.POTTED_ORANGE_TULIP, Blocks.POTTED_WHITE_TULIP, Blocks.POTTED_PINK_TULIP,
                Blocks.POTTED_OXEYE_DAISY, Blocks.POTTED_CORNFLOWER, Blocks.POTTED_LILY_OF_THE_VALLEY,
                Blocks.POTTED_WITHER_ROSE, Blocks.POTTED_RED_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM,
                Blocks.POTTED_DEAD_BUSH)) blockModelTransformer.put(block, new FlowerPotBlockModel());
    }

    // This Class represents a 1.8 Block
    public static class BlockModel {

        // These methods are called in the Movement Emulation accordingly

        public void addCollisionBoxesToList(final World world, final BlockPos pos, final BlockState state, final Box mask, final List<Box> boundingBoxList, final Entity collidingEntity) {}
        public void setBlockBounds(final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ) {}
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            return null;
        }
        public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
            EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(worldIn.getBlockState(pos).getBlock(), 1.0);
            EventDispatcher.INSTANCE.call(eventVelocityMultiplier);
            if(eventVelocityMultiplier.getDirty())
                entityIn.setVelocity(entityIn.getVelocity().multiply(eventVelocityMultiplier.getVelocityMultiplier(), 1.0, eventVelocityMultiplier.getVelocityMultiplier()));
        }
        public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {}
        public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
            ((ILivingEntity_Protocol) entityIn).protocolhack_getPlayerLivingEntityMovementWrapper().fall(fallDistance, 1.0F);
        }
        public void onLanded(World worldIn, Entity entityIn) {
            entityIn.getVelocity().y = 0.0D;
        }
        public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
            return motion;
        }
        public boolean isBlockSolid(World worldIn, BlockPos pos, Direction side) {
            return worldIn.getBlockState(pos).getMaterial().isSolid();
        }
    }

    public static class AirBlockModel extends BlockModel {
        // bypass class for better code style
    }

    public static class DefaultBlockModel extends BlockModel {

        public double minX = 0;
        public double minY = 0;
        public double minZ = 0;
        public double maxX = 1;
        public double maxY = 1;
        public double maxZ = 1;

        @Override
        public void addCollisionBoxesToList(World world, BlockPos pos, BlockState state, Box mask, List<Box> boundingBoxList, Entity collidingEntity) {
            final Box boundingBox = this.getCollisionBoundingBox(world, pos, state);

            if (boundingBox != null && mask.intersects(boundingBox)) boundingBoxList.add(boundingBox);
        }

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            return new Box((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY, (double)pos.getZ() + this.maxZ);
        }

        @Override
        public void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = (double)minX;
            this.minY = (double)minY;
            this.minZ = (double)minZ;
            this.maxX = (double)maxX;
            this.maxY = (double)maxY;
            this.maxZ = (double)maxZ;
        }
    }

    public static class CarpetBlockModel extends DefaultBlockModel {

        public static final List<Block> CARPETS_IN_1_8 = Arrays.asList(
                Blocks.WHITE_CARPET,
                Blocks.ORANGE_CARPET,
                Blocks.MAGENTA_CARPET,
                Blocks.LIGHT_BLUE_CARPET,
                Blocks.YELLOW_CARPET,
                Blocks.LIME_CARPET,
                Blocks.PINK_CARPET,
                Blocks.GRAY_CARPET,
                Blocks.LIGHT_GRAY_CARPET,
                Blocks.CYAN_CARPET,
                Blocks.PURPLE_CARPET,
                Blocks.BLUE_CARPET,
                Blocks.BROWN_CARPET,
                Blocks.GREEN_CARPET,
                Blocks.RED_CARPET,
                Blocks.BLACK_CARPET
        );

        public CarpetBlockModel() {
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        }
    }

    public static class CobwebBlockModel extends BlockModel {

        @Override
        public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
            super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

            ((IEntity_Protocol) entityIn).protocolhack_setInWeb(true);
        }
    }

    public static class LiquidBlockModel extends BlockModel {

        protected int getLevel(World worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).getMaterial() == Material.WATER ? ((Integer)worldIn.getBlockState(pos).get(FluidBlock.LEVEL)).intValue() : -1;
        }

        protected int getEffectiveFlowDecay(World worldIn, BlockPos pos) {
            int i = this.getLevel(worldIn, pos);
            return i >= 8 ? 0 : i;
        }

        public boolean isBlockSolid(World worldIn, BlockPos pos, Direction side) {
            Material material = worldIn.getBlockState(pos).getMaterial();
            return material == Material.WATER ? false : (side == Direction.UP ? true : (material == Material.ICE ? false : super.isBlockSolid(worldIn, pos, side)));
        }

        protected Vec3d getFlowVector(World worldIn, BlockPos pos) {
            Vec3d vec3 = new Vec3d(0.0D, 0.0D, 0.0D);
            int i = this.getEffectiveFlowDecay(worldIn, pos);

            for (Direction enumfacing : Direction.HORIZONTAL) {
                BlockPos blockpos = pos.offset(enumfacing);
                int j = this.getEffectiveFlowDecay(worldIn, blockpos);

                if (j < 0) {
                    if (!worldIn.getBlockState(blockpos).getMaterial().blocksMovement()) {
                        j = this.getEffectiveFlowDecay(worldIn, blockpos.down());

                        if (j >= 0) {
                            int k = j - (i - 8);
                            vec3 = vec3.add((double)((blockpos.getX() - pos.getX()) * k), (double)((blockpos.getY() - pos.getY()) * k), (double)((blockpos.getZ() - pos.getZ()) * k));
                        }
                    }
                } else if (j >= 0) {
                    int l = j - i;
                    vec3 = vec3.add((double)((blockpos.getX() - pos.getX()) * l), (double)((blockpos.getY() - pos.getY()) * l), (double)((blockpos.getZ() - pos.getZ()) * l));
                }
            }

            if (((Integer)worldIn.getBlockState(pos).get(FluidBlock.LEVEL)).intValue() >= 8) {
                for (Direction enumfacing1 : Direction.HORIZONTAL) {
                    BlockPos blockpos1 = pos.offset(enumfacing1);

                    if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
                        vec3 = vec3.normalize().add(0.0D, -6.0D, 0.0D);
                        break;
                    }
                }
            }

            return vec3.normalize();
        }

        @Override
        public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
            return motion.add(getFlowVector(worldIn, pos));
        }
    }

    public static class SlimeBlockModel extends DefaultBlockModel {

        @Override
        public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
            if (Math.abs(entityIn.getVelocity().y) < 0.1D && !entityIn.isSneaking()) {
                double d0 = 0.4D + Math.abs(entityIn.getVelocity().y) * 0.2D;

                EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(Blocks.SLIME_BLOCK, (float) d0);
                EventDispatcher.INSTANCE.call(eventVelocityMultiplier);
                if(eventVelocityMultiplier.getDirty())
                    d0 = eventVelocityMultiplier.getVelocityMultiplier();

                entityIn.getVelocity().x *= d0;
                entityIn.getVelocity().z *= d0;
            }
        }


        @Override
        public void onLanded(World worldIn, Entity entityIn) {
            if (entityIn.isSneaking()) {
                entityIn.getVelocity().y = 0;
            } else if (entityIn.getVelocity().y < 0.0D) {
                entityIn.getVelocity().y = -entityIn.getVelocity().y;
            }
        }
    }

    public static class LadderBlockModel extends DefaultBlockModel {

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            BlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == Blocks.LADDER) {
                final float f = 0.125F;

                switch (iblockstate.get(LadderBlock.FACING)) {
                    case NORTH -> this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    case SOUTH -> this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    case WEST -> this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    case EAST -> this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    default -> this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            }
        }
    }

    public static class SlabBlockModel extends DefaultBlockModel {

        public static final List<Block> SLABS_IN_1_8 = Arrays.asList(
                Blocks.STONE_SLAB,
                Blocks.SANDSTONE_SLAB,
                Blocks.COBBLESTONE_SLAB,
                Blocks.BRICK_SLAB,
                Blocks.STONE_BRICK_SLAB,
                Blocks.NETHER_BRICK_SLAB,
                Blocks.QUARTZ_SLAB,
                Blocks.OAK_SLAB,
                Blocks.SPRUCE_SLAB,
                Blocks.BIRCH_SLAB,
                Blocks.JUNGLE_SLAB,
                Blocks.ACACIA_SLAB,
                Blocks.DARK_OAK_SLAB,
                Blocks.RED_SANDSTONE_SLAB,
                Blocks.PETRIFIED_OAK_SLAB,

                // This was originally a metadata
                Blocks.SMOOTH_STONE_SLAB
        );

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }


        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            final BlockState iblockstate = worldIn.getBlockState(pos);
            final SlabType type = iblockstate.get(SlabBlock.TYPE);

            if (type == SlabType.DOUBLE) {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (SLABS_IN_1_8.contains(iblockstate.getBlock())) {
                if (type == SlabType.TOP) {
                    this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
                }
            }
        }
    }

    public static class StairBlockModel extends DefaultBlockModel {

        public static List<Block> STAIRS_IN_1_8 = Arrays.asList(
                Blocks.OAK_STAIRS,
                Blocks.COBBLESTONE_STAIRS,
                Blocks.BRICK_STAIRS,
                Blocks.STONE_BRICK_STAIRS,
                Blocks.NETHER_BRICK_STAIRS,
                Blocks.SANDSTONE_STAIRS,
                Blocks.SPRUCE_STAIRS,
                Blocks.BIRCH_STAIRS,
                Blocks.JUNGLE_STAIRS,
                Blocks.QUARTZ_STAIRS,
                Blocks.ACACIA_STAIRS,
                Blocks.DARK_OAK_STAIRS,
                Blocks.RED_SANDSTONE_STAIRS
        );

        public boolean isBlockStairs(Block blockIn) {
            return blockIn instanceof StairsBlock;
        }

        /**
         * Check whether there is a stair block at the given position and it has the same properties as the given BlockState
         */
        public boolean isSameStair(World worldIn, BlockPos pos, BlockState state) {
            BlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            return isBlockStairs(block) && iblockstate.get(StairsBlock.HALF) == state.get(StairsBlock.HALF) && iblockstate.get(StairsBlock.FACING) == state.get(StairsBlock.FACING);
        }

        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBaseCollisionBounds(worldIn, pos);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            boolean flag = this.func_176306_h(worldIn, pos);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);

            if (flag && this.func_176304_i(worldIn, pos)) {
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }

            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        public void setBaseCollisionBounds(World worldIn, BlockPos pos) {
            if (worldIn.getBlockState(pos).get(StairsBlock.HALF) == BlockHalf.TOP) {
                this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }

        public boolean func_176306_h(World blockAccess, BlockPos pos) {
            BlockState iblockstate = blockAccess.getBlockState(pos);
            Direction enumfacing = (Direction)iblockstate.get(StairsBlock.FACING);
            BlockHalf blockstairs$enumhalf = iblockstate.get(StairsBlock.HALF);
            boolean flag = blockstairs$enumhalf == BlockHalf.TOP;
            float f = 0.5F;
            float f1 = 1.0F;

            if (flag) {
                f = 0.0F;
                f1 = 0.5F;
            }

            float f2 = 0.0F;
            float f3 = 1.0F;
            float f4 = 0.0F;
            float f5 = 0.5F;
            boolean flag1 = true;

            if (enumfacing == Direction.EAST) {
                f2 = 0.5F;
                f5 = 1.0F;
                BlockState iblockstate1 = blockAccess.getBlockState(pos.east());
                Block block = iblockstate1.getBlock();

                if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.get(StairsBlock.HALF)) {
                    Direction enumfacing1 = iblockstate1.get(StairsBlock.FACING);

                    if (enumfacing1 == Direction.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                    {
                        f5 = 0.5F;
                        flag1 = false;
                    }
                    else if (enumfacing1 == Direction.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                    {
                        f4 = 0.5F;
                        flag1 = false;
                    }
                }
            } else if (enumfacing == Direction.WEST) {
                f3 = 0.5F;
                f5 = 1.0F;
                BlockState iblockstate2 = blockAccess.getBlockState(pos.west());
                Block block1 = iblockstate2.getBlock();

                if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.get(StairsBlock.HALF)) {
                    Direction enumfacing2 = iblockstate2.get(StairsBlock.FACING);

                    if (enumfacing2 == Direction.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                        f5 = 0.5F;
                        flag1 = false;
                    }
                    else if (enumfacing2 == Direction.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                    {
                        f4 = 0.5F;
                        flag1 = false;
                    }
                }
            } else if (enumfacing == Direction.SOUTH) {
                f4 = 0.5F;
                f5 = 1.0F;
                BlockState iblockstate3 = blockAccess.getBlockState(pos.south());
                Block block2 = iblockstate3.getBlock();

                if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.get(StairsBlock.HALF)) {
                    Direction enumfacing3 = iblockstate3.get(StairsBlock.FACING);

                    if (enumfacing3 == Direction.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                        f3 = 0.5F;
                        flag1 = false;
                    } else if (enumfacing3 == Direction.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                        f2 = 0.5F;
                        flag1 = false;
                    }
                }
            } else if (enumfacing == Direction.NORTH) {
                BlockState iblockstate4 = blockAccess.getBlockState(pos.north());
                Block block3 = iblockstate4.getBlock();

                if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.get(StairsBlock.HALF)) {
                    Direction enumfacing4 = iblockstate4.get(StairsBlock.FACING);

                    if (enumfacing4 == Direction.WEST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                        f3 = 0.5F;
                        flag1 = false;
                    } else if (enumfacing4 == Direction.EAST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                        f2 = 0.5F;
                        flag1 = false;
                    }
                }
            }

            this.setBlockBounds(f2, f, f4, f3, f1, f5);
            return flag1;
        }

        public boolean func_176304_i(World blockAccess, BlockPos pos) {
            BlockState iblockstate = blockAccess.getBlockState(pos);
            Direction enumfacing = iblockstate.get(StairsBlock.FACING);
            BlockHalf blockstairs$enumhalf = iblockstate.get(StairsBlock.HALF);
            boolean flag = blockstairs$enumhalf == BlockHalf.TOP;
            float f = 0.5F;
            float f1 = 1.0F;

            if (flag) {
                f = 0.0F;
                f1 = 0.5F;
            }

            float f2 = 0.0F;
            float f3 = 0.5F;
            float f4 = 0.5F;
            float f5 = 1.0F;
            boolean flag1 = false;

            if (enumfacing == Direction.EAST) {
                BlockState iblockstate1 = blockAccess.getBlockState(pos.west());
                Block block = iblockstate1.getBlock();

                if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.get(StairsBlock.HALF)) {
                    Direction enumfacing1 = iblockstate1.get(StairsBlock.FACING);

                    if (enumfacing1 == Direction.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                        f4 = 0.0F;
                        f5 = 0.5F;
                        flag1 = true;
                    }
                    else if (enumfacing1 == Direction.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                        f4 = 0.5F;
                        f5 = 1.0F;
                        flag1 = true;
                    }
                }
            } else if (enumfacing == Direction.WEST) {
                BlockState iblockstate2 = blockAccess.getBlockState(pos.east());
                Block block1 = iblockstate2.getBlock();

                if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.get(StairsBlock.HALF)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    Direction enumfacing2 = iblockstate2.get(StairsBlock.FACING);

                    if (enumfacing2 == Direction.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate)) {
                        f4 = 0.0F;
                        f5 = 0.5F;
                        flag1 = true;
                    } else if (enumfacing2 == Direction.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate)) {
                        f4 = 0.5F;
                        f5 = 1.0F;
                        flag1 = true;
                    }
                }
            } else if (enumfacing == Direction.SOUTH) {
                BlockState iblockstate3 = blockAccess.getBlockState(pos.north());
                Block block2 = iblockstate3.getBlock();

                if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.get(StairsBlock.HALF)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    Direction enumfacing3 = iblockstate3.get(StairsBlock.FACING);

                    if (enumfacing3 == Direction.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                        flag1 = true;
                    } else if (enumfacing3 == Direction.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                        f2 = 0.5F;
                        f3 = 1.0F;
                        flag1 = true;
                    }
                }
            } else if (enumfacing == Direction.NORTH) {
                BlockState iblockstate4 = blockAccess.getBlockState(pos.south());
                Block block3 = iblockstate4.getBlock();

                if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.get(StairsBlock.HALF)) {
                    Direction enumfacing4 = iblockstate4.get(StairsBlock.FACING);

                    if (enumfacing4 == Direction.WEST && !isSameStair(blockAccess, pos.west(), iblockstate)) {
                        flag1 = true;
                    } else if (enumfacing4 == Direction.EAST && !isSameStair(blockAccess, pos.east(), iblockstate)) {
                        f2 = 0.5F;
                        f3 = 1.0F;
                        flag1 = true;
                    }
                }
            }
            if (flag1) this.setBlockBounds(f2, f, f4, f3, f1, f5);
            return flag1;
        }
    }

    public static class BrewingStandBlockModel extends DefaultBlockModel {

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBoundsForItemRender();
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        public void setBlockBoundsForItemRender() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    public static class CauldronBlockModel extends DefaultBlockModel {

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            float f = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBoundsForItemRender();
        }

        public void setBlockBoundsForItemRender() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static class EndPortalFrameBlockModel extends DefaultBlockModel {

        public void setBlockBoundsForItemRender() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
        }

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);

            if (worldIn.getBlockState(pos).get(EndPortalFrameBlock.EYE).booleanValue()) {
                this.setBlockBounds(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }

            this.setBlockBoundsForItemRender();
        }
    }

    public static class FenceBlockModel extends DefaultBlockModel {

        public static List<Block> FENCES_IN_1_8 = Arrays.asList(
                Blocks.OAK_FENCE,
                Blocks.SPRUCE_FENCE,
                Blocks.BIRCH_FENCE,
                Blocks.JUNGLE_FENCE,
                Blocks.ACACIA_FENCE,
                Blocks.DARK_OAK_FENCE,
                Blocks.NETHER_BRICK_FENCE
        );
        public static final List<Material> FENCES_IN_1_8_AS_MATERIALS = FENCES_IN_1_8.stream().map(b -> b.getDefaultState().getMaterial()).toList();

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            boolean flag = this.canConnectTo(worldIn, pos.north());
            boolean flag1 = this.canConnectTo(worldIn, pos.south());
            boolean flag2 = this.canConnectTo(worldIn, pos.west());
            boolean flag3 = this.canConnectTo(worldIn, pos.east());
            float f = 0.375F;
            float f1 = 0.625F;
            float f2 = 0.375F;
            float f3 = 0.625F;

            if (flag) f2 = 0.0F;
            if (flag1) f3 = 1.0F;

            if (flag || flag1) {
                this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }

            f2 = 0.375F;
            f3 = 0.625F;

            if (flag2) f = 0.0F;
            if (flag3) f1 = 1.0F;

            if (flag2 || flag3 || !flag && !flag1) {
                this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }

            if (flag) f2 = 0.0F;
            if (flag1) f3 = 1.0F;

            this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
        }

        public boolean canConnectTo(World worldIn, BlockPos pos) {
            Block block = worldIn.getBlockState(pos).getBlock();
            return block != Blocks.BARRIER && ((block instanceof FenceBlock && FENCES_IN_1_8_AS_MATERIALS.contains(block.getDefaultState().getMaterial()))
                    || block instanceof FenceGateBlock || (block.getDefaultState().isOpaque() && block.getDefaultState().isFullCube(worldIn, pos) && block.getDefaultState().getMaterial() != Material.GOURD));
        }
    }

    public static class HopperBlockModel extends DefaultBlockModel {

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            float f = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static class LilyPadBlockModel extends DefaultBlockModel {

        public LilyPadBlockModel() {
            float f = 0.5F;
            float f1 = 0.015625F;

            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        }

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            if (collidingEntity == null || !(collidingEntity instanceof BoatEntity)) {
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }
        }
    }

    public static class PaneBlockModel extends DefaultBlockModel {

        public static final List<Block> PANES_IN_1_8 = Arrays.asList(
                Blocks.GLASS_PANE,

                Blocks.IRON_BARS,

                Blocks.WHITE_STAINED_GLASS_PANE,
                Blocks.ORANGE_STAINED_GLASS_PANE,
                Blocks.MAGENTA_STAINED_GLASS_PANE,
                Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                Blocks.YELLOW_STAINED_GLASS_PANE,
                Blocks.LIME_STAINED_GLASS_PANE,
                Blocks.PINK_STAINED_GLASS_PANE,
                Blocks.GRAY_STAINED_GLASS_PANE,
                Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                Blocks.CYAN_STAINED_GLASS_PANE,
                Blocks.PURPLE_STAINED_GLASS_PANE,
                Blocks.BLUE_STAINED_GLASS_PANE,
                Blocks.BROWN_STAINED_GLASS_PANE,
                Blocks.GREEN_STAINED_GLASS_PANE,
                Blocks.RED_STAINED_GLASS_PANE,
                Blocks.BLACK_STAINED_GLASS_PANE
        );

        private static final List<Block> INTERNAL_GLASSES_IN_1_8 = Arrays.asList(
                Blocks.GLASS,

                Blocks.WHITE_STAINED_GLASS,
                Blocks.ORANGE_STAINED_GLASS,
                Blocks.MAGENTA_STAINED_GLASS,
                Blocks.LIGHT_BLUE_STAINED_GLASS,
                Blocks.YELLOW_STAINED_GLASS,
                Blocks.LIME_STAINED_GLASS,
                Blocks.PINK_STAINED_GLASS,
                Blocks.GRAY_STAINED_GLASS,
                Blocks.LIGHT_GRAY_STAINED_GLASS,
                Blocks.CYAN_STAINED_GLASS,
                Blocks.PURPLE_STAINED_GLASS,
                Blocks.BLUE_STAINED_GLASS,
                Blocks.BROWN_STAINED_GLASS,
                Blocks.GREEN_STAINED_GLASS,
                Blocks.RED_STAINED_GLASS,
                Blocks.BLACK_STAINED_GLASS
        );

        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            boolean flag = this.canPaneConnectToBlock(worldIn, pos, worldIn.getBlockState(pos.north()).getBlock());
            boolean flag1 = this.canPaneConnectToBlock(worldIn, pos, worldIn.getBlockState(pos.south()).getBlock());
            boolean flag2 = this.canPaneConnectToBlock(worldIn, pos, worldIn.getBlockState(pos.west()).getBlock());
            boolean flag3 = this.canPaneConnectToBlock(worldIn, pos, worldIn.getBlockState(pos.east()).getBlock());

            if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
                if (flag2) {
                    this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
                } else if (flag3) {
                    this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
                }
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }

            if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
                if (flag) {
                    this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
                } else if (flag1) {
                    this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
                }
            } else {
                this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }
        }

        public final boolean canPaneConnectToBlock(final World worldIn, final BlockPos pos, Block blockIn) {
            return blockIn.getDefaultState().isOpaqueFullCube(worldIn, pos) || PANES_IN_1_8.contains(blockIn) || INTERNAL_GLASSES_IN_1_8.contains(blockIn) || blockIn instanceof PaneBlock;
        }
    }

    public static class PistonBaseBlockModel extends DefaultBlockModel {

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
    }

    public static class PistonExtensionBlockModel extends DefaultBlockModel {

        @Override
        public void addCollisionBoxesToList(World worldIn, BlockPos pos, BlockState state, Box mask, List<Box> list, Entity collidingEntity) {
            this.applyHeadBounds(state);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.applyCoreBounds(state);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        public void applyHeadBounds(BlockState state) {
            Direction enumfacing = state.get(PistonBlock.FACING);

            if (enumfacing != null) {
                switch (enumfacing) {
                    case DOWN -> this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                    case UP -> this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                    case NORTH -> this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                    case SOUTH -> this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                    case WEST -> this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                    case EAST -> this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        private void applyCoreBounds(BlockState state) {
            switch (state.get(PistonExtensionBlock.FACING)) {
                case DOWN -> this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                case UP -> this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                case NORTH -> this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                case SOUTH -> this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                case WEST -> this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                case EAST -> this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
            }
        }
    }

    public static class CactusBlockModel extends DefaultBlockModel {

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            float f = 0.0625F;
            return new Box((double)((float)pos.getX() + f), (double)pos.getY(), (double)((float)pos.getZ() + f), (double)((float)(pos.getX() + 1) - f), (double)((float)(pos.getY() + 1) - f), (double)((float)(pos.getZ() + 1) - f));
        }
    }

    public static class CakeBlockModel extends DefaultBlockModel {

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            float f = 0.0625F;
            float f1 = (float)(1 + ((Integer)state.get(CakeBlock.BITES)).intValue() * 2) / 16.0F;
            float f2 = 0.5F;
            return new Box((double)((float)pos.getX() + f1), (double)pos.getY(), (double)((float)pos.getZ() + f), (double)((float)(pos.getX() + 1) - f), (double)((float)pos.getY() + f2), (double)((float)(pos.getZ() + 1) - f));
        }
    }

    public static class CocoaBlockModel extends DefaultBlockModel {

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        @SuppressWarnings("incomplete-switch")
        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            BlockState iblockstate = worldIn.getBlockState(pos);
            Direction enumfacing = iblockstate.get(CocoaBlock.FACING);
            int i = iblockstate.get(CocoaBlock.AGE).intValue();
            int j = 4 + i * 2;
            int k = 5 + i * 2;
            float f = (float)j / 2.0F;

            switch (enumfacing) {
                case SOUTH -> this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, (15.0F - (float) j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                case NORTH -> this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) j) / 16.0F);
                case WEST -> this.setBlockBounds(0.0625F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                case EAST -> this.setBlockBounds((15.0F - (float) j) / 16.0F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
            }
        }
    }

    public static class DoorBlockModel extends DefaultBlockModel {

        public static final List<Block> DOORS_IN_1_8 = Arrays.asList(
                Blocks.OAK_DOOR,
                Blocks.IRON_DOOR,
                Blocks.SPRUCE_DOOR,
                Blocks.BIRCH_DOOR,
                Blocks.JUNGLE_DOOR,
                Blocks.ACACIA_DOOR,
                Blocks.DARK_OAK_DOOR
        );

        public Direction getFacing(int combinedMeta) {
            return Direction.fromHorizontal(combinedMeta & 3).rotateYClockwise();
        }

        protected boolean isOpen(int combinedMeta) {
            return (combinedMeta & 4) != 0;
        }

        protected boolean isTop(int meta) {
            return (meta & 8) != 0;
        }

        protected boolean isHingeLeft(int combinedMeta) {
            return (combinedMeta & 16) != 0;
        }

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
        }

        public int getMetaFromState(BlockState state) {
            int i = 0;

            try {
                if (state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    i = i | 8;

                    if (state.get(DoorBlock.HINGE) == DoorHinge.RIGHT) i |= 1;
                    if (state.get(DoorBlock.POWERED).booleanValue()) i |= 2;
                } else {
                    i = i | rotateY(state.get(DoorBlock.FACING)).getHorizontal();

                    if (state.get(DoorBlock.OPEN).booleanValue()) i |= 4;
                }

                return i;
            } catch (Exception e) {
                // Beta 1.7.3 forcing bypass
                return i;
            }
        }

        // Direction Wrapper
        public Direction rotateY(final Direction input) {
            return switch (input) {
                case NORTH -> Direction.EAST;
                case EAST -> Direction.SOUTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.NORTH;
                default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + input);
            };
        }

        private void setBoundBasedOnMeta(int combinedMeta) {
            float f = 0.1875F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
            Direction enumfacing = getFacing(combinedMeta).getOpposite();
            boolean flag = isOpen(combinedMeta);
            boolean flag1 = isHingeLeft(combinedMeta);

            if (flag) {
                if (enumfacing == Direction.EAST) {
                    if (!flag1) {
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    } else {
                        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    }
                }
                else if (enumfacing == Direction.SOUTH) {
                    if (!flag1) {
                        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    }
                }
                else if (enumfacing == Direction.WEST) {
                    if (!flag1) {
                        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    } else {
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    }
                } else if (enumfacing == Direction.NORTH) {
                    if (!flag1) {
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    } else {
                        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            } else if (enumfacing == Direction.EAST) {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            } else if (enumfacing == Direction.SOUTH) {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            } else if (enumfacing == Direction.WEST) {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (enumfacing == Direction.NORTH) {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }

        public int combineMetadata(World worldIn, BlockPos pos) {
            BlockState iblockstate = worldIn.getBlockState(pos);
            int i = getMetaFromState(iblockstate);
            boolean flag = isTop(i);
            BlockState iblockstate1 = worldIn.getBlockState(pos.down());
            int j = getMetaFromState(iblockstate1);
            int k = flag ? j : i;
            BlockState iblockstate2 = worldIn.getBlockState(pos.up());
            int l = getMetaFromState(iblockstate2);
            int i1 = flag ? i : l;
            boolean flag1 = (i1 & 1) != 0;
            boolean flag2 = (i1 & 2) != 0;
            return removeHalfBit(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
        }
        protected static int removeHalfBit(int meta)
        {
            return meta & 7;
        }
    }

    public static class FarmlandBlockModel extends DefaultBlockModel {

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            return new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
        }
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public static class FenceGateBlockModel extends DefaultBlockModel {

        public static final List<Block> FENCE_GATES_IN_1_8 = Arrays.asList(
                Blocks.OAK_FENCE_GATE,
                Blocks.SPRUCE_FENCE_GATE,
                Blocks.BIRCH_FENCE_GATE,
                Blocks.JUNGLE_FENCE_GATE,
                Blocks.ACACIA_FENCE_GATE,
                Blocks.DARK_OAK_FENCE_GATE
        );

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            if (state.get(FenceGateBlock.OPEN).booleanValue()) {
                return null;
            } else {
                Direction.Axis enumfacing$axis = state.get(FenceGateBlock.FACING).getAxis();
                return enumfacing$axis == Direction.Axis.Z ? new Box((double)pos.getX(), (double)pos.getY(), (double)((float)pos.getZ() + 0.375F), (double)(pos.getX() + 1), (double)((float)pos.getY() + 1.5F), (double)((float)pos.getZ() + 0.625F)) : new Box((double)((float)pos.getX() + 0.375F), (double)pos.getY(), (double)pos.getZ(), (double)((float)pos.getX() + 0.625F), (double)((float)pos.getY() + 1.5F), (double)(pos.getZ() + 1));
            }
        }
    }

    /**
     * 1.8 didn't had a rotation value, it had a raw direction enum, so ViaVersion remaps the raw direction enum to more useful angle values which we need to implement here instead of the old code
     *
     * Solution:
     * splitting Skull and Wall Skull into to different Models which replace the important part, via will remap a up direction skull to a head skull, so we basically use this block which doesn't really exist
     *
     * really cursed stuff
     */
    public static class SkullBlockModel extends DefaultBlockModel {

        public static final List<Block> SKULLS_IN_1_8 = Arrays.asList(
                Blocks.SKELETON_SKULL,
                Blocks.WITHER_SKELETON_SKULL,
                Blocks.ZOMBIE_HEAD,
                Blocks.PLAYER_HEAD,
                Blocks.CREEPER_HEAD
        );

        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        public static class PseudoSkullWallBlockModel extends DefaultBlockModel {

            public static final List<Block> WALL_SKULLS_IN_1_8 = Arrays.asList(
                    Blocks.SKELETON_WALL_SKULL,
                    Blocks.WITHER_SKELETON_WALL_SKULL,
                    Blocks.ZOMBIE_WALL_HEAD,
                    Blocks.PLAYER_WALL_HEAD,
                    Blocks.CREEPER_WALL_HEAD
            );

            @Override
            public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
                this.setBlockBoundsBasedOnState(worldIn, pos);
                return super.getCollisionBoundingBox(worldIn, pos, state);
            }

            public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
                switch (worldIn.getBlockState(pos).get(WallSkullBlock.FACING)) {
                    case UP -> this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                    case NORTH -> this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                    case SOUTH -> this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                    case WEST -> this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                    case EAST -> this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
                    default -> this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                }
            }
        }
    }

    public static class SnowBlockModel extends DefaultBlockModel {

        public SnowBlockModel() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            int i = ((Integer)state.get(SnowBlock.LAYERS)).intValue() - 1;
            float f = 0.125F;
            return new Box((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)((float)pos.getY() + (float)i * f), (double)pos.getZ() + this.maxZ);
        }
    }

    public static class SoulSandBlockModel extends DefaultBlockModel {

        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            float f = 0.125F;
            return new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)((float)(pos.getY() + 1) - f), (double)(pos.getZ() + 1));
        }

        @Override
        public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
            super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

            double multiplier = 0.4D;

            EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(Blocks.SOUL_SAND, (float) multiplier);
            EventDispatcher.INSTANCE.call(eventVelocityMultiplier);
            if(eventVelocityMultiplier.getDirty())
                multiplier = eventVelocityMultiplier.getVelocityMultiplier();

            entityIn.getVelocity().x *= multiplier;
            entityIn.getVelocity().z *= multiplier;
        }
    }

    public static class TrapdoorBlockModel extends DefaultBlockModel {

        public static final List<Block> TRAPDOORS_IN_1_8 = Arrays.asList(
                Blocks.IRON_TRAPDOOR,
                Blocks.OAK_TRAPDOOR
        );

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            this.setBounds(worldIn.getBlockState(pos));
        }

        public void setBounds(BlockState state) {
            if (TRAPDOORS_IN_1_8.contains(state.getBlock())) {
                boolean flag = state.get(TrapdoorBlock.HALF) == BlockHalf.TOP;
                Boolean obool = state.get(TrapdoorBlock.OPEN);
                Direction enumfacing = state.get(TrapdoorBlock.FACING);
                float f = 0.1875F;

                if (flag) {
                    this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
                }

                if (obool.booleanValue()) {
                    if (enumfacing == Direction.NORTH) this.setBlockBounds(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                    if (enumfacing == Direction.SOUTH) this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                    if (enumfacing == Direction.WEST) this.setBlockBounds(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    if (enumfacing == Direction.EAST) this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                }
            }
        }
    }

    public static class WallBlockModel extends DefaultBlockModel {

        public static final List<Block> WALLS_IN_1_8 = Arrays.asList(
                Blocks.COBBLESTONE_WALL,
                Blocks.MOSSY_COBBLESTONE_WALL
        );

        @Override
        public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
            this.setBlockBoundsBasedOnState(worldIn, pos);
            this.maxY = 1.5D;
            return super.getCollisionBoundingBox(worldIn, pos, state);
        }

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            boolean flag = this.canConnectTo(worldIn, pos.north());
            boolean flag1 = this.canConnectTo(worldIn, pos.south());
            boolean flag2 = this.canConnectTo(worldIn, pos.west());
            boolean flag3 = this.canConnectTo(worldIn, pos.east());

            float f = 0.25F;
            float f1 = 0.75F;
            float f2 = 0.25F;
            float f3 = 0.75F;
            float f4 = 1.0F;

            if (flag) f2 = 0.0F;
            if (flag1) f3 = 1.0F;
            if (flag2) f = 0.0F;
            if (flag3) f1 = 1.0F;

            if (flag && flag1 && !flag2 && !flag3) {
                f4 = 0.8125F;
                f = 0.3125F;
                f1 = 0.6875F;
            } else if (!flag && !flag1 && flag2 && flag3) {
                f4 = 0.8125F;
                f2 = 0.3125F;
                f3 = 0.6875F;
            }

            this.setBlockBounds(f, 0.0F, f2, f1, f4, f3);
        }

        public boolean canConnectTo(World worldIn, BlockPos pos) {
            final Block block = worldIn.getBlockState(pos).getBlock();
            return block != Blocks.BARRIER && (WALLS_IN_1_8.contains(block) || block instanceof FenceGateBlock || (block.getDefaultState().isOpaqueFullCube(worldIn, pos) && block.getDefaultState().getMaterial() != Material.GOURD));
        }
    }

    public static class RedstoneComparatorAndRepeaterBlockModel extends DefaultBlockModel {

        public RedstoneComparatorAndRepeaterBlockModel() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    public static class AnvilBlockModel extends DefaultBlockModel {

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            final Direction enumfacing = worldIn.getBlockState(pos).get(AnvilBlock.FACING);

            if (enumfacing.getAxis() == Direction.Axis.X) {
                this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
            } else {
                this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
            }
        }

        @Override
        public void addCollisionBoxesToList(World world, BlockPos pos, BlockState state, Box mask, List<Box> boundingBoxList, Entity collidingEntity) {
            this.setBlockBoundsBasedOnState(world, pos);
            super.addCollisionBoxesToList(world, pos, state, mask, boundingBoxList, collidingEntity);
        }
    }

    public static class BedBlockModel extends DefaultBlockModel {

        public BedBlockModel() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
        }
    }

    public static class ChestBlockAndTrappedChestBlockModel extends DefaultBlockModel {

        public void setBlockBoundsBasedOnState(World worldIn, BlockPos pos) {
            if (worldIn.getBlockState(pos.north()).getBlock() == Blocks.CHEST) {
                this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
            } else if (worldIn.getBlockState(pos.south()).getBlock() == Blocks.CHEST) {
                this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
            } else if (worldIn.getBlockState(pos.west()).getBlock() == Blocks.CHEST) {
                this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
            } else if (worldIn.getBlockState(pos.east()).getBlock() == Blocks.CHEST) {
                this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
            } else {
                this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
            }
        }

        @Override
        public void addCollisionBoxesToList(World world, BlockPos pos, BlockState state, Box mask, List<Box> boundingBoxList, Entity collidingEntity) {
            setBlockBoundsBasedOnState(world, pos);
            super.addCollisionBoxesToList(world, pos, state, mask, boundingBoxList, collidingEntity);
        }
    }

    public static class DaylightDetectorBlockModel extends DefaultBlockModel {

        public DaylightDetectorBlockModel() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        }
    }

    public static class DragonEggBlockModel extends DefaultBlockModel {

        public DragonEggBlockModel() {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
        }
    }

    public static class EnchantmentTableBlockModel extends DefaultBlockModel {

        public EnchantmentTableBlockModel() {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        }
    }

    public static class EnderChestBlockModel extends DefaultBlockModel {

        public EnderChestBlockModel() {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    public static class FlowerPotBlockModel extends DefaultBlockModel {

        public FlowerPotBlockModel() {
            float f = 0.375F;
            float f1 = f / 2.0F;
            this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
        }
    }

    /**
     * @author FlorianMichael as Jesse
     *
     * This method returns the appropriate model of the 1.8 from a block, if no model was specified,
     * the model of a Stone block (DefaultBlockModel) is passed, as in the 1.8, the BlockModelEmulator defines
     * thus only modified blocks or blocks which have no model data.
     * @param block input block
     * @return block model wrapper
     */
    public static BlockModel getTransformerByBlock(final Block block) {
        if (!blockModelTransformer.containsKey(block)) {
            return blockModelTransformer.get(Blocks.STONE);
        }
        return blockModelTransformer.get(block);
    }
}
