package betterwithmods.module.tweaks;

import betterwithmods.common.entity.ai.EntityAIMate;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.library.utils.EntityUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class AnimalBirth extends Feature {

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent evt) {
        if (evt.getEntity() instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) evt.getEntity();
            if (entity instanceof EntityAnimal && EntityUtils.hasAI(entity, net.minecraft.entity.ai.EntityAIMate.class)) {
                EntityUtils.removeTask(entity, net.minecraft.entity.ai.EntityAIMate.class);
                entity.tasks.addTask(0, new EntityAIMate((EntityAnimal) entity, 1.0D, 25d));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Make born animals spawn between their parents for easier automation.";
    }

    @Override
    public boolean hasEvent() {
        return true;
    }

}
