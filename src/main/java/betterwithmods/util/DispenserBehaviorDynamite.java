package betterwithmods.util;

import betterwithmods.common.BWMItems;
import betterwithmods.common.entity.EntityDynamite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DispenserBehaviorDynamite extends BehaviorProjectileDispense {

    @Nonnull
    @Override
    protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
        return new EntityDynamite(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BWMItems.DYNAMITE, 1));
    }

}
