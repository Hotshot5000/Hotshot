/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 9:57 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.annotations.Profile;


/**
 * @see Profile
 */
public interface ArtemisProfiler {
	void start();
	void stop();
	void initialize(BaseSystem owner, World world);
}