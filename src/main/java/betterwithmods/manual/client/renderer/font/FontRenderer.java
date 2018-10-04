package betterwithmods.manual.client.renderer.font;

/**
 * Base interface for font renderers.
 */
public interface FontRenderer {
    /**
     * Render the specified string.
     *
     * @param value the string to baking.
     */
    void drawString(final CharSequence value);

    /**
     * Render up to the specified amount of characters of the specified string.
     * <p>
     * This is intended as a convenience method for clamped-width rendering,
     * avoiding additional string operations such as <tt>substring</tt>.
     *
     * @param value    the string to baking.
     * @param maxChars the maximum number of characters to baking.
     */
    void drawString(final CharSequence value, final int maxChars);

    /**
     * Get the width of the characters drawn with the font renderer, in pixels.
     *
     * @return the width of the drawn characters.
     */
    int getCharWidth();

    /**
     * Get the height of the characters drawn with the font renderer, in pixels.
     *
     * @return the height of the drawn characters.
     */
    int getCharHeight();
}
