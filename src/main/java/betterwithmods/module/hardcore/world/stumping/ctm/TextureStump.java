package betterwithmods.module.hardcore.world.stumping.ctm;


import betterwithmods.module.hardcore.world.stumping.HCStumping;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.render.AbstractTexture;
import team.chisel.ctm.client.util.Quad;

import javax.annotation.Nullable;
import java.util.List;

public class TextureStump extends AbstractTexture<TextureTypeStump> {

    public TextureStump(TextureTypeStump type, TextureInfo info) {
        super(type, info);
    }

    @Override
    public List<BakedQuad> transformQuad(BakedQuad quad, @Nullable ITextureContext context, int quadGoal) {
        if (HCStumping.CTM && context instanceof TextureContextStump) {
            TextureContextStump c = (TextureContextStump) context;
            Quad q = makeQuad(quad, context);
            return Lists.newArrayList(q.transformUVs(sprites[(int) c.getCompressedData()]).rebake());
        }
        return Lists.newArrayList(quad);
    }
}