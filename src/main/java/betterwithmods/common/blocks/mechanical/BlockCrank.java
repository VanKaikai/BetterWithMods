package betterwithmods.common.blocks.mechanical;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.block.IOverpower;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.tile.TileCrank;
import betterwithmods.module.general.MechanicalPower;
import betterwithmods.util.TooltipLib;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockCrank extends BWMBlock implements IOverpower {
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 7);
    public static final float BASE_HEIGHT = 0.25F;
    private static final int TICK_RATE = 3;
    private static final AxisAlignedBB CRANK_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, BASE_HEIGHT, 1.0F);

    public BlockCrank() {
        super(Material.ROCK);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.WOOD);
        this.setTickRandomly(true);
        this.setDefaultState(getDefaultState().withProperty(STAGE, 0));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int tickRate(World worldIn) {
        return TICK_RATE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return CRANK_AABB;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleBlockUpdate(pos, this, tickRate(worldIn), 5);
    }

    @Override
    public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
        return world.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        int stage = state.getValue(STAGE);
        if (stage == 0) {
            return (MechanicalPower.HAND_CRANK_EXHAUSTION > 0.0)
                    ? toggleWithExhaustion(world, pos, state, player, hand)
                    : toggleSwitch(world, pos, state);
        }
        return false;
    }

    private boolean toggleWithExhaustion(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand) {
        if (player.getFoodStats().getFoodLevel() > PlayerHelper.getMinimumHunger()) {
            player.addExhaustion(MechanicalPower.HAND_CRANK_EXHAUSTION);
            if (!world.isRemote) {
                toggleSwitch(world, pos, state);
            }
        } else if (world.isRemote) {
            if (hand == EnumHand.MAIN_HAND)
                player.sendStatusMessage(TooltipLib.getMessageComponent(TooltipLib.HANDCRANK_EXHAUSTION), true);
            return false;
        }
        return true;
    }

    private boolean toggleSwitch(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            if (!checkForOverpower(world, pos)) {
                world.setBlockState(pos, state.withProperty(STAGE, 1));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 2.0F);
                world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
            } else {
                overpower(world, pos);
            }
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return true;
    }

    public boolean checkForOverpower(World world, BlockPos pos) {
        int potentialDevices = 0;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset = pos.offset(facing);
            if (BWMAPI.IMPLEMENTATION.canInput(world, offset, facing)) {
                potentialDevices++;
            }
        }
        return potentialDevices > 1;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int stage = state.getValue(STAGE);
        if (stage > 0) {
            if (stage < 7) {
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 2.0F);
                if (stage <= 5)
                    world.scheduleBlockUpdate(pos, this, tickRate(world) + stage, 5);
                else
                    world.scheduleBlockUpdate(pos, this, 18, 5);

                world.setBlockState(pos, state.withProperty(STAGE, stage + 1));
            } else {
                world.setBlockState(pos, state.withProperty(STAGE, 0));
                world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.7F);
            }
        }
        world.notifyNeighborsOfStateChange(pos, this, false);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        BlockPos down = pos.down();
        if (!world.isSideSolid(down, EnumFacing.UP)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileCrank();
    }

    @Override
    public void overpower(World world, BlockPos pos) {
        if (doesOverpower()) {
            //TODO add loot table
            world.setBlockToAir(pos);
        }
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : super.getBlockFaceShape(worldIn, state, pos, face);
    }
}
