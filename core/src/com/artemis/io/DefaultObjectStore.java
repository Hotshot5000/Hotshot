/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.io;

import com.artemis.utils.reflect.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is only public in order to keep GWT happy.
 * Nothing to see here (API can change without prior notice)
 */
public class DefaultObjectStore {

	private final HashMap<Class, TypeData> defaultValues = new HashMap<>();
	private boolean usePrototypes = true;

	public boolean hasDefaultValues(Object object) {
		if (!usePrototypes)
			return false;

		TypeData typeData = defaultValues.get(object.getClass());
		if (typeData == null) {
			typeData = new TypeData(newInstance(object.getClass()));
			defaultValues.put(object.getClass(), typeData);
		}

		return typeData.hasDefaultValues(object);
	}

	private Object newInstance(Class<?> type) {
		try {
			return ClassReflection.newInstance(type);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUsePrototypes(boolean usePrototypes) {
		this.usePrototypes = usePrototypes;
	}

	static class TypeData {
		private final Object object;
		private final Class type;
		private final Field[] fields;

		TypeData(Object object) {
			this.object = object;
			this.type = object.getClass();

			Field[] fields = ClassReflection.getDeclaredFields(type);
			List<Field> filtered = new ArrayList<>();
            for (Field f : fields) {
                if (!f.isStatic()) {
                    f.setAccessible(true);
                    filtered.add(f);
                }
            }

			this.fields = filtered.toArray(new Field[0]);
		}

		boolean hasDefaultValues(Object other) {
			assert (other.getClass() == type);

			try {
                for (Field f : fields) {
                    if (!equals(f.get(object), f.get(other)))
                        return false;
                }

				return true;
			} catch (ReflectionException e) {
				throw new RuntimeException(e);
			}
		}

		private static boolean equals(Object a, Object b) {
			return (a != null) ? a.equals(b) : b == null;
		}
	}
}
