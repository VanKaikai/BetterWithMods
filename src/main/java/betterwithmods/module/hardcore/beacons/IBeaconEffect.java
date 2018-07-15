package betterwithmods.module.hardcore.beacons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by primetoxinz on 7/17/17.
 */
public interface IBeaconEffect {

    int[] radii = new int[]{20, 40, 80, 160};

    static void forEachPlayersAround(World world, BlockPos pos, int level, Consumer<? super EntityLivingBase> player) {
        forEachEntityAround(EntityPlayer.class, world, pos, level, player);
    }

    static void forEachEntityAround(Class<? extends EntityLivingBase> clazz, World world, BlockPos pos, int level, Consumer<? super EntityLivingBase> consumer) {
        int r = radii[Math.min(level - 1, 3)];
        AxisAlignedBB box = new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(r);
        List<? extends EntityLivingBase> entities = world.getEntitiesWithinAABB(clazz, box);
        entities.forEach(consumer);
    }

    void effect(World world, BlockPos pos, int level);

    default boolean processInteractions(World world, BlockPos pos, int level, EntityPlayer player, ItemStack stack) {
        return false;
    }

    default void breakBlock(World world, BlockPos pos, int level) {

    }

    default int getTickSpeed() {
        return 120;
    }
}
