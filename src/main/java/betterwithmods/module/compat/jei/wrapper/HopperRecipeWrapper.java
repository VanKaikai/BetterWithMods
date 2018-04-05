package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.common.registry.HopperInteractions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/20/16
 */
public class HopperRecipeWrapper implements IRecipeWrapper {

    public final HopperInteractions.HopperRecipe recipe;
    protected final List<ItemStack> input;
    protected final List<ItemStack> filter;
    protected final List<ItemStack> outputs;

    public HopperRecipeWrapper(HopperInteractions.HopperRecipe recipe) {
        this.recipe = recipe;
        this.input = Lists.newArrayList(recipe.getInput());
        this.outputs = Lists.newArrayList(recipe.getOutput());
        if (!recipe.getSecondaryOutput().isEmpty()) {
            this.outputs.addAll(recipe.getSecondaryOutput());
        }
        this.filter = Lists.newArrayList(BWRegistry.HOPPER_FILTERS.getFilter(recipe.getFilterType()).getFilter().getMatchingStacks());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, Lists.newArrayList(Iterables.concat(filter, input)));
        ingredients.setOutputs(ItemStack.class, outputs);
    }

    public static class SoulUrn extends HopperRecipeWrapper {
        public SoulUrn(HopperInteractions.SoulUrnRecipe recipe) {
            super(recipe);
            if (!recipe.getSecondaryOutput().isEmpty()) {
                this.input.add(BlockUrn.getStack(BlockUrn.EnumType.EMPTY, 1));
            }
        }
    }
}
