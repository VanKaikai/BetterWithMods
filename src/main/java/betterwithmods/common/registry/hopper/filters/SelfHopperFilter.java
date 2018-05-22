package betterwithmods.common.registry.hopper.filters;

import betterwithmods.BWMod;
import betterwithmods.api.tile.IHopperFilter;
import betterwithmods.client.model.filters.ModelTransparent;
import betterwithmods.client.model.filters.ModelWithResource;
import betterwithmods.client.model.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class SelfHopperFilter implements IHopperFilter {

    private Ingredient filter;

    public SelfHopperFilter(ItemStack stack) {
        this(Ingredient.fromStacks(stack));
    }

    public SelfHopperFilter(Ingredient filter) {
        this.filter = filter;
    }

    @Override
    public boolean allow(ItemStack stack) {
        return filter.apply(stack);
    }

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(BWMod.MODID, "self");
    }

    @Override
    public Ingredient getFilter() {
        return filter;
    }

    @Override
    public ModelWithResource getModelOverride(ItemStack filter) {
        return new ModelTransparent(RenderUtils.getResourceLocation(filter));
    }
}
