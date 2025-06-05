/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class BoxEmitter extends AreaEmitter {

    private final ENG_Vector4D xOff = new ENG_Vector4D();
    private final ENG_Vector4D yOff = new ENG_Vector4D();
    private final ENG_Vector4D zOff = new ENG_Vector4D();
    private final ENG_Vector4D temp = new ENG_Vector4D();

    public BoxEmitter(ENG_ParticleSystem p) {
        super(p);
        
        initDefaults("Box");
    }

    @Override
    public void _initParticle(ENG_Particle particle) {

        super._initParticle(particle);


        mXRange.mul(ENG_Utility.symmetricRandom(), xOff);
        mYRange.mul(ENG_Utility.symmetricRandom(), yOff);
        mZRange.mul(ENG_Utility.symmetricRandom(), zOff);

        temp.set(ENG_Math.PT4_ZERO);
        mPosition.add(xOff, temp);
        temp.addInPlace(yOff);
        temp.addInPlace(zOff);
        particle.position.set(temp);

        // Generate complex data by reference
        genEmissionColour(particle.colour);
        genEmissionDirection(particle.direction);
        genEmissionVelocity(particle.direction);

        // Generate simpler data
        particle.timeToLive = particle.totalTimeToLive = genEmissionTTL();
    }

}
