/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.Transient;

/**
 * Creates a tag, local to an instance of {@link com.artemis.io.SaveFileFormat}.
 *
 * @see com.artemis.io.SaveFileFormat#get(String)
 * @see com.artemis.io.SaveFileFormat#has(String)
 */
@Transient
public class SerializationTag extends PooledComponent {
	public String tag;

	@Override
	protected void reset() {
		tag = null;
	}
}
