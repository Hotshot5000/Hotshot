/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ArtemisMultiException extends RuntimeException {
	private final List<Throwable> exceptions = new ArrayList<>();
	
	public ArtemisMultiException(List<Throwable> exceptions) {
		super();
		this.exceptions.addAll(exceptions);
	}

	public List<Throwable> getExceptions() {
		return exceptions;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Throwable t : exceptions) {
			if (sb.length() > 0) sb.append("\n");
			sb.append(t.getMessage());
		}
		return sb.toString();
	}
}
