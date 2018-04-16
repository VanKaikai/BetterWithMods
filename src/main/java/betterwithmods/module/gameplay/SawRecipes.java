package betterwithmods.module.gameplay;

import betterwithmods.BWMod;
import betterwithmods.api.util.IBlockVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockDropIngredient;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.common.registry.crafting.ChoppingRecipe;
import betterwithmods.module.Feature;
import betterwithmods.module.hardcore.crafting.HCLumber;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class SawRecipes extends Feature {
    public SawRecipes() {
        canDisable = false;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.PUMPKIN, 0, OreDictionary.WILDCARD_VALUE));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.VINE));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.YELLOW_FLOWER));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.BROWN_MUSHROOM));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_MUSHROOM));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(BWMBlocks.ROPE));
        for (int i = 0; i < 9; i++)
            BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_FLOWER, 1, i));
        BWRegistry.WOOD_SAW.addRecipe(new SawRecipe(new BlockIngredient(new ItemStack(Blocks.MELON_BLOCK)), NonNullList.create()) {
            @Override
            public NonNullList<ItemStack> getOutputs() {
                Random random = new Random();
                return InvUtils.asNonnullList(new ItemStack(Items.MELON, 3 + random.nextInt(5)));
            }
        });

        BWOreDictionary.findLogRecipes();
        //TODO configure this
        BWOreDictionary.logRecipes.forEach(BWMRecipes::removeRecipe);
        int plankCount = BWMod.MODULE_LOADER.isFeatureEnabled(HCLumber.class) ? HCLumber.axePlankAmount: 4;
        if (!Loader.isModLoaded("primal")) {
            for(IBlockVariants variant: BWOreDictionary.blockVariants) {
                ItemStack log = variant.getVariant(IBlockVariants.EnumBlock.LOG,1);
                if(!log.isEmpty()) {
                    ResourceLocation location = new ResourceLocation(BWMod.MODID, log.getItem().getRegistryName() + "_" + log.getMetadata());
                    BWMRecipes.addRecipe(new ChoppingRecipe(variant, plankCount).setRegistryName(location));
                }
            }
        }

}

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        int count = BWMod.MODULE_LOADER.isFeatureEnabled(HCLumber.class) ? 4 : 6;
        for (IBlockVariants wood : BWOreDictionary.blockVariants) {
            BWRegistry.WOOD_SAW.addRecipe(new BlockDropIngredient(wood.getVariant(IBlockVariants.EnumBlock.LOG, 1)), Lists.newArrayList(wood.getVariant(IBlockVariants.EnumBlock.BLOCK, count), wood.getVariant(IBlockVariants.EnumBlock.BARK, 1), wood.getVariant(IBlockVariants.EnumBlock.SAWDUST, 2)));
        }
    }
    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
