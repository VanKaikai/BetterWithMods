package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.BlockIngredientSpecial;
import betterwithmods.util.DirUtils;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Set;

import static net.minecraft.util.EnumFacing.UP;

/**
 * Created by primetoxinz on 9/4/16.
 */
public class BlockBUD extends BWMBlock {
    private static final PropertyBool REDSTONE = PropertyBool.create("redstone");
    private static final Set<BlockIngredient> BLACKLIST = Sets.newHashSet(
            new BlockIngredient(new ItemStack(BWMBlocks.BUDDY_BLOCK)),
            new BlockIngredient(new ItemStack(Blocks.REDSTONE_WIRE)),
            new BlockIngredient(new ItemStack(Blocks.POWERED_REPEATER)),
            new BlockIngredient(new ItemStack(Blocks.UNPOWERED_REPEATER)),
            new BlockIngredient(new ItemStack(Blocks.REDSTONE_TORCH)),
            new BlockIngredient(new ItemStack(Blocks.UNLIT_REDSTONE_TORCH)),
            new BlockIngredient(new ItemStack(BWMBlocks.LIGHT)),
            //Blacklist all tileentites from causing buds
            new BlockIngredientSpecial((world, pos) -> {
                IBlockState state = world.getBlockState(pos);
                return state.getBlock().hasTileEntity(state);
            })
    );

    public BlockBUD() {
        super(Material.ROCK);
        setHardness(3.5F);
        setSoundType(SoundType.STONE);
        setDefaultState(getDefaultState().withProperty(DirUtils.FACING, UP));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int tickRate(World var1) {
        return 5;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, REDSTONE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int facing = (meta >> 1);
        boolean redstone = (meta & 1) == 1;
        return getDefaultState().withProperty(REDSTONE, redstone).withProperty(DirUtils.FACING, EnumFacing.getFront(facing));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(DirUtils.FACING).getIndex();
        int redstone = state.getValue(REDSTONE) ? 1 : 0;
        return redstone | facing << 1;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(DirUtils.FACING, DirUtils.convertEntityOrientationToFacing(placer, UP));
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        worldIn.scheduleUpdate(pos, state.getBlock(), 1);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    private boolean isBlacklisted(World world, BlockPos pos) {
        for (BlockIngredient ingredient : BLACKLIST) {
            if (ingredient.apply(world, pos, world.getBlockState(pos)))
                return true;
        }
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos other) {
        if (!isRedstoneOn(world, pos) && !isBlacklisted(world, other)) {
            world.scheduleUpdate(pos, state.getBlock(), tickRate(world));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (isRedstoneOn(worldIn, pos)) {
            setRedstone(worldIn, pos, false);
        } else {
            setRedstone(worldIn, pos, true);
            worldIn.scheduleUpdate(pos, state.getBlock(), tickRate(worldIn));
        }
    }

    public void setRedstone(World world, BlockPos pos, boolean newState) {
        if (newState != isRedstoneOn(world, pos)) {
            if (newState) {
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1, .5f);
            }

            BlockPos facing = pos.offset(getFacing(world.getBlockState(pos)));
            if (!world.isAirBlock(facing)) {
                world.getBlockState(facing).getBlock().onNeighborChange(world, facing, pos);
            }
            world.setBlockState(pos, world.getBlockState(pos).withProperty(REDSTONE, newState));
        }
    }

    public boolean isRedstoneOn(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(REDSTONE);
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getPower(blockAccess, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getPower(blockAccess, pos, side);
    }

    public int getPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side.getOpposite() == getFacing(world.getBlockState(pos)) && isRedstoneOn(world, pos) ? 15 : 0;
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(DirUtils.FACING);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.FACING, facing);
    }
}
