/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.injection;

import java.util.Map;

/**
 * Field resolver for manually registered objects, for injection by type or name.
 *
 * @see com.artemis.WorldConfiguration#register
 * @author Daan van Yperen
 */
public interface PojoFieldResolver extends FieldResolver {

	/**
	 * Set manaully registered objects.
	 * @param pojos Map of manually registered objects.
	 */
	void setPojos(Map<String, Object> pojos);
}
