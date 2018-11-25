package betterwithmods.common.items.tools;

import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMOreDictionary;
import betterwithmods.library.utils.ToolUtils;
import betterwithmods.module.general.InfernalEnchanting;
import betterwithmods.module.internal.ItemRegistry;
import betterwithmods.util.PlayerUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ItemSoulforgedMattock extends ItemTool {

    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL,
            Material.GROUND, Material.GRASS, Material.CLAY, Material.GLASS, Material.PISTON, Material.SNOW);

    public ItemSoulforgedMattock() {
        super(ItemRegistry.SOULFORGED_STEEL, Sets.newHashSet());
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, @Nonnull ItemStack repair) {
        return BWMOreDictionary.listContains(repair, OreDictionary.getOres("ingotSoulforgedSteel")) || super.getIsRepairable(toRepair, repair);
    }


    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, IBlockState state) {
        if (PlayerUtils.isCurrentToolEffectiveOnBlock(stack, state, EFFECTIVE_MATERIALS))
            return efficiency;
        return ToolUtils.getMaxDestorySpeed(stack, state, (ItemTool) BWMItems.STEEL_PICKAXE, (ItemTool) BWMItems.STEEL_SHOVEL);
    }

    @Nonnull
    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("bwmmattock", "pickaxe", "shovel");
    }

    public boolean canHarvestBlock(IBlockState blockIn) {
        Block block = blockIn.getBlock();
        return toolMaterial.getHarvestLevel() >= block.getHarvestLevel(blockIn);
    }


    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return InfernalEnchanting.canEnchantSteel(enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, @Nonnull String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        return toolMaterial.getHarvestLevel();
    }
}
