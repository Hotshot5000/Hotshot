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

import com.artemis.EntityFactory;

/**
 * Stickied methods have their parameter values persisted
 * for the duration of the {@link EntityFactory} instance.
 * <p>
 * After a factory has created the first entity, stickied methods
 * throw an {@link IllegalArgumentException}. Calling {@link EntityFactory#copy()}
 * allows updating stickied values again - until the new factory
 * creates an entity.
 * </p>
 *
 * @see EntityFactory
 * @see Bind
 * @see UseSetter
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface Sticky {}
