package betterwithmods.common.blocks;

import betterwithmods.util.DirUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by primetoxinz on 7/21/17.
 */
public class BlockBrokenGearbox extends BWMBlock {
    public static final PropertyInteger REPAIR = PropertyInteger.create("repair", 0, 1);

    public EnumTier type;

    public BlockBrokenGearbox(EnumTier type) {
        super(Material.WOOD);
        this.type = type;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(DirUtils.FACING,facing);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, REPAIR);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DirUtils.FACING, EnumFacing.VALUES[meta & 3]).withProperty(REPAIR, (meta >> 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(DirUtils.FACING).getIndex();
        int type = state.getValue(REPAIR);
        return facing | type << 3;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        if (type == EnumTier.STEEL)
            return 4000f;
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        if (type == EnumTier.STEEL)
            return 100f;
        return 3.5f;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (type == EnumTier.STEEL)
            return SoundType.METAL;
        return SoundType.WOOD;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        if (type == EnumTier.STEEL)
            return Material.IRON;
        return Material.WOOD;
    }


}
