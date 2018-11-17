package betterwithmods.module.tweaks;

import betterwithmods.library.common.modularity.impl.Feature;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class CactusSkeleton extends Feature {

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof AbstractSkeleton && event.getSource().damageType.equals(DamageSource.CACTUS.damageType)) {
            event.setCanceled(true);
        }
    }

    @Override
    public String getDescription() {
        return "Skeletons are no longer damaged by Cacti. Intended to make killing mobs in mob traps harder.";
    }

    @Override
    public boolean hasEvent() {
        return true;
    }

}
