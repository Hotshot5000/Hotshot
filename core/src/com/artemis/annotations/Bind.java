/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artemis.Component;
import com.artemis.EntityFactory;

/**
 * Associates components to {@link EntityFactory EntityFactories}, either by
 * annotating at the class or method level. For all methods declared by an
 * {@link EntityFactory}, the parameters must match fields declared by the component.
 * Only primitive, enum and string types are currently supported. The {@link UseSetter} annotation
 * can be employed when setter invocation is desirable. 
 * 
 * <p>Annotated methods always work with a single component type, while multiple component
 * types can be given when annotation an {@link EntityFactory} interface.</p> 
 * 
 * @see EntityFactory
 * @see Sticky
 * @see UseSetter
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Bind {
	Class<? extends Component>[] value();
}
