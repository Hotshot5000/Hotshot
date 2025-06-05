/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.injection;

import com.artemis.World;
import com.artemis.utils.reflect.Field;

/**
 * API used by {@link FieldHandler} to resolve field values in classes eligible for injection.
 *
 * @author Snorre E. Brekke
 */
public interface FieldResolver {

	/**
	 * Called after Wo
	 *
	 * @param world
	 */
	void initialize(World world);

	Object resolve(Class<?> fieldType, Field field);
}
