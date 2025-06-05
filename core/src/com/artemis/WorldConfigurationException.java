/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import java.lang.RuntimeException;import java.lang.String;import java.lang.Throwable; /**
 * World configuration failed.
 *
 * @author Daan van Yperen
 */
public class WorldConfigurationException extends RuntimeException {
	public WorldConfigurationException(String msg) {
		super(msg);
	}

	public WorldConfigurationException(String msg, Throwable e) {
		super(msg,e);
	}
}
