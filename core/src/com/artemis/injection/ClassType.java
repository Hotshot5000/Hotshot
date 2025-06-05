/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.injection;

/**
 * Enum used to cache class type according to their usage in Artemis.
 *
 * @author Snorre E. Brekke
 */
public enum ClassType {
	/**
	 * Used for (sub)classes of {@link com.artemis.ComponentMapper}
	 */
	MAPPER,
	/**
	 * Used for (sub)classes of {@link com.artemis.BaseSystem}
	 */
	SYSTEM,
	/**
	 * Used for (sub)classes of {@link com.artemis.EntityFactory}
	 */
	FACTORY,
	/**
	 * Used for everything else.
	 */
	CUSTOM
}
