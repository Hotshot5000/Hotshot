/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

class ComponentPool {
	
	private final Bag<Pool> pools;

	ComponentPool() {
		pools = new Bag<>();
	}
	
	<T extends PooledComponent> T obtain(Class<T> componentClass, ComponentType type)
		throws ReflectionException {
		
		Pool pool = getPool(type.getIndex());
		return (pool.size() > 0) ? pool.obtain() :  ClassReflection.newInstance(componentClass);
	}
	
	void free(PooledComponent c, ComponentType type) {
		free(c, type.getIndex());
	}

	void free(PooledComponent c, int typeIndex) {
		c.reset();
		getPool(typeIndex).free(c);
	}

	private <T extends PooledComponent>Pool getPool(int typeIndex) {
		Pool pool = pools.safeGet(typeIndex);
		if (pool == null) {
			pool = new Pool();
			pools.set(typeIndex, pool);
		}
		return pool;
	}
	
	private static class Pool {
		private final Bag<PooledComponent> cache = new Bag<>();
		
		@SuppressWarnings("unchecked")
		<T extends PooledComponent> T obtain() {
			return (T)cache.removeLast();
		}
		
		int size() {
			return cache.size();
		}
		
		void free(PooledComponent component) {
			cache.add(component);
		}
	}
}
