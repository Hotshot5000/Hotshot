/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.reflect.ClassReflection;


@SuppressWarnings("serial")
public class MundaneWireException extends RuntimeException {

	public MundaneWireException(Class<? extends BaseSystem> klazz) {
		super("Not added to world: " + ClassReflection.getSimpleName(klazz));
	}

	public MundaneWireException(String message, Throwable cause) {
		super(message, cause);
	}

	public MundaneWireException(String message) {
		super(message);
	}
}
