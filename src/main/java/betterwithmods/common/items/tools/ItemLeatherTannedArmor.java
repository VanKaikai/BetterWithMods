package betterwithmods.common.items.tools;

import betterwithmods.common.BWMOreDictionary;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class ItemLeatherTannedArmor extends BWMArmor {
    private static final ArmorMaterial LEATHER_TANNED = EnumHelper.addArmorMaterial("leather_tanned", "betterwithmods:leather_tanned", 10, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    public ItemLeatherTannedArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(LEATHER_TANNED, equipmentSlotIn, "leather_tanned");

    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, @Nonnull ItemStack repair) {
        return BWMOreDictionary.listContains(repair, OreDictionary.getOres("hideTanned")) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }
}