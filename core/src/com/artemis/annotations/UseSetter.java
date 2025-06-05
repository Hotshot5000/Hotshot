/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.artemis.EntityFactory;

/**
 * Invokes setter on component, instead of invoking fields. 
 * 
 * @see EntityFactory
 * @see Bind
 * @see Sticky
 */
@Retention(SOURCE)
@Target(METHOD)
@Documented
public @interface UseSetter {
	String value() default "";
}
