/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */
package headwayent.hotshotengine.android.animation;

/**
 * Abstract base class used convert type T to another type V and back again. This
 * is necessary when the value types of in animation are different from the property
 * type. BidirectionalTypeConverter is needed when only the final value for the
 * animation is supplied to animators.
 * @see PropertyValuesHolder#setConverter(TypeConverter)
 */
public abstract class BidirectionalTypeConverter<T, V> extends TypeConverter<T, V> {
    private BidirectionalTypeConverter mInvertedConverter;

    public BidirectionalTypeConverter(Class<T> fromClass, Class<V> toClass) {
        super(fromClass, toClass);
    }

    /**
     * Does a conversion from the target type back to the source type. The subclass
     * must implement this when a TypeConverter is used in animations and current
     * values will need to be read for an animation.
     * @param value The Object to convert.
     * @return A value of type T, converted from <code>value</code>.
     */
    public abstract T convertBack(V value);

    /**
     * Returns the inverse of this converter, where the from and to classes are reversed.
     * The inverted converter uses this convert to call {@link #convertBack(Object)} for
     * {@link #convert(Object)} calls and {@link #convert(Object)} for
     * {@link #convertBack(Object)} calls.
     * @return The inverse of this converter, where the from and to classes are reversed.
     */
    public BidirectionalTypeConverter invert() {
        if (mInvertedConverter == null) {
            mInvertedConverter = new InvertedConverter(this);
        }
        return mInvertedConverter;
    }

    private static class InvertedConverter<From, To> extends BidirectionalTypeConverter<From, To> {
        private final BidirectionalTypeConverter<To, From> mConverter;

        public InvertedConverter(BidirectionalTypeConverter<To, From> converter) {
            super(converter.getTargetType(), converter.getSourceType());
            mConverter = converter;
        }

        @Override
        public From convertBack(To value) {
            return mConverter.convert(value);
        }

        @Override
        public To convert(From value) {
            return mConverter.convertBack(value);
        }
    }
}
