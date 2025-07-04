/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.BitSet;

import static com.artemis.utils.ConverterUtil.toIntBag;

/**
 * Maintains the list of entities matched by an aspect. Entity subscriptions
 * are automatically updated during {@link com.artemis.World#process()}.
 * Any {@link com.artemis.EntitySubscription.SubscriptionListener | listeners}
 * are informed when entities are added or removed.
 */
public class EntitySubscription {
	final Aspect aspect;
	private final Aspect.Builder aspectReflection;
	private final BitSet aspectCache;

	private final IntBag entities;
	private final BitSet activeEntityIds;
	private final EntityManager em;

	private final Bag<SubscriptionListener> listeners;

	private final BitSet insertedIds;
	private final BitSet removedIds;

	private final IntBag inserted;
	private final IntBag removed;
	private boolean dirty;

	EntitySubscription(World world, Aspect.Builder builder) {
		aspect = builder.build(world);
		aspectReflection = builder;
		aspectCache = new BitSet();
		em = world.getEntityManager();

		activeEntityIds = new BitSet();
		entities = new IntBag();

		listeners = new Bag<>();

		insertedIds = new BitSet();
		removedIds = new BitSet();

		inserted = new IntBag();
		removed = new IntBag();
	}

	/**
	 * Returns a reference to the bag holding all matched
	 * entities.
	 *
	 * <p><b>Warning: </b> Never remove elements from the bag, as this
	 * will lead to undefined behavior.</p>
	 *
	 * @return View of all active entities.
	 */
	public IntBag getEntities() {
		if (dirty) {
			rebuildCompressedActives();
			dirty = false;
		}
		return entities;
	}

	/**
	 * Returns the bitset tracking all matched entities.
	 *
	 * <p><b>Warning: </b> Never toggle bits in the bitset, as
	 * this <i>may</i> lead to erroneously added or removed entities.</p>
	 *
	 * @return View of all active entities.
	 */
	public BitSet getActiveEntityIds() {
		return activeEntityIds;
	}

	/**
	 * @return aspect used for matching entities to subscription.
	 */
	public Aspect getAspect() {
		return aspect;
	}

	/**
	 * @return aspect builder used for matching entities to subscription.
	 */
	public Aspect.Builder getAspectBuilder() {
		return aspectReflection;
	}

	/**
	 * A new unique component composition detected, check if this
	 * subscription's aspect is interested in it.
	 */
	void processComponentIdentity(int id, BitSet componentBits) {
		aspectCache.set(id, aspect.isInterested(componentBits));
	}

	void rebuildCompressedActives() {
		BitSet bs = activeEntityIds;
		int size = bs.cardinality();
		entities.setSize(size);
		entities.ensureCapacity(size);
		int[] activesArray = entities.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}
	}

	final void check(int id) {
		boolean interested = aspectCache.get(em.getIdentity(id));
		boolean contains = activeEntityIds.get(id);

		if (interested && !contains) {
			insert(id);
		} else if (!interested && contains) {
			remove(id);
		}
	}

	private void remove(int entityId) {
		activeEntityIds.clear(entityId);
		removedIds.set(entityId);
	}

	private void insert(int entityId) {
		activeEntityIds.set(entityId);
		insertedIds.set(entityId);
	}

	void process(IntBag changed, IntBag deleted) {
		deleted(deleted);
		changed(changed);

		dirty |= informEntityChanges();
	}

	void processAll(IntBag changed, IntBag deleted) {
		deletedAll(deleted);
		changed(changed);

		dirty |= informEntityChanges();
	}

	boolean informEntityChanges() {
		if (insertedIds.isEmpty() && removedIds.isEmpty())
			return false;

		transferBitsToInts();
		for (int i = 0, s = listeners.size(); s > i; i++) {
			if (!removed.isEmpty())
				listeners.get(i).removed(removed);

			if (!inserted.isEmpty())
				listeners.get(i).inserted(inserted);
		}

		inserted.setSize(0);
		removed.setSize(0);

		return true;
	}

	private void transferBitsToInts() {
		toIntBag(insertedIds, inserted);
		toIntBag(removedIds, removed);
		insertedIds.clear();
		removedIds.clear();
	}

	private void changed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check(ids[i]);
		}
	}

	private void deleted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(ids[i]);
		}
	}

	private void deletedAll(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			int id = ids[i];
			activeEntityIds.clear(id);
			removedIds.set(id);
		}
	}

	private void deleted(int entityId) {
		if(activeEntityIds.get(entityId))
			remove(entityId);
	}

	/**
	 * Add listener interested in changes to the subscription.
	 *
	 * @param listener listener to add.
	 */
	public void addSubscriptionListener(SubscriptionListener listener) {
		listeners.add(listener);
	}

	/**
	 * <p>This interfaces reports entities inserted or
	 * removed when matched against their {@link com.artemis.EntitySubscription}</p>
	 */
	public interface SubscriptionListener {
		/**
		 * Called after entities have been matched and inserted into an
		 * EntitySubscription.
		 */
		void inserted(IntBag entities);

		/**
		 * Called after entities have been removed from an EntitySubscription.
		 */
		void removed(IntBag entities);
	}

}
