package betterwithmods.module.gameplay.miniblocks.client;

import betterwithmods.client.baking.ModelFactory;
import betterwithmods.client.baking.WrappedBakedModel;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.blocks.camo.BlockCamo;
import betterwithmods.common.blocks.camo.CamoInfo;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;

public class CamoModel extends ModelFactory<CamoInfo> {

    public static CamoModel TABLE_SUPPORTED, TABLE_UNSUPPORTED;
    public static CamoModel BENCH_SUPPORTED, BENCH_UNSUPPORTED;
    public static CamoModel KILN;
    public static CamoModel BARK;

    public IModel template;

    public CamoModel(IModel template) {
        super(BlockCamo.CAMO_INFO, TextureMap.LOCATION_MISSING_TEXTURE);
        this.template = template;
    }

    @Override
    public IBakedModel bake(CamoInfo object, boolean isItem, BlockRenderLayer layer) {
        ImmutableMap.Builder<String, String> textures = new ImmutableMap.Builder<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            textures.put(facing.getName2(), RenderUtils.getTextureFromFace(object.getState(), facing).getIconName());
        }

        TRSRTransformation state = new TRSRTransformation(ModelRotation.X0_Y0);
        IModel retexture = template.retexture(textures.build()).uvlock(true);
        return new WrappedBakedModel(retexture.bake(state, DefaultVertexFormats.BLOCK, RenderUtils.textureGetter), RenderUtils.getParticleTexture(object.getState())).addDefaultBlockTransforms();
    }

    @Override
    public CamoInfo fromItemStack(ItemStack stack) {
        return new CamoInfo(stack);
    }
}