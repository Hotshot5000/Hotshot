/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.IntBag;

/**
 * Tracks a subset of entities, but does not implement any sorting or iteration.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class BaseEntitySystem extends BaseSystem
		implements EntitySubscription.SubscriptionListener {

	private final Aspect.Builder aspectConfiguration;
	protected EntitySubscription subscription;

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public BaseEntitySystem(Aspect.Builder aspect) {
		super();
		if (aspect == null) {
			String error = "Aspect.Builder was null; to use systems which " +
					"do not subscribe to entities, extend BaseSystem directly.";
			throw new NullPointerException(error);
		}

		aspectConfiguration = aspect;
	}

	protected void setWorld(World world) {
		super.setWorld(world);

		subscription = getSubscription();
		subscription.addSubscriptionListener(this);
	}

	/**
	 * @return entity subscription backing this system.
	 */
	public EntitySubscription getSubscription() {
		final AspectSubscriptionManager sm = world.getAspectSubscriptionManager();
		return sm.get(aspectConfiguration);
	}

	@Override
	public void inserted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			inserted(ids[i]);
		}
	}

	/**
	 * Gets the entities processed by this system. Do not delete entities from
	 * this bag - it is the live thing.
	 *
	 * @return System's entity ids, as matched by aspect.
	 */
	public IntBag getEntityIds() {
		return subscription.getEntities();
	}

	/**
	 * Called if entity has come into scope for this system, e.g
	 * created or a component was added to it.
	 *
	 * @param entityId
	 *			the entity that was added to this system
	 */
	protected void inserted(int entityId) {}

	@Override
	public void removed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			removed(ids[i]);
		}
	}

	/**
	 * Called if entity has gone out of scope of this system, e.g deleted
	 * or had one of it's components removed.
	 *
	 * @param entityId
	 *			the entity that was removed from this system
	 */
	protected void removed(int entityId) {}
}
