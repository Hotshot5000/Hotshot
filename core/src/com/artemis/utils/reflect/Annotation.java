/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.utils.reflect;

/** Provides information about, and access to, an annotation of a field, class or interface.
 * @author dludwig */
public final class Annotation {

	private final java.lang.annotation.Annotation annotation;

	Annotation (java.lang.annotation.Annotation annotation) {
		this.annotation = annotation;
	}

	@SuppressWarnings("unchecked")
	public <T extends java.lang.annotation.Annotation> T getAnnotation (Class<T> annotationType) {
		if (annotation.annotationType().equals(annotationType)) {
			return (T) annotation;
		}
		return null;
	}

	public Class<? extends java.lang.annotation.Annotation> getAnnotationType () {
		return annotation.annotationType();
	}
}
