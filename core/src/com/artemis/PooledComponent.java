/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

/**
 * Component type that recycles instances.
 * <p>
 * Expects no <code>final</code> fields.
 *
 * @see com.artemis.annotations.PooledWeaver to automate pooled component creation.
 */
public abstract class PooledComponent extends Component {

	/** Called whenever the component is recycled. Implementation should reset component to pristine state. */
	protected abstract void reset();
}
