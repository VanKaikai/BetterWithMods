package betterwithmods.event;

import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.gameplay.Gameplay;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/26/16
 */
@Mod.EventBusSubscriber
public class BlastingOilEvent {
    // TODO: Instead of disabling this module consider on performance tweaks for massive-multiplayer servers with A LOT of entities
        //TODO Make this a Feature in 1.13

    @SubscribeEvent
    public static void onPlayerTakeDamage(LivingHurtEvent e) {
        if (Gameplay.disableBlastingOilEvents)
            return;

        if(Gameplay.blacklistDamageSources.contains(e.getSource().damageType))
            return;

        DamageSource BLAST_OIL = new DamageSource("blastingoil");
        EntityLivingBase living = e.getEntityLiving();
        if (living.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler inventory = living.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if(inventory != null) {
                int count = 0;
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);

                if (!stack.isEmpty() && stack.isItemEqual(ItemMaterial.getStack(ItemMaterial.EnumMaterial.BLASTING_OIL))) {
                    count += stack.getCount();
                    inventory.extractItem(i, stack.getCount(), false);
                }
            }
            if (count > 0) {
                living.attackEntityFrom(BLAST_OIL, Float.MAX_VALUE);
                living.getEntityWorld().createExplosion(null, living.posX, living.posY + living.height / 16, living.posZ, (float) (Math.sqrt(count / 5) / 2.5 + 1), true);
            }
        }
    }

    private final static HashMap<EntityItem, Double> highestPoint = Maps.newHashMap();

    @SubscribeEvent
    public static void onHitGround(TickEvent.WorldTickEvent event) {
        if (Gameplay.disableBlastingOilEvents)
            return;
        World world = event.world;
        if (world.isRemote || event.phase != TickEvent.Phase.END)
            return;
        List<EntityItem> items = world.loadedEntityList.stream().filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem().isItemEqual(ItemMaterial.getStack(ItemMaterial.EnumMaterial.BLASTING_OIL))).map(e -> (EntityItem) e).collect(Collectors.toList());
        HashSet<EntityItem> toRemove = new HashSet<>();
        items.forEach(item -> {
            boolean ground = item.onGround;
            if (item.isBurning() || (ground && Math.abs(item.posY - highestPoint.getOrDefault(item, item.posY)) > 2.0)) {
                int count = item.getItem().getCount();
                if (count > 0) {
                    world.createExplosion(item, item.posX, item.posY + item.height / 16, item.posZ, (float) (Math.sqrt(count / 5) / 2.5 + 1), true);
                    toRemove.add(item);
                    item.setDead();
                }
            }
            if (item.motionY > 0 || !highestPoint.containsKey(item))
                highestPoint.put(item, item.posY);
        });
        toRemove.forEach(highestPoint::remove);
    }
}
