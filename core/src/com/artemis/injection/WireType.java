/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.injection;

/**
 * <p>Enum used by {@link ClassType} to indicate if a class or field is annotated
 * with {@link com.artemis.annotations.Wire}.</p>
 *
 * @author Snorre E. Brekke
 */
public enum WireType {
	/**
	 * Indicates that a class is (implicitly or explicitly) annotated
	 * with {@link com.artemis.annotations.Wire}.
	 */
	WIRE,
	/**
	 * Indicates that a class is not annotated with anything relevant to the artemis-world.
	 */
	IGNORED,
	/**
	 * Indicates that a class is annotated with {@link com.artemis.annotations.SkipWire}
	 */
	SKIPWIRE
}
