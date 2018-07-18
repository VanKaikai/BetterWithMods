package betterwithmods.common.registry;

import betterwithmods.api.tile.IHeated;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.advancements.BWAdvancements;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.common.tile.TileKiln;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by primetoxinz on 6/6/17.
 */
public class KilnStructureManager {

    public static final Set<IBlockState> KILN_BLOCKS = new HashSet<>();

    public static void registerKilnBlock(IBlockState state) {
        KILN_BLOCKS.add(state);
    }

    public static boolean isKilnBlock(IBlockState state) {
        if (state == Blocks.AIR.getDefaultState())
            return false;
        return KILN_BLOCKS.contains(state);
    }

    public static boolean createKiln(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (!isKilnBlock(state))
            return false;
        if (isValidKiln(world, pos)) {
            IBlockState kiln = BWMBlocks.KILN.getDefaultState();
            world.setBlockState(pos, kiln);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileKiln) {
                ((TileKiln) tile).setState(state);
                world.notifyBlockUpdate(pos, kiln, kiln, 8);
                //TRIGGER ADVANCEMENT
                world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(10.0D, 5.0D, 10.0D)).forEach(BWAdvancements.CONSTRUCT_KILN::trigger);
            }
            return true;
        }
        return false;
    }

    public static boolean isValidRecipe(World world, BlockPos pos, KilnRecipe recipe) {
        return recipe.canCraft(getKiln(), world, pos);
    }

    public static boolean isValidKiln(IBlockAccess world, BlockPos pos) {
        int numBrick = 0;
        BlockPos center = pos.up();
        for (EnumFacing face : EnumFacing.VALUES) {
            if (face == EnumFacing.DOWN) continue;
            IBlockState state = world.getBlockState(center.offset(face));
            if (isKilnBlock(state))
                numBrick++;
        }
        return numBrick > 2;
    }

    public static void removeKilnBlock(IBlockState state) {
        KILN_BLOCKS.remove(state);
    }

    public static Set<IBlockState> getKilnBlocks() {
        return KILN_BLOCKS;
    }

    public static Kiln getKiln() {
        return new Kiln();
    }

    public static class Kiln implements IHeated {
        @Override
        public int getHeat(World world, BlockPos pos) {
            return BWMHeatRegistry.getHeat(world, pos.down());
        }


        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public BlockPos getPos() {
            return null;
        }

        @Override
        public ItemStackHandler getInventory() {
            return null;
        }
    }
}
