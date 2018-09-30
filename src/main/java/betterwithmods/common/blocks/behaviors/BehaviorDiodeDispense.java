package betterwithmods.common.blocks.behaviors;

import betterwithmods.BetterWithMods;
import betterwithmods.common.blocks.BlockBDispenser;
import betterwithmods.module.general.General;
import betterwithmods.util.DirUtils;
import betterwithmods.util.player.Profiles;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;

public class BehaviorDiodeDispense extends BehaviorDefaultDispenseItem {
    @Nonnull
    @Override
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        EnumFacing facing = source.getBlockState().getValue(BlockBDispenser.FACING);
        IPosition pos = BlockBDispenser.getDispensePosition(source);
        BlockPos check = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && stack.getItem() instanceof ItemBlockSpecial) {
            FakePlayer fake = FakePlayerFactory.get((WorldServer) source.getWorld(), Profiles.BWMDISP);
            fake.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
            DirUtils.setEntityOrientationFacing(fake, facing);
            if (General.isDebug())
                BetterWithMods.logger.debug("Better With Mods FakePlayer ID: " + fake.getUniqueID());
            if (stack.getItem().onItemUse(fake, source.getWorld(), check, EnumHand.MAIN_HAND, facing, 0.1F, 0.0F, 0.1F) == EnumActionResult.SUCCESS) {
                stack.shrink(1);
                return stack.isEmpty() ? ItemStack.EMPTY : stack;
            } else {
                //stack.grow(1);
                return stack;
            }
        } else {
            //stack.grow(1);
            return stack;
        }
    }
}
