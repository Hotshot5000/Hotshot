/*
 * Created by Sebastian Bugiu on 4/9/23, 10:06 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:44 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package com.artemis;

/**
 * Most basic system.
 * <p>
 * Upon calling world.process(), your systems are processed in sequence.
 * <p>
 * Flow:
 * {@link #initialize()} - Initialize your system, on top of the dependency injection.
 * {@link #begin()} - Called before the entities are processed.
 * {@link #processSystem()} - Called once per cycle.
 * {@link #end()} - Called after the entities have been processed.
 * 
 * @see com.artemis.annotations.Wire
 */
public abstract class BaseSystem {
	/** The world this system belongs to. */
	protected World world;
	/** If the system is enabled or not. */
	boolean enabled = true;
	public boolean passive;

	public BaseSystem() {}

	/**
	 * Called before system processing begins.
	 * <p>
	 * <b>Nota Bene:</b> Any entities created in this method
	 * won't become active until the next system starts processing
	 * or when a new processing rounds begins, whichever comes first.
	 * </p>
	 */
	protected void begin() {}

	/**
	 * Process system.
	 * <p>
	 * Does nothing if {@link #checkProcessing()} is false or the system
	 * is disabled.
	 *
	 * @see InvocationStrategy
	 */
	public final void process() {
		if(enabled && checkProcessing()) {
			begin();
			processSystem();
			end();
		}
	}

	/**
	 * Process the system.
	 */
	protected abstract void processSystem();

	/**
	 * Called after the systems has finished processing.
	 */
	protected void end() {}

	/**
	 * Does the system desire processing.
	 * <p>
	 * Useful when the system is enabled, but only occasionally
	 * needs to process.
	 * <p>
	 * This only affects processing, and does not affect events
	 * or subscription lists.
	 *
	 * @return true if the system should be processed, false if not.
	 * @see #isEnabled() both must be true before the system will process.
	 */
	@SuppressWarnings("static-method")
	protected boolean checkProcessing() {
		return true;
	}

	/**
	 * Override to implement code that gets executed when systems are
	 * initialized.
	 * <p>
	 * Note that artemis native types like systems, factories and
	 * component mappers are automatically injected by artemis.
	 */
	protected void initialize() {}

	/**
	 * Check if the system is enabled.
	 *
	 * @return {@code true} if enabled, otherwise false
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enabled systems run during {@link #process()}.
	 * <p>
	 * This only affects processing, and does not affect events
	 * or subscription lists.
	 * <p>
	 * Systems are enabled by default.
	 *
	 * @param enabled
	 *			system will not run when set to false
	 * @see #checkProcessing() both must be true before the system will process.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	//Fucking retards forgot about passive systems of course.


	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * Set the world this system works on.
	 *
	 * @param world
	 *			the world to set
	 */
	protected void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Get the world associated with the manager.
	 *
	 * @return the associated world
	 */
	protected World getWorld() {
		return world;
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}
}
