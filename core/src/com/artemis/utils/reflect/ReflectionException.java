/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils.reflect;

/** Thrown when an exception occurs during reflection.
 * @author nexsoftware
 */
public class ReflectionException extends Exception {

	private static final long serialVersionUID = -7146287043138864498L;

	public ReflectionException () {
		super();
	}

	public ReflectionException (String message) {
		super(message);
	}

	public ReflectionException (Throwable cause) {
		super(cause);
	}

	public ReflectionException (String message, Throwable cause) {
		super(message, cause);
	}

}