/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

import com.artemis.utils.Bag;


/**
 * Let's the user set the size.
 * <p>
 * Setting the size does not resize the bag, nor will it clean up contents
 * beyond the given size. Only use this if you know what you are doing!
 * </p>
 *
 * @author junkdog
 *
 * @param <T>
 *			object type this bag holds
 */
class WildBag<T> extends Bag<T> {


	/**
	 * Set the size.
	 * <p>
	 * This will not resize the bag, nor will it clean up contents beyond the
	 * given size. Use with caution.
	 * </p>
	 *
	 * @param size
	 *			the size to set
	 */
	void setSize(int size) {
		this.size = size;
	}

}
