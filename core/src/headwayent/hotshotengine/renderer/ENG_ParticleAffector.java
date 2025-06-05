/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;

public abstract class ENG_ParticleAffector implements ENG_StringIntefaceInterface {

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    protected String mType = "";
    protected final ENG_ParticleSystem mParent;

    protected void addBaseParameters() {

    }

    public ENG_ParticleAffector(ENG_ParticleSystem pSys) {
        mParent = pSys;
    }

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }

    public void _initParticle(ENG_Particle particle) {

    }

    /**
     * @param pSystem
     * @param timeElapsed Number of seconds since last call
     */
    public abstract void _affectParticles(
            ENG_ParticleSystem pSystem, float timeElapsed);

    public String getType() {
        return mType;
    }

}
