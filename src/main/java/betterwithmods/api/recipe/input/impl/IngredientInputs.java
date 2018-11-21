package betterwithmods.api.recipe.input.impl;

import betterwithmods.api.recipe.input.IRecipeContext;
import betterwithmods.api.recipe.input.IRecipeInputs;
import betterwithmods.api.recipe.output.IRecipeOutputs;
import betterwithmods.api.recipe.output.impl.ListOutputs;
import betterwithmods.library.utils.InventoryUtils;
import betterwithmods.library.utils.ListUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class IngredientInputs implements IRecipeInputs {

    private NonNullList<Ingredient> ingredients;
    private boolean containers = true;

    public IngredientInputs(Ingredient... ingredients) {
        this.ingredients = ListUtils.asNonnullList(ingredients);
    }

    public IngredientInputs(List<Ingredient> ingredients) {
        this(ListUtils.asNonnullList(ingredients));
    }

    public IngredientInputs(NonNullList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int orderedMatch(IRecipeContext context) {
        int index = Integer.MAX_VALUE;
        for (Ingredient ingredient : ingredients) {
            if ((index = Math.min(index, InventoryUtils.getFirstOccupiedStackOfItem(context.getInventory(), ingredient))) == -1)
                return -1;
        }
        return index;
    }

    @Override
    public boolean match(IRecipeContext context) {
        for (Ingredient ingredient : ingredients) {
            if (InventoryUtils.getFirstOccupiedStackOfItem(context.getInventory(), ingredient) == -1)
                return false;
        }
        return true;
    }

    @Override
    public IRecipeOutputs consume(IRecipeContext context) {
        IItemHandler inventory = context.getInventory();
        NonNullList<ItemStack> containItems = NonNullList.create();
        for (Ingredient ingredient : ingredients) {
            if (!InventoryUtils.consumeItemsInInventory(inventory, ingredient, false, containItems))
                return null;
        }
        //this is dumb :P
        if (handleContainers())
            return new ListOutputs(containItems);
        return new ListOutputs();
    }

    @Override
    public boolean isInvalid() {
        return ingredients.isEmpty() || ingredients.stream().noneMatch(InventoryUtils::isIngredientValid);
    }

    @Override
    public NonNullList<Ingredient> getInputs() {
        return ingredients;
    }

    @Override
    public boolean handleContainers() {
        return containers;
    }

    @Override
    public IRecipeInputs disableContainers() {
        this.containers = false;
        return this;
    }
}
