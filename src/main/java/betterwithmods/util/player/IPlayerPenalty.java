package betterwithmods.util.player;

/**
 * Penalty the player has.
 *
 * @author Koward
 */
public interface IPlayerPenalty {

    float getModifier();

    String getDescription();

    boolean canJump();

    boolean canSprint();
}
