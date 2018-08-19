package betterwithmods.module.recipes;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWMRegistry;
import betterwithmods.common.BWMSounds;
import betterwithmods.common.blocks.BlockRawPastry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.module.Feature;
import betterwithmods.util.ColorUtils;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreIngredient;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class MillRecipes extends Feature {
    private boolean grindingOnly;


    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected boolean canEnable() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

        grindingOnly = loadProperty("Grinding Only", true).setComment("Remove normal recipes for certain grindable items").get();

        if (grindingOnly) {
            BWMRecipes.removeRecipe(new ItemStack(Items.SUGAR));
            BWMRecipes.removeRecipe(new ItemStack(Items.BLAZE_POWDER));
            BWMRecipes.removeRecipe(StackIngredient.fromStacks(new ItemStack(Items.BEETROOT)));
            for (BlockIngredient flower : ColorUtils.FLOWER_TO_DYES.keySet()) {
                BWMRecipes.removeRecipe(flower);
            }
        }
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("netherrack"), Lists.newArrayList(ItemMaterial.getStack(ItemMaterial.EnumMaterial.GROUND_NETHERRACK)), BWMSounds.MILLSTONE_NETHERRACK);
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.BLAZE_ROD), Lists.newArrayList(new ItemStack(Items.BLAZE_POWDER, 3)), SoundEvents.ENTITY_BLAZE_DEATH);

        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(BWMBlocks.WOLF), Lists.newArrayList(new ItemStack(Items.STRING, 10), ColorUtils.getDye(EnumDyeColor.RED, 3)), SoundEvents.ENTITY_WOLF_WHINE);
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.REEDS), Lists.newArrayList(new ItemStack(Items.SUGAR, 2)));

        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropHemp"), ItemMaterial.getStack(ItemMaterial.EnumMaterial.HEMP_FIBERS, 3));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.COAL, 1), ItemMaterial.getStack(ItemMaterial.EnumMaterial.COAL_DUST));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.COAL, 1, 1), ItemMaterial.getStack(ItemMaterial.EnumMaterial.CHARCOAL_DUST));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.BONE), ColorUtils.getDye(EnumDyeColor.WHITE, 6));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.SKULL, 1, 0), ColorUtils.getDye(EnumDyeColor.WHITE, 10));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.SKULL, 1, 1), ColorUtils.getDye(EnumDyeColor.WHITE, 10));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Blocks.BONE_BLOCK), ColorUtils.getDye(EnumDyeColor.WHITE, 9));

        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.BEETROOT), ColorUtils.getDye(EnumDyeColor.RED, 2));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.LEATHER), ItemMaterial.getStack(ItemMaterial.EnumMaterial.SCOURED_LEATHER));
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.RABBIT_HIDE), ItemMaterial.getStack(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT));
        BWMRegistry.MILLSTONE.addMillRecipe(ItemMaterial.getStack(ItemMaterial.EnumMaterial.LEATHER_CUT), ItemMaterial.getStack(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT));
        for (BlockIngredient flower : ColorUtils.FLOWER_TO_DYES.keySet()) {
            BWMRegistry.MILLSTONE.addMillRecipe(flower, ColorUtils.FLOWER_TO_DYES.get(flower).getStack());
        }
        BWMRegistry.MILLSTONE.addMillRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()), ItemMaterial.getStack(ItemMaterial.EnumMaterial.COCOA_POWDER));
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropWheat"), BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropBarley"), BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropOats"), BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropRye"), BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
        BWMRegistry.MILLSTONE.addMillRecipe(new OreIngredient("cropRice"), BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
    }

}

