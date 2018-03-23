package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 5/10/17.
 */
public class HarderSteelRecipe extends Feature {

    @Override
    public void setupConfig() {
        super.setupConfig();
    }

    @Override
    public String getFeatureDescription() {
        return "Whether Steel requires End Slag, a material only available after the End.";
    }

    @Override
    public void init(FMLInitializationEvent event) {
        CauldronRecipes.addStokedCauldronRecipe(ItemMaterial.getStack(ItemMaterial.EnumMaterial.BRIMSTONE), ItemMaterial.getStack(ItemMaterial.EnumMaterial.SOUL_FLUX), new Object[]{ItemMaterial.getStack(ItemMaterial.EnumMaterial.ENDER_SLAG)});
        KilnRecipes.addKilnRecipe(Blocks.END_STONE, 0, BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE), ItemMaterial.getStack(ItemMaterial.EnumMaterial.ENDER_SLAG));
        CrucibleRecipes.addStokedCrucibleRecipe(ItemMaterial.getStack(ItemMaterial.EnumMaterial.STEEL_INGOT), new ItemStack(BWMBlocks.URN, 1, 0), new Object[]{"dustCarbon", new ItemStack(BWMBlocks.URN, 1, 8), "ingotIron", ItemMaterial.getStack(ItemMaterial.EnumMaterial.SOUL_FLUX)});


    }

    @Override
    public void disabledInit(FMLInitializationEvent event) {
        CauldronRecipes.addStokedCauldronRecipe(ItemMaterial.getStack(ItemMaterial.EnumMaterial.BRIMSTONE), new Object[]{ItemMaterial.getStack(ItemMaterial.EnumMaterial.ENDER_SLAG)});
        KilnRecipes.addKilnRecipe(Blocks.END_STONE, 0, BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE), ItemMaterial.getStack(ItemMaterial.EnumMaterial.BRIMSTONE));
        CrucibleRecipes.addStokedCrucibleRecipe(ItemMaterial.getStack(ItemMaterial.EnumMaterial.STEEL_INGOT), new ItemStack(BWMBlocks.URN, 1, 0), new Object[]{"dustCarbon", new ItemStack(BWMBlocks.URN, 1, 8), "ingotIron"});

    }
}
