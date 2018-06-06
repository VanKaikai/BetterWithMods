package betterwithmods.common.penalties;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class GloomPenalties extends PenaltyHandler<Integer, GloomPenalty> {

    private static final String category = "hardcore.hcgloom";

    public GloomPenalties() {
        super();
        addDefault(new GloomPenalty(false, true, 0, "none", "bwm.gloom_penalty.none", category, 0, Range.is(0)));
        addPenalty(new GloomPenalty(false, true, 0.25f, "gloom", "bwm.gloom_penalty.gloom", category, 1, Range.between(1, 1200)));
        addPenalty(new GloomPenalty(false, true, 0.50f, "dread", "bwm.gloom_penalty.dread", category, 2, Range.between(1201, 2400)));
        addPenalty(new GloomPenalty(false, false, 0.75f, "terror", "bwm.gloom_penalty.terror", category, 2, Range.between(2401, 100000)));
    }

    @Override
    public GloomPenalty getPenalty(EntityPlayer player) {

        return null;
    }

    public int getMaxTime() {
        return penalties.stream().mapToInt(p -> p.getRange().getMaximum()).max().orElse(0);
    }
}
