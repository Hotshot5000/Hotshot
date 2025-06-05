/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.io;

import com.artemis.Entity;

import java.util.*;

/**
 * Maintains serialization-local key-to-entity mappings.
 */
class SerializationKeyTracker {
	private final Map<String, Entity> keyToEntity = new HashMap<>();

	void register(String key, Entity e) {
		keyToEntity.put(key, e);
	}

	Entity get(String key) {
		return keyToEntity.get(key);
	}

	Set<String> keys() {
		return keyToEntity.keySet();
	}
}
