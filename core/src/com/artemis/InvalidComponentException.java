/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.reflect.ClassReflection;

@SuppressWarnings("serial")
public class InvalidComponentException extends RuntimeException {

	private final Class<?> componentClass;

	public InvalidComponentException(Class<?> componentClass, String string) {
		super(message(componentClass, string));
		this.componentClass = componentClass;
	}

	public InvalidComponentException(Class<?> componentClass, String string, Exception e) {
		super(message(componentClass, string), e);
		this.componentClass = componentClass;
	}
	
	private static String message(Class<?> componentClass, String string) {
		return ClassReflection.getSimpleName(componentClass) + ": " + string;
	}

	public Class<?> getComponentClass() {
		return componentClass;
	}
}
