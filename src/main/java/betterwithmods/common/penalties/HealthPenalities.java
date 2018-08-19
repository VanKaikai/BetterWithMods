package betterwithmods.common.penalties;

import betterwithmods.module.Feature;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class HealthPenalities extends PenaltyHandler<Float, BasicPenalty<Float>> {

    public HealthPenalities(Feature feature) {
        addDefault(new BasicPenalty<>(true, true, true, true, true, false, 1f, 0f, "none", "health_penalty.none", feature, Range.between(20f, 11f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, true, false, 0.75f, 1 / 5f, "hurt", "health_penalty.hurt", feature, Range.between(11f, 9f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, false, true, 0.75f, 2 / 5f, "injured", "health_penalty.injured", feature, Range.between(9f, 7f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, false, true, 0.5f, 3 / 5f, "wounded", "health_penalty.wounded", feature, Range.between(7f, 5f)));
        addPenalty(new BasicPenalty<>(false, false, true, false, false, true, 0.25f, 4 / 5f, "crippled", "health_penalty.crippled", feature, Range.between(5f, 3f)));
        addPenalty(new BasicPenalty<>(false, false, true, false, false, true, 0.25f, 1, "dying", "health_penalty.dying", feature, Range.between(3f, -1f)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BasicPenalty getPenalty(EntityPlayer player) {
        float level = player.getHealth();
        return getPenalty(level);
    }

}
