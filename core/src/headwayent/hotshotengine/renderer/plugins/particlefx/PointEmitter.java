/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleEmitter;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class PointEmitter extends ENG_ParticleEmitter {

    public PointEmitter(ENG_ParticleSystem p) {
        super(p);

        mType = "Point";

        if (getStringInterface().createParamDictionary("PointEmitter")) {
            addBaseParameters();
        }
    }

    @Override
    public void _initParticle(ENG_Particle pParticle) {

        super._initParticle(pParticle);
        // Point emitter emits from own position
        pParticle.position.set(mPosition);

        // Generate complex data by reference
        genEmissionColour(pParticle.colour);
        genEmissionDirection(pParticle.direction);
        genEmissionVelocity(pParticle.direction);

        // Generate simpler data
        pParticle.timeToLive = pParticle.totalTimeToLive = genEmissionTTL();
    }

    public short _getEmissionCount(float timeElapsed) {
        // Use basic constant emission
        return genConstantEmissionCount(timeElapsed);
    }

}
