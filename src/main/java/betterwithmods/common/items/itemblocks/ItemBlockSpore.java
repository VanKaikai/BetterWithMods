package betterwithmods.common.items.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by primetoxinz on 6/25/17.
 */
public class ItemBlockSpore extends ItemBlock {

    public ItemBlockSpore(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
        if (!world.provider.isNether())
            return false;
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
