package betterwithmods.util.player;

/**
 * Created by primetoxinz on 5/13/17.
 */
//public enum GloomPenalty implements IPlayerPenalty {
//    NO_PENALTY(1.0f, 0, 0, "bwm.gloom_penalty.none", true),
//    GLOOM(1.5f, 1, 1200, "bwm.gloom_penalty.gloom", true),
//    DREAD(2f, 1200, 2400, "bwm.gloom_penalty.dread", true),
//    TERROR(3f, 2400, 10000000, "bwm.gloom_penalty.terror", false);
//
//    private final float modifier;
//    private final int timeLower, timeUpper;
//    private final String description;
//    public static final GloomPenalty[] VALUES = values();
//    private  final boolean canJump;
//    GloomPenalty(float modifier, int timeLower, int timeUpper, String description, boolean canJump) {
//        this.modifier = modifier;
//        this.timeLower = timeLower;
//        this.timeUpper = timeUpper;
//        this.description = description;
//        this.canJump = canJump;
//    }
//
//    public boolean isInRange(int time) {
//        return time > timeLower && time <= timeUpper;
//    }
//
//    @Override
//    public float getModifier() {
//        return modifier;
//    }
//
//    public int getTimeLower() {
//        return timeLower;
//    }
//
//    public int getTimeUpper() {
//        return timeUpper;
//    }
//
//    @Override
//    public String getName() {
//        return description;
//    }
//
//    @Override
//    public boolean canJump() {
//        return canJump;
//    }
//}
