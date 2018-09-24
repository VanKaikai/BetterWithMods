package betterwithmods.common.penalties;

import betterwithmods.library.modularity.impl.Feature;
import betterwithmods.module.hardcore.needs.HCGloom;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class GloomPenalties extends PenaltyHandler<Integer, GloomPenalty> {

    public GloomPenalties(Feature feature) {
        super();
        addDefault(new GloomPenalty(false, true, 0, "none", "gloom_penalty.none", feature, 0, Range.is(0)));
        addPenalty(new GloomPenalty(false, true, 0.01f, "gloom", "gloom_penalty.gloom", feature, 1, Range.between(1, 1200)));
        addPenalty(new GloomPenalty(false, true, 0.05f, "dread", "gloom_penalty.dread", feature, 2, Range.between(1201, 2400)));
        addPenalty(new GloomPenalty(true, false, 0.10f, "terror", "gloom_penalty.terror", feature, 2, Range.between(2401, 100000)));
    }

    @Override
    public GloomPenalty getPenalty(EntityPlayer player) {
        return getPenalty(HCGloom.getGloomTime(player));
    }

    public int getMaxTime() {
        return penalties.stream().mapToInt(p -> p.getRange().getMaximum()).max().orElse(0);
    }
}
