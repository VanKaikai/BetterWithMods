package betterwithmods.integration.minetweaker;

import betterwithmods.craft.TurntableInteraction;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.google.common.collect.Lists;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

import static betterwithmods.integration.minetweaker.Turntable.clazz;

/**
 * Purpose:
 *
 * @author Tyler Marshall
 * @version 3/2/17
 */
@ZenClass(clazz)
public class Turntable {
    public static final String clazz = "mods.betterwithmods.Turntable";
    @ZenMethod
    public static void addRecipe(IItemStack inputBlock, IItemStack outputBlock, IItemStack[] additionalOutput) {
        if(!InputHelper.isABlock(inputBlock))
            LogHelper.logError("Input must be a block", new IllegalArgumentException());
        MineTweakerAPI.apply(new Add(InputHelper.toStack(inputBlock),InputHelper.toStack(outputBlock), Lists.newArrayList(InputHelper.toStacks(additionalOutput))));
    }

    public static class Add extends BMAdd {
        public Add(ItemStack input, ItemStack output, List<ItemStack> scraps) {
            super("turntable", TurntableInteraction.INSTANCE, new TurntableInteraction.TurntableRecipe(input,output,scraps));
        }
    }

}
