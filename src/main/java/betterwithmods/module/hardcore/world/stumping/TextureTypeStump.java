package betterwithmods.module.hardcore.world.stumping;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.api.texture.ITextureType;
import team.chisel.ctm.api.texture.TextureType;
import team.chisel.ctm.api.util.TextureInfo;

@TextureType(value = "bwm_stump")
public class TextureTypeStump implements ITextureType {

    @Override
    public TextureStump makeTexture(TextureInfo info) {
        return new TextureStump(this, info);
    }

    @Override
    public ITextureContext getBlockRenderContext(IBlockState state, IBlockAccess world, BlockPos pos, ICTMTexture<?> tex) {
        WorldClient worldClient = Minecraft.getMinecraft().world;
        return new TextureContextStump(HCStumping.isStump(worldClient, pos));
    }

    @Override
    public ITextureContext getContextFromData(long data) { return new TextureContextStump(data == 1); }

    @Override
    public int requiredTextures() {
        return 2;
    }
}
