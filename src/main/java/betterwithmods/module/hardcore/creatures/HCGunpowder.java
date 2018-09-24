package betterwithmods.module.hardcore.creatures;

import betterwithmods.common.items.ItemMaterial;
import betterwithmods.library.modularity.impl.Feature;
import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by primetoxinz on 4/20/17.
 */
@Mod.EventBusSubscriber
public class HCGunpowder extends Feature {
    public static List<Class> disableGunpowder = Lists.newArrayList();

    @Override
    public void onInit(FMLInitializationEvent event) {

        String[] array = loadProperty("Disable Gunpowder Drop",  new String[]{
                "net.minecraft.entity.monster.EntityCreeper",
                "net.minecraft.entity.monster.EntityGhast",
                "net.minecraft.entity.monster.EntityWitch",
                "betterwithmods.common.entity.EntityShearedCreeper"
        }).setComment("List of entity classes which gunpowder will be replaced with niter").get();
        disableGunpowder = Arrays.stream(array).map(clazz -> {
            try {
                return Class.forName(clazz);
            } catch (ClassNotFoundException ignore) {
            }
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Makes a raw resource drop that must be crafted to make useful gunpowder";
    }

    @SubscribeEvent
    public static void mobDrops(LivingDropsEvent evt) {
        boolean contained = false;
        for (Class clazz : disableGunpowder) {
            if (evt.getEntity().getClass().isAssignableFrom(clazz)) {
                contained = true;
                break;
            }
        }
        if (contained) {
            for (EntityItem item : evt.getDrops()) {
                ItemStack stack = item.getItem();
                if (stack.getItem() == Items.GUNPOWDER) {
                    item.setItem(ItemMaterial.getStack(ItemMaterial.EnumMaterial.NITER, stack.getCount()));
                }
            }
        }
    }

}
