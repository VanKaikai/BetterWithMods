package betterwithmods.module.internal;

import betterwithmods.BetterWithMods;
import betterwithmods.common.BWMOreDictionary;
import betterwithmods.common.registry.anvil.CraftingManagerAnvil;
import betterwithmods.common.registry.block.managers.CrafingManagerKiln;
import betterwithmods.common.registry.block.managers.CraftingManagerSaw;
import betterwithmods.common.registry.block.managers.CraftingManagerTurntable;
import betterwithmods.common.registry.bulk.manager.CraftingManagerMill;
import betterwithmods.common.registry.bulk.manager.CraftingManagerPot;
import betterwithmods.common.registry.hopper.filters.HopperFilters;
import betterwithmods.common.registry.hopper.manager.CraftingManagerHopper;
import betterwithmods.library.modularity.impl.RequiredFeature;
import betterwithmods.library.recipes.RecipeRemover;
import betterwithmods.library.utils.InventoryUtils;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.List;

public class RecipeRegistry extends RequiredFeature {

    public static final CraftingManagerPot CAULDRON = new CraftingManagerPot();
    public static final CraftingManagerPot CRUCIBLE = new CraftingManagerPot();
    public static final CraftingManagerMill MILLSTONE = new CraftingManagerMill();
    public static final CraftingManagerSaw WOOD_SAW = new CraftingManagerSaw();
    public static final CrafingManagerKiln KILN = new CrafingManagerKiln();
    public static final CraftingManagerTurntable TURNTABLE = new CraftingManagerTurntable();
    public static final CraftingManagerHopper FILTERED_HOPPER = new CraftingManagerHopper();
    public static final CraftingManagerAnvil ANVIL = new CraftingManagerAnvil();
    public static final HopperFilters HOPPER_FILTERS = new HopperFilters();

    private static final List<RecipeRemover<?>> RECIPE_REMOVERS = Lists.newArrayList();


    private static final List<IRecipe> RECIPE_ADDITIONS = Lists.newArrayList();

    /**
     * @param recipe recipe to be registered
     * Must be called during preinit phase
     */
    public static void addRecipe(IRecipe recipe) {
        RECIPE_ADDITIONS.add(recipe);
    }


    /**
     * @param remover recipe remove to match the IRecipes that should be removed
     * Must be called during preinit phase
     */
    public static <T> void removeRecipe(RecipeRemover<T> remover) {
        RECIPE_REMOVERS.add(remover);
    }

    public static void removeFurnaceRecipe(Item input) {
        removeFurnaceRecipe(new ItemStack(input));
    }

    public static boolean removeFurnaceRecipe(ItemStack input) {
        //for some reason mojang put fucking wildcard for their ore meta
        return FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(next -> InventoryUtils.matches(next.getKey(), input));
    }

    @Override
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        //Gather oredictionary
        //TODO should be obsolute in 1.13
        BWMOreDictionary.registerOres();
        BWMOreDictionary.oreGathering();

        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>) event.getRegistry();
        for (IRecipe recipe : reg) {
            for (RecipeRemover remover : RECIPE_REMOVERS) {
                if (remover.apply(recipe)) {
                    reg.remove(recipe.getRegistryName());
                    BetterWithMods.LOGGER.info("Removing Recipe: {}", recipe.getRegistryName());
                }
            }
        }
        reg.registerAll(RECIPE_ADDITIONS.toArray(new IRecipe[0]));
    }
}

