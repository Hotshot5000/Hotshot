/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.systems;

import com.artemis.*;
import com.artemis.utils.IntBag;

/**
 * Iterates over {@link EntitySubscription} member entities by
 * entity identity.
 * <p>
 * Use this when you need to process entities matching an {@link Aspect},
 * and you want maximum performance.
 *
 * @author Arni Arent
 * @author Adrian Papari
 * @see EntityProcessingSystem Entity iteration by entity reference.
 */
public abstract class IteratingSystem extends BaseEntitySystem {

	/**
	 * Creates a new EntityProcessingSystem.
	 *
	 * @param aspect
	 *			the aspect to match entities
	 */
	public IteratingSystem(Aspect.Builder aspect) {
		super(aspect);
	}

	/**
	 * Process a entity this system is interested in.
	 *
	 * @param entityId
	 *			the entity to process
	 */
	protected abstract void process(int entityId);

	/** @inheritDoc */
	@Override
	protected final void processSystem() {
		IntBag actives = subscription.getEntities();
		int[] ids = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			process(ids[i]);
		}
	}
}
