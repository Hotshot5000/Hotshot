/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils;

import com.artemis.*;
import com.artemis.managers.*;
import com.artemis.utils.reflect.ClassReflection;

import java.util.UUID;

/**
 * Non-reusable entity creation helper for rapid prototyping.
 * <p>
 * Discouraged for use other than rapid prototyping and simple games.
 * Use {@link ComponentMapper} instead.
 * <p>
 * Example: new Builder(world)
 * .with(Pos.class, Anim.class)
 * .tag("boss")
 * .player("player1")
 * .group("enemies")
 * .build();
 *
 * @author Daan van Yperen
 * @author Junkdog
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public class EntityBuilder {

	private final World world;
	private final EntityEdit edit;

	/** Begin building new entity.*/
	public EntityBuilder(World world) {
		this.world = world;
		edit = world.createEntity().edit();
	}

	/** Begin building new entity based on archetype.*/
	public EntityBuilder(World world, Archetype archetype) {
		this.world = world;
		edit = world.createEntity(archetype).edit();
	}

	/** Add component to entity. */
	public EntityBuilder with(Component component) {
		edit.add(component);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2) {
		edit.add(component1);
		edit.add(component2);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		edit.add(component4);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component component1, Component component2, Component component3, Component component4, Component component5) {
		edit.add(component1);
		edit.add(component2);
		edit.add(component3);
		edit.add(component4);
		edit.add(component5);
		return this;
	}

	/** Add components to entity. */
	public EntityBuilder with(Component... components) {
        for (Component component : components) {
            edit.add(component);
        }
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component) {
		edit.create(component);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2) {
		edit.create(component1);
		edit.create(component2);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		edit.create(component4);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4, Class<? extends Component> component5) {
		edit.create(component1);
		edit.create(component2);
		edit.create(component3);
		edit.create(component4);
		edit.create(component5);
		return this;
	}

	/** Add artemis managed components to entity. */
	public EntityBuilder with(Class<? extends Component>... components) {
        for (Class<? extends Component> component : components) {
            edit.create(component);
        }
		return this;
	}

	/** Set UUID of entity */
	public EntityBuilder UUID(UUID uuid)
	{
		resolveManager(UuidEntityManager.class).setUuid(edit.getEntity(), uuid);
		return this;
	}

	/**
	 * Register entity with owning player.
	 * An entity can only belong to a single player at a time.
	 * Requires registered PlayerManager.
	 */
	public EntityBuilder player(String player) {
		resolveManager(PlayerManager.class).setPlayer(edit.getEntity(), player);
		return this;
	}

	/** Register entity with tag. Requires registered TagManager */
	public EntityBuilder tag(String tag) {
		resolveManager(TagManager.class).register(tag, edit.getEntity());
		return this;
	}

	/** Register entity with group. Requires registered TagManager */
	public EntityBuilder group(String group) {
		resolveManager(GroupManager.class).add(edit.getEntity(), group);
		return this;
	}
	
	/** Register entity with multiple groups. Requires registered TagManager */
	public EntityBuilder groups(String... groups) {
        for (String group : groups) {
            group(group);
        }
		return this;
	}

	/** Assemble, add to world */
	public Entity build() {
		return edit.getEntity();
	}

	/** Fetch manager or throw RuntimeException if not registered. */
	protected <T extends BaseSystem> T resolveManager(Class<T> type) {
		final T teamManager = world.getSystem(type);
		if ( teamManager == null ) {
			throw new RuntimeException("Register " + ClassReflection.getSimpleName(type) + " with your artemis world.");
		}
		return teamManager;
	}
}
