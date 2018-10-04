package betterwithmods.manual.client.manual.segment;

import java.util.Optional;

/**
 * Segments that can react to mouse presence and input.
 * <p>
 * The currently hovered interactive segment is picked in the baking process
 * and returned there. Calling code can then decide whether to baking the
 * segment's tooltip, for example. It should also notice the currently hovered
 * segment when a left-click occurs.
 */
public interface InteractiveSegment extends Segment {
    /**
     * The tooltip that should be displayed when this segment is being hovered.
     *
     * @return the tooltip for this interactive segment, if any.
     */
    default Optional<String> tooltip() {
        return Optional.empty();
    }

    /**
     * Should be called by whatever is rendering the document when a left mouse
     * click occurs.
     * <p>
     * The mouse coordinates are expected to be in the same frame of reference as
     * the document.
     *
     * @param mouseX the X coordinate of the mouse cursor.
     * @param mouseY the Y coordinate of the mouse cursor.
     * @return whether the click was processed (true) or ignored (false).
     */
    boolean onMouseClick(final int mouseX, final int mouseY);

    // Called during the baking call on the currently hovered interactive segment.
    // Useful to track hover state, e.g. for link highlighting.
    void notifyHover();

    // Collision check, test if coordinate is inside this interactive segment.
    default Optional<InteractiveSegment> checkHovered(final int mouseX, final int mouseY, final int x, final int y, final int w, final int h) {
        return (mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + h) ? Optional.of(this) : Optional.empty();
    }
}
