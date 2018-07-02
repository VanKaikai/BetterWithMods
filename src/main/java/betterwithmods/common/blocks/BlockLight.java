package betterwithmods.common.blocks;

import betterwithmods.util.item.ToolsManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockLight extends BWMBlock {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockLight() {
        super(Material.GLASS);
        this.setHardness(2.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, true));
        this.setSoundType(SoundType.GLASS);
        ToolsManager.setPickaxesAsEffectiveAgainst(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(ACTIVE))
            return 15;
        return 0;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            if (state.getValue(ACTIVE) && !world.isBlockPowered(pos) && !isIndirectlyPowered(world, pos))
                world.setBlockState(pos, state.withProperty(ACTIVE, false));
            else if (!state.getValue(ACTIVE) && (world.isBlockPowered(pos) || isIndirectlyPowered(world, pos)))
                world.setBlockState(pos, state.withProperty(ACTIVE, true));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if (!world.isRemote) {
            if (state.getValue(ACTIVE) && !world.isBlockPowered(pos) && !isIndirectlyPowered(world, pos))
                world.scheduleUpdate(pos, this, 4);
            else if (!state.getValue(ACTIVE) && (world.isBlockPowered(pos) || isIndirectlyPowered(world, pos))) {
                world.setBlockState(pos, state.withProperty(ACTIVE, true), 2);
                for (EnumFacing facing : EnumFacing.VALUES) {
                    if (world.getBlockState(pos.offset(facing)).getBlock() == this)
                        world.neighborChanged(pos.offset(facing), this, pos);
                }
            }
        }
    }

    private boolean isIndirectlyPowered(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos check = pos.offset(facing);
            if (!world.isAirBlock(check) && !(world.getBlockState(check).getBlock() instanceof BlockBUD) && world.isBlockPowered(check))
                return true;
        }
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (state.getValue(ACTIVE) && !world.isBlockPowered(pos)) {
                world.setBlockState(pos, state.withProperty(ACTIVE, false), 2);
                for (EnumFacing facing : EnumFacing.VALUES) {
                    if (world.getBlockState(pos.offset(facing)).getBlock() == this)
                        world.neighborChanged(pos.offset(facing), this, pos);
                }
            }
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, meta == 1);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        IBlockState neighbor = world.getBlockState(pos.offset(side));
        return state != neighbor;
    }
}
