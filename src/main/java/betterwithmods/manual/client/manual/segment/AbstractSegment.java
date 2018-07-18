package betterwithmods.manual.client.manual.segment;

import betterwithmods.manual.common.api.ManualDefinitionImpl;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

abstract class AbstractSegment implements Segment {
    protected final ManualDefinitionImpl myManual;
    private Segment next;

    public AbstractSegment(ManualDefinitionImpl manual) {
        myManual = manual;
    }

    @Override
    public Segment root() {
        final Segment parent = parent();
        return parent == null ? this : parent.root();
    }

    @Override
    public Optional<InteractiveSegment> render(final int x, final int y, final int indent, final int maxWidth, final FontRenderer renderer, final int mouseX, final int mouseY) {
        return Optional.empty();
    }

    @Override
    public Iterable<Segment> refine(final Pattern pattern, final SegmentRefiner refiner) {
        return Collections.singletonList(this);
    }

    @Override
    public Segment next() {
        return next;
    }

    @Override
    public void setNext(@Nullable final Segment segment) {
        next = segment;
    }

    @Override
    public ManualDefinitionImpl getManual() {
        return myManual;
    }
}
