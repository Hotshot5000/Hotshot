/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.Transient;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is only public in order to keep GWT happy.
 * Nothing to see here (API can change without prior notice)
 */
public class ArchetypeMapper {
	public final Map<Integer, TransmuterEntry> compositionIdMapper = new HashMap<>();
	public SaveFileFormat serializationState;

	public ArchetypeMapper(World world, IntBag toSave) {
		int[] ids = toSave.getData();
		Bag<Component> components = new Bag<>();
		Bag<Class<? extends Component>> types = new Bag<>();

		for (int i = 0, s = toSave.size(); s > i; i++) {
			int compositionId = world.getEntity(ids[i]).getCompositionId();
			if (!compositionIdMapper.containsKey(compositionId)) {
				components.clear();
				types.clear();

				world.getComponentManager().getComponentsFor(ids[i], components);
				compositionIdMapper.put(compositionId,
					new TransmuterEntry(toClasses(components, types)));
			}
		}
	}

	public ArchetypeMapper() {} // for serialization

	private static Bag<Class<? extends Component>> toClasses(Bag<Component> source,
	                                                         Bag<Class<? extends Component>> target) {
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i).getClass());
		}

		return target;
	}
	public void transmute(Entity e, int compositionId) {
		compositionIdMapper.get(compositionId).transmute(e);
	}

	public Iterable<? extends Map.Entry<Integer, TransmuterEntry>> entrySet() {
		return compositionIdMapper.entrySet();
	}

	public static class TransmuterEntry {
		public final List<Class<? extends Component>> componentTypes = new ArrayList<>();
		private transient EntityTransmuter transmuter;

		public TransmuterEntry(Bag<Class<? extends Component>> types) {
			for (Class<? extends Component> c : types) {
                if (ClassReflection.getAnnotation(c, Transient.class) != null)
                    continue;

                componentTypes.add(c);
            }
		}

		public TransmuterEntry() {}

		public void transmute(Entity e) {
			if (transmuter == null) {
				EntityTransmuterFactory factory = new EntityTransmuterFactory(e.getWorld());
				for (int i = 0, s = componentTypes.size(); s > i; i++)
					factory.add(componentTypes.get(i));

				transmuter = factory.build();
			}

			transmuter.transmute(e);
		}
	}
}
