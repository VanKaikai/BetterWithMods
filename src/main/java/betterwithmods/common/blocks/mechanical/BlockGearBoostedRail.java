package betterwithmods.common.blocks.mechanical;

import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockGearBoostedRail extends BlockRailPowered {

    public static final double MOTION_CART = 0.05d;
    public static final double MOTION_SCALE = 0.06d;

    public BlockGearBoostedRail() {
        super();
        this.setHardness(0.7F);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    protected void updateState(IBlockState state, World world, @Nonnull BlockPos pos, Block block) {
        boolean poweredProperty = state.getValue(POWERED);
        boolean isOnPoweredGearbox = isOnActiveGearbox(state, world, pos);
        if (poweredProperty != isOnPoweredGearbox) {
            world.setBlockState(pos, state.withProperty(POWERED, isOnPoweredGearbox), 3);
            world.notifyNeighborsOfStateChange(pos.down(), this, false);
            if (state.getValue(SHAPE).isAscending())
                world.notifyNeighborsOfStateChange(pos.up(), this, false);
        }
    }

    private boolean isOnActiveGearbox(IBlockState state, World world, BlockPos pos) {
        if (!(world.getBlockState(pos.down()).getBlock() instanceof BlockGearbox)) return false;
        EnumRailDirection dir = state.getValue(SHAPE);
        IBlockState below = world.getBlockState(pos.down());
        EnumFacing face = below.getValue(DirUtils.FACING);
        boolean correctFace = false;
        if (dir == EnumRailDirection.ASCENDING_EAST || dir == EnumRailDirection.ASCENDING_WEST || dir == EnumRailDirection.EAST_WEST) {
            correctFace = face == EnumFacing.DOWN || face == EnumFacing.NORTH || face == EnumFacing.SOUTH;
        } else if (dir == EnumRailDirection.ASCENDING_NORTH || dir == EnumRailDirection.ASCENDING_SOUTH || dir == EnumRailDirection.NORTH_SOUTH) {
            correctFace = face == EnumFacing.DOWN || face == EnumFacing.EAST || face == EnumFacing.WEST;
        }
        return correctFace && ((BlockGearbox) below.getBlock()).isActive(below);
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    private void accelerateMinecart(World world, EntityMinecart cart, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        double planarMotion = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        EnumFacing gearboxFace = world.getBlockState(pos.down()).getValue(DirUtils.FACING);
        switch (state.getValue(SHAPE)) {
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
            case NORTH_SOUTH:
                if (planarMotion > 0.01D) {
                    if ((gearboxFace == EnumFacing.EAST && cart.motionZ < 0.0D) ||
                            (gearboxFace == EnumFacing.WEST && cart.motionZ > 0.0D)) {
                        cart.motionZ -= cart.motionZ / planarMotion * MOTION_SCALE;
                        if (!world.isRemote && planarMotion > MOTION_CART && world.rand.nextDouble() < planarMotion)
                            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 2.0F);
                    } else
                        cart.motionZ += cart.motionZ / planarMotion * MOTION_SCALE;
                } else {
                    if (gearboxFace == EnumFacing.EAST && !world.getBlockState(pos.west()).isOpaqueCube())
                        cart.motionZ = MOTION_CART;
                    else if (gearboxFace == EnumFacing.WEST && !world.getBlockState(pos.east()).isOpaqueCube())
                        cart.motionZ = -MOTION_CART;
                    else if (gearboxFace == EnumFacing.DOWN && world.getBlockState(pos.west()).isOpaqueCube())
                        cart.motionZ = MOTION_CART;
                    else if (gearboxFace == EnumFacing.DOWN && world.getBlockState(pos.east()).isOpaqueCube())
                        cart.motionZ = -MOTION_CART;
                }
                break;
            case ASCENDING_EAST:
            case ASCENDING_WEST:
            case EAST_WEST:
                if (planarMotion > 0.01D) {
                    if ((gearboxFace == EnumFacing.SOUTH && cart.motionX > 0.0D) ||
                            (gearboxFace == EnumFacing.NORTH && cart.motionX < 0.0D)) {
                        cart.motionX -= cart.motionX / planarMotion * MOTION_SCALE;
                        if (!world.isRemote && planarMotion > MOTION_CART && world.rand.nextDouble() < planarMotion)
                            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 2.0F);
                    } else
                        cart.motionX += cart.motionX / planarMotion * MOTION_SCALE;
                } else {
                    if (gearboxFace == EnumFacing.SOUTH && !world.getBlockState(pos.west()).isOpaqueCube())
                        cart.motionX = -MOTION_CART;
                    else if (gearboxFace == EnumFacing.NORTH && !world.getBlockState(pos.east()).isOpaqueCube())
                        cart.motionX = MOTION_CART;
                    else if (gearboxFace == EnumFacing.DOWN && world.getBlockState(pos.west()).isOpaqueCube())
                        cart.motionX = MOTION_CART;
                    else if (gearboxFace == EnumFacing.DOWN && world.getBlockState(pos.east()).isOpaqueCube())
                        cart.motionX = -MOTION_CART;
                }
                break;
            default:
                break;
        }
    }

    private void decelerateMinecart(World world, EntityMinecart cart, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        double planarMotion = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        if (planarMotion > 0.01D) {
            double zMotion = Math.sqrt(cart.motionZ * cart.motionZ);
            double xMotion = Math.sqrt(cart.motionX * cart.motionX);
            if (xMotion > 0.0D) {
                cart.motionX -= cart.motionX / planarMotion * 0.06D;
            } else if (zMotion > 0.0D) {
                cart.motionZ -= cart.motionZ / planarMotion * 0.06D;
            }
            playBoosterSound(world, pos, planarMotion);
        } else if (state.getValue(SHAPE) == EnumRailDirection.EAST_WEST || state.getValue(SHAPE) == EnumRailDirection.NORTH_SOUTH) {
            cart.motionX = 0.0D;
            cart.motionZ = 0.0D;
        }
    }

    /**
     * Plays a sound according to cart's motion.
     *
     * @param world        World.
     * @param pos          Position in the world.
     * @param planarMotion Motion of the cart.
     */
    private void playBoosterSound(World world, BlockPos pos, double planarMotion) {
        if (!world.isRemote && planarMotion > MOTION_CART && world.rand.nextDouble() < planarMotion)
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 2.0F);
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() == this)) return;
        IBlockState under = world.getBlockState(pos.down());
        Block blockUnder = under.getBlock();
        if (blockUnder instanceof BlockGearbox) {
            BlockGearbox gearbox = (BlockGearbox) blockUnder;
            EnumFacing face = world.getBlockState(pos.down()).getValue(DirUtils.FACING);
            if (face == EnumFacing.UP) return;
            if (world.isBlockPowered(pos.down()))
                return;//=> No deceleration or acceleration if the block under the rail is powered.
            if (gearbox.isActive(under))
                accelerateMinecart(world, cart, pos);
            else
                decelerateMinecart(world, cart, pos);
        } else {
            double planarMotion = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
            playBoosterSound(world, pos, planarMotion);
        }
    }
}
