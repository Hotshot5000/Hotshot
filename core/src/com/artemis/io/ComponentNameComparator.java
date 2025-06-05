/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.io;

import com.artemis.Component;

import java.io.Serializable;
import java.util.Comparator;

public class ComponentNameComparator implements Comparator<Component>, Serializable {
	@Override
	public int compare(Component o1, Component o2) {
		String name1 = o1.getClass().getSimpleName();
		String name2 = o2.getClass().getSimpleName();

		return name1.compareTo(name2);
	}
}
