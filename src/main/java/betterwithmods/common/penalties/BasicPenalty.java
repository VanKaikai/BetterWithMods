package betterwithmods.common.penalties;

import betterwithmods.common.penalties.attribute.BWMAttributes;
import betterwithmods.module.ConfigHelper;
import org.apache.commons.lang3.Range;

/**
 * Penalty linked to hunger (food level).
 *
 * @author Koward
 */
public class BasicPenalty<T extends Number & Comparable> extends Penalty<T> {


    public BasicPenalty(boolean jump, boolean swim, boolean heal, boolean sprint, boolean attack, boolean pain, float speed, float severity, String name, String lang, String category, Range<T> range) {
        //TODO this needs a better desc
        super(lang, severity, BWMAttributes.getRange(category, name, "Numberic range for whether this penalty it active", range),
                BWMAttributes.JUMP.fromConfig(category, name, jump),
                BWMAttributes.SWIM.fromConfig(category, name, swim),
                BWMAttributes.HEAL.fromConfig(category, name, heal),
                BWMAttributes.SPRINT.fromConfig(category, name, sprint),
                BWMAttributes.ATTACK.fromConfig(category, name, attack),
                BWMAttributes.PAIN.fromConfig(category, name, pain),
                BWMAttributes.SPEED.fromConfig(category, name, speed)
        );
        ConfigHelper.setDescription(category + "." + name, "Configure values for the " + name + " penalty");
    }


}

