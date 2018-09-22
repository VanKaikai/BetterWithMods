package betterwithmods.api.block;

import betterwithmods.module.general.MechanicalPower;
import betterwithmods.module.internal.SoundRegistry;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 7/18/17.
 */
public interface IOverpower {
    void overpower(World world, BlockPos pos);

    default void overpowerSound(World world, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            world.playSound(null, pos, SoundRegistry.BLOCK_MECHANICAL_OVERPOWER, SoundCategory.BLOCKS, 0.5f, world.rand.nextFloat() * 0.1F + 0.45F);
        }
        for (int i = 0; i < 5; i++) {
            float flX = pos.getX() + world.rand.nextFloat();
            float flY = pos.getY() + world.rand.nextFloat() * 0.5F + 1.0F;
            float flZ = pos.getZ() + world.rand.nextFloat();

            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, flX, flY, flZ, 0.0D, 0.0D, 0.0D);
        }
    }

    ResourceLocation getRegistryName();

    default boolean doesOverpower() {
        return MechanicalPower.doesMachineOverpower(getRegistryName());
    }
}
