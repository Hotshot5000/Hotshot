/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.annotations.UnstableApi;

import java.nio.ByteBuffer;

/**
 * Packs components into a memory-friendly storage, such as a primitive array or {@link ByteBuffer},
 * reuses the same instance for all entities.
 * <p>
 * Constructor must either be zero-length or take a {@link World} instance.
 * Calling the constructor must not change the underlying component data - ie,
 * it's the equivalent of a {@link #clone()}.
 * <p>
 *
 * <b>UnstableApi:</b> Pending optimization work might result in changes to this interface.
 */
@UnstableApi
@Deprecated
public abstract class PackedComponent extends Component {

	/**
	 * Sets the currently processed entity. Automatically
	 * called by {@link PackedComponentMapper}.
	 *
	 * @param entityId id of entity to process.
	 */
	protected abstract void forEntity(int entityId);

	/**
	 * Internal method, used by the {@link ComponentManager},
	 * will always send the highest seen {@link Entity#getId()}.
	 * <p/>
	 * Ensures that the backing data storage can accomodate
	 * all entities.
	 *
	 * @param id Highest seen entity id.
	 */
	protected abstract void ensureCapacity(int id);

	/**
	 * Resets the component upon deletion.
	 */
	protected abstract void reset();

	/**
	 * Marks packed component for freeing of resources upon {@link World#dispose()}. Called once.
	 */
	public interface DisposedWithWorld {
		void free(World world);
	}
}
