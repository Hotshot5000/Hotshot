/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

/**
 * Injection failed.
 *
 * @author Daan van Yperen
 */
public class InjectionException extends RuntimeException {
	public InjectionException(String msg) {
		super(msg);
	}

	public InjectionException(String msg, Throwable e) {
		super(msg,e);
	}
}
