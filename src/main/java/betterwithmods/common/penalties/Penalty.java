package betterwithmods.common.penalties;

import betterwithmods.common.penalties.attribute.Attribute;
import betterwithmods.common.penalties.attribute.IAttribute;
import betterwithmods.common.penalties.attribute.IAttributeInstance;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;

public class Penalty<T extends Number & Comparable> implements Comparable<Penalty> {
    private final Range<T> range;

    private final float severity;
    private final Map<IAttribute<?>, IAttributeInstance<?>> attributeMap = Maps.newHashMap();
    private final String name;

    public Penalty(String name, float severity, Range<T> range, IAttributeInstance<?>... attributes) {
        this.name = name;
        this.severity = severity;
        this.range = range;
        Arrays.stream(attributes).forEach(a -> attributeMap.put(a.getParent(), a));
    }

    public Range<T> getRange() {
        return range;
    }

    public boolean inRange(T t) {
        return range.contains(t);
    }

    public float getSeverity() {
        return severity;
    }

    public IAttributeInstance<?> getAttribute(Attribute<?> attribute) {
        return attributeMap.get(attribute);
    }

    @SuppressWarnings("unchecked")
    public IAttributeInstance<Boolean> getBoolean(Attribute<Boolean> attribute) {
        return (IAttributeInstance<Boolean>) getAttribute(attribute);
    }

    @SuppressWarnings("unchecked")
    public IAttributeInstance<Float> getFloat(Attribute<Float> attribute) {
        return (IAttributeInstance<Float>) getAttribute(attribute);
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@Nonnull Penalty penalty) {
        return Float.compare(this.getSeverity(), penalty.getSeverity());
    }
}
