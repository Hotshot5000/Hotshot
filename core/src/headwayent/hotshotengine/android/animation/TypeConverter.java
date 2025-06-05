/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.animation;

/**
 * Abstract base class used convert type T to another type V. This
 * is necessary when the value types of in animation are different
 * from the property type.
 * @see PropertyValuesHolder#setConverter(TypeConverter)
 */
public abstract class TypeConverter<T, V> {
    private final Class<T> mFromClass;
    private final Class<V> mToClass;

    public TypeConverter(Class<T> fromClass, Class<V> toClass) {
        mFromClass = fromClass;
        mToClass = toClass;
    }

    /**
     * Returns the target converted type. Used by the animation system to determine
     * the proper setter function to call.
     * @return The Class to convert the input to.
     */
    Class<V> getTargetType() {
        return mToClass;
    }

    /**
     * Returns the source conversion type.
     */
    Class<T> getSourceType() {
        return mFromClass;
    }

    /**
     * Converts a value from one type to another.
     * @param value The Object to convert.
     * @return A value of type V, converted from <code>value</code>.
     */
    public abstract V convert(T value);
}
