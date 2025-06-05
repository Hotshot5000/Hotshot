/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import java.lang.Class;import java.lang.Comparable;import java.lang.Object;import java.lang.Override;


/**
 * Artemis pieces with priority pending registration.
 *
 * @author Daan van Yperen
 * @see WorldConfigurationBuilder
 */
class ConfigurationElement<T> implements Comparable<ConfigurationElement<T>> {
	public final int priority;
	public final Class<?> itemType;
	public final T item;

	public ConfigurationElement(T item, int priority) {
		this.item = item;
		itemType = item.getClass();
		this.priority = priority;
	}

	@Override
	public int compareTo(ConfigurationElement<T> o) {
		// Sort by priority descending.
		return o.priority - priority;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || !(o == null || getClass() != o.getClass()) && item.equals(((ConfigurationElement<?>) o).item);
	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item) {
		return of(item, WorldConfigurationBuilder.Priority.NORMAL);
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item, int priority) {
		return new ConfigurationElement<>(item, priority);
	}
}
