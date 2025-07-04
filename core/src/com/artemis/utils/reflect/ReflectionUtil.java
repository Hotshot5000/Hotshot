/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils.reflect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.Manager;

public final class ReflectionUtil {
	private static final Class<?>[] PARAM_ENTITY = {Entity.class};

	private ReflectionUtil() {}

	public static boolean implementsObserver(BaseSystem owner, String methodName) {
		try {
			Method method = ClassReflection.getMethod(owner.getClass(), methodName, PARAM_ENTITY);
			Class declarer = method.getDeclaringClass();
			return !(Manager.class.equals(declarer) || EntitySystem.class.equals(declarer));
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
