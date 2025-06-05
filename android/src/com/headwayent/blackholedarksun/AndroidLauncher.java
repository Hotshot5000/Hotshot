/*
 * Created by Sebastian Bugiu on 4/18/23, 1:21 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/9/23, 10:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.headwayent.blackholedarksun;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.headwayent.blackholedarksun.BlackholeDarksunMain;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new BlackholeDarksunMain(), config);
	}
}
