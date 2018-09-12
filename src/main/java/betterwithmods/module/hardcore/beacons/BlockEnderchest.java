package betterwithmods.module.hardcore.beacons;

import betterwithmods.util.CapabilityUtils;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockEnderchest extends BlockEnderChest {

    public BlockEnderchest() {
        setTranslationKey("enderChest");
    }

    @Override
    public boolean onBlockActivated(World world, @Nonnull BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.getBlockState(pos).getBlock().equals(Blocks.ENDER_CHEST)) {
            if (!world.isRemote && world.getTileEntity(pos) instanceof TileEnderchest) {
                TileEnderchest tile = (TileEnderchest) world.getTileEntity(pos);
                if (tile != null) {
                    InventoryEnderChest chest = tile.getType().getFunction().apply(tile, player);
                    if (!world.getBlockState(pos.up()).isNormalCube()) {
                        chest.setChestTileEntity(tile);
                        player.displayGUIChest(chest);
                        player.addStat(StatList.ENDERCHEST_OPENED);
                    }
                }

            }
        }
        return true;
    }


    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEnderchest && ((TileEnderchest) tile).getType() == TileEnderchest.Type.NONE) {
            EnderchestCap chest = CapabilityUtils.getCapability(tile, EnderchestCap.ENDERCHEST_CAPABILITY, EnumFacing.UP).orElse(null);
            if (chest != null) {
                InventoryHelper.dropInventoryItems(worldIn, pos, chest.getInventory());
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnderchest();
    }
}
