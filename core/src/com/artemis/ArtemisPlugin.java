/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

/**
 * Plugin for artemis-odb.
 *
 * @author Daan van Yperen
 */
public interface ArtemisPlugin {

	/**
	 * Register your plugin.
	 * <p>
	 * Set up all your dependencies here.
	 * - systems
	 * - field resolvers
	 * - other plugins
	 * <p>
	 * Always prefer to use {@link WorldConfigurationBuilder#dependsOn} as it can handle repeated dependencies,
	 * as opposed to {@link WorldConfigurationBuilder#with}, which will throw an exception upon attempting to
	 * add a pre-existing class.
	 *
	 * @param b builder to register your dependencies with.
	 */
	void setup(WorldConfigurationBuilder b);
}
