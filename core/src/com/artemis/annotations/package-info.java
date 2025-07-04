/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/**
 * Annotations used by Artemis, both runtime and during compilation,
 * 
 * <p><b>Runtime:</b> {@link com.artemis.annotations.Wire}.</p>
 * 
 * <p><b>Annotation Processor for {@link com.artemis.EntityFactory}:</b> {@link com.artemis.annotations.Bind},
 * {@link com.artemis.annotations.Sticky}, {@link com.artemis.annotations.UseSetter}.</p>
 * 
 * <p><b>Bytecode weaving (provided by CLI tool and gradle/maven plugin):</b> {@link com.artemis.annotations.PackedWeaver},
 * {@link com.artemis.annotations.PooledWeaver}, {@link com.artemis.annotations.PreserveProcessVisiblity},
 * {@link com.artemis.annotations.Profile}.</p>
 */
package com.artemis.annotations;
