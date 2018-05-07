package betterwithmods.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class InfernalEnchantment extends Enchantment {
    private Enchantment enchantment;

    public InfernalEnchantment(Enchantment enchantment) {
        super(enchantment.getRarity(), enchantment.type, getSlots(enchantment));
        this.enchantment = enchantment;
    }

    private static EntityEquipmentSlot[] getSlots(Enchantment enchantment) {
        return ReflectionHelper.getPrivateValue(Enchantment.class, enchantment, "field_185263_a", "applicableEquipmentTypes");
    }
}
