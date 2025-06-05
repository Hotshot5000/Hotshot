/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.Bag;

/**
 * Simple sequential invocation strategy.
 * @see SystemInvocationStrategy
 */
public class InvocationStrategy extends SystemInvocationStrategy {

	/** Processes all systems in order. */
	@Override
	protected void process(Bag<BaseSystem> systems) {
		Object[] systemsData = systems.getData();
		for (int i = 0, s = systems.size(); s > i; i++) {
			BaseSystem system = (BaseSystem) systemsData[i];
			if (!system.isPassive()) {
				system.process();
				updateEntityStates();
			}
		}
	}
}
