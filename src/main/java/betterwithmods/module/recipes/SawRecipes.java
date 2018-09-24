package betterwithmods.module.recipes;

import betterwithmods.api.recipe.output.impl.RandomCountOutputs;
import betterwithmods.api.recipe.output.impl.RandomOutput;
import betterwithmods.api.util.IBlockVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMOreDictionary;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWMRegistry;
import betterwithmods.library.utils.ingredient.BlockDropIngredient;
import betterwithmods.library.utils.ingredient.BlockIngredient;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.common.registry.crafting.ChoppingRecipe;
import betterwithmods.lib.ModLib;
import betterwithmods.library.modularity.impl.Feature;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class SawRecipes extends Feature {

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


        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.PUMPKIN, 0, OreDictionary.WILDCARD_VALUE));
        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.VINE));
        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.YELLOW_FLOWER));
        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.BROWN_MUSHROOM));
        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_MUSHROOM));
        BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(BWMBlocks.ROPE));
        for (int i = 0; i < 9; i++)
            BWMRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_FLOWER, 1, i));

        BWMRegistry.WOOD_SAW.addRecipe(new SawRecipe(new BlockIngredient(new ItemStack(Blocks.MELON_BLOCK)), new RandomCountOutputs(new RandomOutput(new ItemStack(Items.MELON), 3, 8))));

        BWMOreDictionary.findLogRecipes();
        //TODO configure this
        BWMOreDictionary.logRecipes.forEach(BWMRecipes::removeRecipe);
        if (!Loader.isModLoaded("primal")) {
            for (IBlockVariants variant : BWMOreDictionary.blockVariants) {
                ItemStack log = variant.getVariant(IBlockVariants.EnumBlock.LOG, 1);
                if (!log.isEmpty()) {
                    ResourceLocation location = new ResourceLocation(ModLib.MODID, log.getItem().getRegistryName().getPath() + "_" + log.getMetadata());
                    BWMRecipes.addRecipe(new ChoppingRecipe(variant).setRegistryName(location));
                }
            }
        }
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
        int plankCount = loadProperty("Saw Plank Output", 4).setComment("Plank count that is output when a log is chopped by a Saw.").get();
        int barkCount = loadProperty("Saw Bark Output", 1).setComment("Bark count that is output when a log is chopped by a Saw.").get();
        int sawDustCount = loadProperty("Saw sawdust Output", 2).setComment("Sawdust count that is output when a log is chopped by a Saw.").get();
        for (IBlockVariants wood : BWMOreDictionary.blockVariants) {
            BWMRegistry.WOOD_SAW.addRecipe(new BlockDropIngredient(wood.getVariant(IBlockVariants.EnumBlock.LOG, 1)), Lists.newArrayList(wood.getVariant(IBlockVariants.EnumBlock.BLOCK, plankCount), wood.getVariant(IBlockVariants.EnumBlock.BARK, barkCount), wood.getVariant(IBlockVariants.EnumBlock.SAWDUST, sawDustCount)));
        }
    }

}