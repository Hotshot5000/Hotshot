/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static com.artemis.utils.ConverterUtil.toIntBag;

/**
 * Manages all instances of {@link EntitySubscription}.
 * <p>
 * Entity subscriptions are automatically updated during {@link com.artemis.World#process()}.
 * Any {@link com.artemis.EntitySubscription.SubscriptionListener | listeners}
 * are informed when entities are added or removed.
 *
 * @see EntitySubscription
 */
@SkipWire
public class AspectSubscriptionManager extends Manager {

	private final Map<Aspect.Builder, EntitySubscription> subscriptionMap;
	private final Bag<EntitySubscription> subscriptions;

	private final IntBag changedIds = new IntBag();
	private final IntBag deletedIds = new IntBag();

	protected AspectSubscriptionManager() {
		subscriptionMap = new HashMap<>();
		subscriptions = new Bag<>();
	}

	/**
	 * Get subscription to all entities matching {@link Aspect}.
	 * <p>
	 * Will create a new subscription if not yet available for
	 * given {@link Aspect} match.
	 *
	 * @param builder Aspect to match.
	 * @return {@link EntitySubscription} for aspect.
	 */
	public EntitySubscription get(Aspect.Builder builder) {
		EntitySubscription subscription = subscriptionMap.get(builder);
		return (subscription != null) ? subscription : createSubscription(builder);
	}

	private EntitySubscription createSubscription(Aspect.Builder builder) {
		EntitySubscription entitySubscription = new EntitySubscription(world, builder);
		subscriptionMap.put(builder, entitySubscription);
		subscriptions.add(entitySubscription);

		world.getEntityManager().synchronize(entitySubscription);

		return entitySubscription;
	}

	/**
	 * Informs all listeners of added, changed and deleted changes.
	 * <p>
	 * Two types of listeners:
	 * {@see EntityObserver} implementations are guaranteed to be called back in order of system registration.
	 * {@see com.artemis.EntitySubscription.SubscriptionListener}, where order can vary (typically ordinal, except
	 * for subscrip1tions created in process, initialize instead of setWorld).
     * <p>
	 * {@link com.artemis.EntitySubscription.SubscriptionListener#inserted(IntBag)}
	 * {@link com.artemis.EntitySubscription.SubscriptionListener#removed(IntBag)}
	 *
	 * Observers are called before Subscriptions, which means managerial tasks get artificial priority.
	 *
	 * @param changed Entities with changed composition or state.
	 * @param deleted Entities removed from world.
	 */
	void process(BitSet changed, BitSet deleted) {
		toEntityIntBags(changed, deleted);

		// note: processAll != process
		subscriptions.get(0).processAll(changedIds, deletedIds);

		Object[] subscribers = subscriptions.getData();
		for (int i = 1, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.process(changedIds, deletedIds);
		}
	}

	private void toEntityIntBags(BitSet changed, BitSet deleted) {
		toIntBag(changed, changedIds);
		toIntBag(deleted, deletedIds);

		changed.clear();
		deleted.clear();
	}

	void processComponentIdentity(int id, BitSet componentBits) {
		Object[] subscribers = subscriptions.getData();
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.processComponentIdentity(id, componentBits);
		}
	}
}
