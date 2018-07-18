package betterwithmods.common.entity.ai.eat;

import betterwithmods.module.hardcore.creatures.chicken.EggLayer;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;

public class EntityAIAnimalEat extends EntityAIEatFood<EntityAnimal> {

    public EntityAIAnimalEat(EntityAnimal entity, Ingredient validItem, double distance) {
        super(entity, validItem, distance);
    }

    @Override
    public boolean isReady() {
        return canBreed(entity);
    }

    @Override
    public void onEaten(ItemStack food) {
        EggLayer layer = entity.getCapability(EggLayer.EGG_LAYER_CAP, EnumFacing.DOWN);
        if (layer != null) {
            layer.feed(entity, food);
        } else {
            entity.setInLove(null);
            food.shrink(1);
        }
    }

    private boolean canBreed(EntityAnimal entity) {
        //Handle HCChickens
        EggLayer layer = entity.getCapability(EggLayer.EGG_LAYER_CAP, EnumFacing.DOWN);
        if (layer != null) {
            return !layer.isFeed();
        }

        //Handle tamed animals
        if (entity instanceof EntityTameable) {
            return ((EntityTameable) entity).isTamed() && !((EntityTameable) entity).isSitting();
        }

        //Only adults that are read to breed
        if (!entity.isChild()) {
            return !entity.isInLove();
        }
        return false;
    }
}