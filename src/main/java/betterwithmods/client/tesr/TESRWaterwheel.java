package betterwithmods.client.tesr;

import betterwithmods.client.model.generators.ModelWaterwheel;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.tile.TileWaterwheel;
import betterwithmods.lib.ModLib;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TESRWaterwheel extends TileEntitySpecialRenderer<TileWaterwheel> {
    public static final ResourceLocation WATERWHEEL = new ResourceLocation(ModLib.MODID, "textures/blocks/waterwheel.png");
    private static ModelWaterwheel waterwheel = new ModelWaterwheel();

    public TESRWaterwheel() {

    }

    public static void renderWaterwheel(float direction, float rotation, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        waterwheel.setAngle(0, 0, Math.toRadians(rotation));
        GlStateManager.rotate(direction, 0.0F, 1.0F, 0.0F);
        waterwheel.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileWaterwheel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockPos pos = te.getBlockPos();
        RenderUtils.renderDebugBoundingBox(x, y, z, te.getRenderBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()));
        EnumFacing dir = te.getOrientation();
        float rotation = (te.getCurrentRotation() + (te.getMaximumInput(dir) == 0 ? 0 : partialTicks * te.getPrevRotation()));
        float direction = dir == EnumFacing.SOUTH ? 180 : 90;
        renderWaterwheel(direction, rotation, x, y, z);
    }


}
