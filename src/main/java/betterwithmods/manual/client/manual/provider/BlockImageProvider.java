package betterwithmods.manual.client.manual.provider;

import betterwithmods.manual.api.API;
import betterwithmods.manual.api.manual.ImageProvider;
import betterwithmods.manual.api.manual.ImageRenderer;
import betterwithmods.manual.client.manual.segment.render.ItemStackImageRenderer;
import betterwithmods.manual.client.manual.segment.render.MissingItemRenderer;
import com.google.common.base.Strings;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BlockImageProvider implements ImageProvider {
    private static final String WARNING_BLOCK_MISSING = API.MOD_ID + ".manual.warning.missing.block";

    @Override
    @Nullable
    public ImageRenderer getImage(@Nonnull final String data) {
        final int splitIndex = data.lastIndexOf('@');
        final String name, optMeta;
        if (splitIndex > 0) {
            name = data.substring(0, splitIndex);
            optMeta = data.substring(splitIndex);
        } else {
            name = data;
            optMeta = "";
        }
        final int meta = (Strings.isNullOrEmpty(optMeta)) ? 0 : Integer.parseInt(optMeta.substring(1));
        final Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
        if (Item.getItemFromBlock(block) != Items.AIR) {
            return new ItemStackImageRenderer(new ItemStack(block, 1, meta));
        } else {
            return new MissingItemRenderer(WARNING_BLOCK_MISSING);
        }
    }

    //TODO - remove this, meta is being removed in 1.13!
    @Deprecated
    public int parseMeta(String optMeta) {
        if (optMeta.equals("*")) {
            return OreDictionary.WILDCARD_VALUE;
        } else {
            return Integer.parseInt(optMeta);
        }
    }
}
