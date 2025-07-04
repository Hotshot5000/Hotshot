/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.ConverterUtil;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Maintains state of all component types which can reference other components.
 */
class ReferenceTracker {
	final Bag<EntityReference> referenced = new Bag<>();
	private final Set<Class<?>> referencingTypes = new HashSet<>();
	private final Set<Field> referencingFields = new HashSet<>();

	private final BitSet entityIds = new BitSet();
	private final World world;

	ReferenceTracker(World world) {
		this.world = world;
	}

	void inspectTypes(World world) {
		clear();
		ComponentManager cm = world.getComponentManager();
		for (ComponentType ct : cm.getComponentTypes()) {
			inspectType(ct.getType());
		}
	}

	void inspectTypes(Collection<Class<? extends Component>> types) {
		clear();
		for (Class<?> component : types) {
			inspectType(component);
		}
	}

	private void clear() {
		referencingFields.clear();
		referencingTypes.clear();
		referenced.clear();
	}

	private void inspectType(Class<?> type) {
		if (referencingTypes.contains(type))
			return;

		Field[] fields = ClassReflection.getDeclaredFields(type);
        for (Field f : fields) {
            if (isReferencingEntity(f)) {
                referencingFields.add(f);
                referencingTypes.add(type);
                referenced.add(new EntityReference(type, f));
            }
        }
	}

	void addEntityReferencingComponent(Component c) {
		Class<? extends Component> componentClass = c.getClass();
		if (!referencingTypes.contains(componentClass))
			return;

		for (int i = 0, s = referenced.size(); s > i; i++) {
			EntityReference ref = referenced.get(i);

			if (ref.componentType == componentClass) {
				// a component can be referenced once per field
				// referencing another entity
				ref.operations.add(c);
			}
		}
	}


	void translate(Bag<Entity> translations) {
		for (EntityReference ref : referenced) {
			ref.translate(translations);
		}

		translations.clear();
	}

	EntityReference find(Class<?> componentType, String fieldName) {
		for (int i = 0, s = referenced.size(); s > i; i++) {
			EntityReference ref = referenced.get(i);
			if (ref.componentType.equals(componentType) && ref.field.getName().equals(fieldName))
				return ref;
		}

		throw new RuntimeException(
				componentType.getSimpleName() + "." + fieldName);
	}

	private boolean isReferencingEntity(Field f) {
		boolean explicitEntityId = f.getDeclaredAnnotation(EntityId.class) != null;
		Class type = f.getType();
		return (Entity.class == type)
				|| (Bag.class == type) // due to GWT limitations
				|| (int.class == type && explicitEntityId)
				|| (IntBag.class == type && explicitEntityId);
	}

	void preWrite(SaveFileFormat save) {
		entityIds.clear();

		ConverterUtil.toBitSet(save.entities, entityIds);
		boolean foundNew = true;

		BitSet bs = entityIds;

		while (foundNew) {
			foundNew = false;
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
				for (Field f : referencingFields) {
					foundNew |= findReferences(i, f, bs);
				}
			}
		}

		ConverterUtil.toIntBag(entityIds, save.entities);
	}

	private boolean findReferences(int entityId, Field f, BitSet referencedIds) {
		Component c = world.getEntity(entityId).getComponent(f.getDeclaringClass());
		if (c == null)
			return false;

		Class type = f.getType();
		try {
			if (type.equals(int.class)) {
				return updateReferenced((Integer)f.get(c), referencedIds);
			} else if (type.equals(Entity.class)) {
				return updateReferenced((Entity)f.get(c), referencedIds);
			} else if (type.equals(IntBag.class)) {
				return updateReferenced((IntBag)f.get(c), referencedIds);
			} else if (type.equals(Bag.class)) {
				return updateReferenced((Bag<Entity>)f.get(c), referencedIds);
			} else {
				throw new RuntimeException("unknown type: " + type);
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean updateReferenced(Bag<Entity> entities, BitSet referencedIds) {
		boolean updated = false;
		for (int i = 0; i < entities.size(); i++)
			updated |= updateReferenced(entities.get(i), referencedIds);

		return updated;
	}

	private boolean updateReferenced(IntBag ids, BitSet referencedIds) {
		boolean updated = false;
		for (int i = 0; i < ids.size(); i++)
			updated |= updateReferenced(ids.get(i), referencedIds);

		return updated;
	}


	private boolean updateReferenced(Entity e, BitSet referencedIds) {
		return (e != null)
			? updateReferenced(e.getId(), referencedIds)
			: false;
	}

	private boolean updateReferenced(int entityId, BitSet referencedIds) {
		if (entityId > -1 && !referencedIds.get(entityId)) {
			referencedIds.set(entityId);
			return true;
		} else {
			return false;
		}
	}
}
