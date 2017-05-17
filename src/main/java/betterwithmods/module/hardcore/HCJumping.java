package betterwithmods.module.hardcore;

import betterwithmods.module.Feature;
import betterwithmods.util.player.EntityPlayerExt;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by tyler on 5/3/17.
 */
public class HCJumping extends Feature {

    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent.RightClickBlock e) {
        if(!EntityPlayerExt.isSurvival(e.getEntityPlayer()))
            return;
        if(e.getItemStack().getItem() instanceof ItemBlock && e.getEntityPlayer().isAirBorne) {
            e.setResult(Event.Result.DENY);
            e.setCanceled(true);
        }
    }
    @Override
    public String getFeatureDescription() {
        return "Stops the ability to place blocks while in the air. This stops the use of 'Derp Pillars' to escape attacks";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
