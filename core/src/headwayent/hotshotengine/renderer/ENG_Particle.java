/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector4D;

public class ENG_Particle {

    /// Type of particle
    public enum ParticleType {
        Visual,
        Emitter
    }

    /// Parent ParticleSystem
    protected ENG_ParticleSystem mParentSystem;
    /// Additional visual data you might want to associate with the Particle
    protected Object mVisual;

    /// Does this particle have it's own dimensions?
    public boolean mOwnDimensions;
    /// Personal width if mOwnDimensions == true
    public float mWidth;
    /// Personal height if mOwnDimensions == true
    public float mHeight;
    /// Current rotation value
    public final ENG_Radian rotation = new ENG_Radian();
    // Note the intentional public access to internal variables
    // Accessing via get/set would be too costly for 000's of particles
    /// World position
    public final ENG_Vector4D position = new ENG_Vector4D(true);
    /// Direction (and speed) 
    public final ENG_Vector4D direction = new ENG_Vector4D();
    /// Current colour
    public final ENG_ColorValue colour = new ENG_ColorValue(ENG_ColorValue.WHITE);
    /// Time to live, number of seconds left of particles natural life
    public float timeToLive = 10;
    /// Total Time to live, number of seconds of particles natural life
    public float totalTimeToLive = 10;
    /// Speed of rotation in radians/sec
    public ENG_Radian rotationSpeed = new ENG_Radian();
    /// Determines the type of particle.
    public ParticleType particleType = ParticleType.Visual;

    public ENG_Particle() {

    }

    public void setDimensions(float width, float height) {
        mOwnDimensions = true;
        mWidth = width;
        mHeight = height;
        mParentSystem._notifyParticleResized();
    }

    public boolean hasOwnDimensions() {
        return mOwnDimensions;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setRotation(ENG_Radian rot) {
        rotation.set(rot);
        if (rot.valueRadians() != 0.0f) {
            mParentSystem._notifyParticleRotated();
        }
    }

    public ENG_Radian getRotation() {
        return new ENG_Radian(rotation);
    }

    public void getRotation(ENG_Radian rad) {
        rad.set(rotation);
    }

    public void _notifyOwner(ENG_ParticleSystem p) {
        mParentSystem = p;
    }

    public void _notifyVisualData(Object o) {
        mVisual = o;
    }

    public Object getVisualData() {
        return mVisual;
    }

    public void resetDimensions() {
        mOwnDimensions = false;
    }
}
