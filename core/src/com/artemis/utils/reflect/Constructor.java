/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils.reflect;

import java.lang.reflect.InvocationTargetException;

/** Provides information about, and access to, a single constructor for a Class.
 * @author nexsoftware */
@SuppressWarnings("rawtypes")
public final class Constructor {

	private final java.lang.reflect.Constructor constructor;

	Constructor (java.lang.reflect.Constructor constructor) {
		this.constructor = constructor;
	}

	/** Returns an array of Class objects that represent the formal parameter types, in declaration order, of the constructor. */
	public Class[] getParameterTypes () {
		return constructor.getParameterTypes();
	}

	/** Returns the Class object representing the class or interface that declares the constructor. */
	public Class getDeclaringClass () {
		return constructor.getDeclaringClass();
	}

	public boolean isAccessible () {
		return constructor.isAccessible();
	}

	public void setAccessible (boolean accessible) {
		constructor.setAccessible(accessible);
	}

	/** Uses the constructor to create and initialize a new instance of the constructor's declaring class, with the supplied initialization parameters. */
	public Object newInstance (Object... args) throws ReflectionException {
		try {
			return constructor.newInstance(args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(), e);
		} catch (InstantiationException e) {
			throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException("Exception occurred in constructor for class: " + getDeclaringClass().getName(), e);
		}
	}

}