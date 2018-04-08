package betterwithmods.module.gameplay.miniblocks.blocks;

import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.orientations.ColumnOrientation;
import betterwithmods.module.gameplay.miniblocks.tiles.TileColumn;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockColumn extends BlockMini {

    public BlockColumn(Material material) {
        super(material);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileColumn();
    }

    @Override
    public BaseOrientation getOrientationFromPlacement(EntityLivingBase placer, @Nullable EnumFacing face, ItemStack stack, float hitX, float hitY, float hitZ) {
        if (face != null)
            return ColumnOrientation.getFromVec(new Vec3d(hitX, hitY, hitZ), face);
        return BaseOrientation.DEFAULT;
    }
}