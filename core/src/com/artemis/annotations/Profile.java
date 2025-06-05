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

import com.artemis.utils.ArtemisProfiler;

/**
 * Profile EntitySystems with user-specified profiler class, implementing ArtemisProfiler.
 * 
 * <p>Injects conditional profiler call at start of <code>begin()</code> and before any exit
 * point in <code>end()</code>.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Profile
{
	Class<? extends ArtemisProfiler> using();
	boolean enabled() default true;
}