/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis.injection;

import com.artemis.*;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * By default, injects {@link ComponentMapper}, {@link BaseSystem} and {@link Manager} types into systems and
 * managers. Can also inject arbitrary types if registered through {@link WorldConfiguration#register}.
 * <p>
 * Caches all type-information.
 *
 * <p>
 * For greater control over the dependency-resolution, provide a {@link FieldHandler} to {@link #setFieldHandler(FieldHandler)},
 * which will be used to resolve dependency values instead.
 * </p>
 *
 * @author Arni Arent
 * @author Snorre E. Brekke
 * @see com.artemis.injection.FieldHandler
 */
public final class CachedInjector implements Injector {
	private final InjectionCache cache = new InjectionCache();
	private FieldHandler fieldHandler;

	@Override
	public Injector setFieldHandler(FieldHandler fieldHandler) {
		this.fieldHandler = fieldHandler;
		return this;
	}

	@Override
	public void initialize(World world, Map<String, Object> injectables) {
		if (fieldHandler == null) {
			fieldHandler = new FieldHandler(cache);
		}

		fieldHandler.initialize(world, injectables);
	}

	@Override
	public boolean isInjectable(Object target) {
		try {
			CachedClass cachedClass = cache.getCachedClass(target.getClass());
			return cachedClass.wireType == WireType.WIRE;
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring", e);
		}
	}

	@Override
	public void inject(Object target) throws RuntimeException {
		try {
			Class<?> clazz = target.getClass();
			CachedClass cachedClass = cache.getCachedClass(clazz);

			if (cachedClass.wireType == WireType.WIRE) {
				injectValidFields(target, cachedClass);
			} else {
				injectAnnotatedFields(target, cachedClass);
			}
		} catch (RuntimeException e ) {
			throw new MundaneWireException("Error while wiring " + target.getClass().getName(), e);
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring " + target.getClass().getName(), e);
		}
	}

	private void injectValidFields(Object target, CachedClass cachedClass)
			throws ReflectionException {
		Field[] declaredFields = getAllInjectableFields(cachedClass);
        for (Field declaredField : declaredFields) {
            injectField(target, declaredField, cachedClass.failOnNull);
        }
	}

	private Field[] getAllInjectableFields(CachedClass cachedClass) {
		Field[] declaredFields = cachedClass.allFields;
		if (declaredFields == null) {
			List<Field> fieldList = new ArrayList<>();
			Class<?> clazz = cachedClass.clazz;
			collectDeclaredInjectableFields(fieldList, clazz);

			while (cachedClass.injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
				collectDeclaredInjectableFields(fieldList, clazz);
			}
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            cachedClass.allFields = declaredFields = fieldList.toArray(new Field[fieldList.size()]);
		}
		return declaredFields;
	}

	private void collectDeclaredInjectableFields(List<Field> fieldList, Class<?> clazz) {
		try {
			if (cache.getCachedClass(clazz).wireType != WireType.SKIPWIRE) {
				Field[] classFields = ClassReflection.getDeclaredFields(clazz);
                for (Field classField : classFields) {
                    if (isWireable(classField)) {
                        fieldList.add(classField);
                    }
                }
			}
		} catch (ReflectionException e) {
			throw new MundaneWireException("Error while wiring", e);
		}
	}

	private boolean isWireable(Field field) {
		return cache.getCachedField(field).wireType != WireType.SKIPWIRE;
	}

	private void injectAnnotatedFields(Object target, CachedClass cachedClass)
			throws ReflectionException {
		injectClass(target, cachedClass);
	}

	private void injectClass(Object target, CachedClass cachedClass) throws ReflectionException {
		Field[] declaredFields = getAllInjectableFields(cachedClass);
        for (Field field : declaredFields) {
            CachedField cachedField = cache.getCachedField(field);
            if (cachedField.wireType != WireType.IGNORED) {
                injectField(target, field, cachedField.wireType == WireType.WIRE);
            }
        }
	}

	private void injectField(Object target, Field field, boolean failOnNotInjected)
			throws ReflectionException {
		Class<?> fieldType;
		try {
			fieldType = field.getType();
		} catch (RuntimeException ignore) {
			// Swallow exception caused by missing typedata on gwt platfString.format("Failed to inject %s into %s:
			// %s not registered with world.")orm.
			// @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for
			// users failing to add systems/components to gwt reflection inclusion config.
			return;
		}

		Object resolve = fieldHandler.resolve(fieldType, field);
		if (resolve != null) {
			setField(target, field, resolve);
		}

		if (resolve == null && failOnNotInjected && cache.getFieldClassType(fieldType) != ClassType.CUSTOM) {
			throw onFailedInjection(fieldType.getSimpleName(), field);
		}
	}

	private void setField(Object target, Field field, Object fieldValue) throws ReflectionException {
		field.setAccessible(true);
		field.set(target, fieldValue);
	}

	private MundaneWireException onFailedInjection(String typeName, Field failedInjection) {
		String error = "Failed to inject " + failedInjection.getType().getName() +
                " into " + failedInjection.getDeclaringClass().getName() + ": " +
                typeName + " not registered with world.";

		return new MundaneWireException(error);
	}

}
