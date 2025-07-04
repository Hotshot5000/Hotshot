/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.Component;

/**
 * Transforms a {@link Component} into a {@link com.artemis.PooledComponent}. Component transformation
 * takes place during the <code>artemis</code> goal defined in <code>artemis-odb-maven-plugin</code>
 * or the <code>weave</code> task in <code>artemis-odb-gradle-plugin</code>.
 * <p>
 * This feature helps mitigate garbage collection related freezes and stuttering on the Android platform.
 * It is considered stable. If pooling is not available on a platform, this feature will degrade
 * gracefully to vanilla components.
 * 
 * @see <a href="https://github.com/junkdog/artemis-odb/wiki/%40PooledWeaver">Component pooling</a>
 *	  on the wiki.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface PooledWeaver {
	
	/**
	 * If true, forces weaving even if maven property <code>enablePooledWeaving</code> is
	 * set to <code>false</code>. 
	 */
	boolean forceWeaving() default false;
}
