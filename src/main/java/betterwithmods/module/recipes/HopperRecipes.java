package betterwithmods.module.recipes;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.output.impl.ChanceOutput;
import betterwithmods.api.recipe.output.impl.ListOutputs;
import betterwithmods.api.recipe.output.impl.WeightedOutputs;
import betterwithmods.client.model.filters.ModelGrate;
import betterwithmods.client.model.filters.ModelSlats;
import betterwithmods.client.model.filters.ModelWithResource;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMOreDictionary;
import betterwithmods.common.BWMRegistry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.block.recipe.IngredientSpecial;
import betterwithmods.common.registry.hopper.filters.HopperFilter;
import betterwithmods.common.registry.hopper.filters.SoulsandFilter;
import betterwithmods.common.registry.hopper.recipes.HopperRecipe;
import betterwithmods.common.registry.hopper.recipes.SoulUrnRecipe;
import betterwithmods.common.tile.TileFilteredHopper;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreIngredient;

/**
 * Created by primetoxinz on 6/23/17.
 */
public class HopperRecipes extends Feature {
    public static boolean useSelfFiltering;

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected boolean canEnable() {
        return true;
    }


    @Override
    public void onInit(FMLInitializationEvent event) {
        boolean brimstoneFiltering = loadProperty("Glowstone Filtering", true).setComment("Passing glowstone through a soulsand filter makes brimstone.").get();
        useSelfFiltering = loadProperty("Self Filtering", false).setComment("Allow the Hopper to filter by the item in the filter slot.").get();
        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "ladder"), StackIngredient.fromStacks(new ItemStack(Blocks.LADDER)), Lists.newArrayList(
                new IngredientSpecial(stack -> !(stack.getItem() instanceof ItemBlock)),
                new OreIngredient("treeSapling")
        )));

        BWMRegistry.HOPPER_FILTERS.addFilter(new SoulsandFilter(StackIngredient.fromStacks(new ItemStack(Blocks.SOUL_SAND)), Lists.newArrayList(StackIngredient.fromStacks(new ItemStack(Blocks.SOUL_SAND)))));

        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "wicker"), StackIngredient.fromStacks(new ItemStack(BWMBlocks.WICKER)), Lists.newArrayList(
                new OreIngredient("sand"),
                new OreIngredient("listAllseeds"),
                new OreIngredient("foodFlour"),
                new OreIngredient("pile"),
                new IngredientSpecial(stack -> BWMOreDictionary.dustNames.stream().anyMatch(ore -> ore.apply(stack)))
        )));

        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "trapdoor"), StackIngredient.fromStacks(new ItemStack(Blocks.TRAPDOOR)), Lists.newArrayList(
                new IngredientSpecial(stack -> stack.getItem() instanceof ItemBlock)
        )));

        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "grates"), new OreIngredient("grates"), Lists.newArrayList(
                new IngredientSpecial(stack -> stack.getMaxStackSize() == 1)
        )) {
            @Override
            public ModelWithResource getModelOverride(ItemStack filter) {
                return new ModelGrate(filter);
            }
        });

        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "iron_bar"), StackIngredient.fromStacks(new ItemStack(Blocks.IRON_BARS)), Lists.newArrayList(
                new IngredientSpecial(stack -> stack.getMaxStackSize() > 1)
        )));

        BWMRegistry.HOPPER_FILTERS.addFilter(new HopperFilter(new ResourceLocation(BWMod.MODID, "slats"), new OreIngredient("slats"),
                BWMOreDictionary.fromOres(
                        "paper",
                        "scroll",
                        "string",
                        "fiberHemp",
                        "hideTanned",
                        "hideTanned",
                        "hideBelt",
                        "hideScoured",
                        "hideStrap",
                        "leather",
                        "wool"
                )) {
            @Override
            public ModelWithResource getModelOverride(ItemStack filter) {
                return new ModelSlats(filter);
            }
        });

        BWMRegistry.FILTERED_HOPPER.addRecipe(new HopperRecipe(BWMod.MODID + ":wicker", new OreIngredient("dustRedstone"), Lists.newArrayList(new ItemStack(Items.GLOWSTONE_DUST)), Lists.newArrayList(new ItemStack(Items.GUNPOWDER))));

        BWMRegistry.FILTERED_HOPPER.addRecipe(new SoulUrnRecipe(new OreIngredient("dustNetherrack"), ItemStack.EMPTY, ItemMaterial.getStack(ItemMaterial.EnumMaterial.HELLFIRE_DUST)));
        BWMRegistry.FILTERED_HOPPER.addRecipe(new SoulUrnRecipe(new OreIngredient("dustSoul"), ItemStack.EMPTY, ItemMaterial.getStack(ItemMaterial.EnumMaterial.SAWDUST)));
        if (brimstoneFiltering)
            BWMRegistry.FILTERED_HOPPER.addRecipe(new SoulUrnRecipe(new OreIngredient("dustGlowstone"), ItemStack.EMPTY, ItemMaterial.getStack(ItemMaterial.EnumMaterial.BRIMSTONE)));

        BWMRegistry.FILTERED_HOPPER.addRecipe(new HopperRecipe(BWMod.MODID + ":wicker",
                Ingredient.fromStacks(new ItemStack(Blocks.GRAVEL)),
                new WeightedOutputs(new ChanceOutput(new ItemStack(Blocks.SAND), 0.5), new ChanceOutput(new ItemStack(Blocks.SAND, 1, 1), 0.5)),
                new ListOutputs(new ItemStack(Items.FLINT))
        ));

        BWMRegistry.FILTERED_HOPPER.addRecipe(new HopperRecipe(BWMod.MODID + ":soul_sand", new OreIngredient("sand"), ItemStack.EMPTY, new ItemStack(Blocks.SOUL_SAND)) {
            @Override
            protected boolean canCraft(World world, BlockPos pos, TileFilteredHopper tile) {
                return super.canCraft(world, pos, tile) && tile.soulsRetained > 0;
            }

            @Override
            public void onCraft(World world, BlockPos pos, EntityItem item, TileFilteredHopper tile) {
                if (tile != null)
                    tile.decreaseSoulCount(1);
                super.onCraft(world, pos, item, tile);
            }
        });
    }
}

