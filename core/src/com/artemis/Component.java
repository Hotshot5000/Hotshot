/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

/**
 * Components are pure data classes with optionally some helper methods.
 * <p/>
 * Extend to create your own components. Decorate with {@link com.artemis.annotations.PooledWeaver}
 * or manually extend {@link PooledComponent} to make the component pooled.
 *
 * @author Arni Arent
 * @see PooledComponent
 * @see com.artemis.annotations.PooledWeaver
 */
public abstract class Component {
}
