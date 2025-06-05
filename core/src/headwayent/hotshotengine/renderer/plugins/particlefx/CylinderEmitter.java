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

public class CylinderEmitter extends AreaEmitter {

    private final ENG_Vector4D temp = new ENG_Vector4D();
    private final ENG_Vector4D temp2 = new ENG_Vector4D();

    public CylinderEmitter(ENG_ParticleSystem p) {
        super(p);
        
        initDefaults("Cylinder");
    }

    @Override
    public void _initParticle(ENG_Particle particle) {

        super._initParticle(particle);

        float x, y, z;

        //noinspection ConditionalBreakInInfiniteLoop
        while (true) {
            x = ENG_Utility.symmetricRandom();
            y = ENG_Utility.symmetricRandom();
            z = ENG_Utility.symmetricRandom();

            if (x * x + y * y <= 1.0f) {
                break;
            }
        }

        temp.set(ENG_Math.PT4_ZERO);
        temp2.set(ENG_Math.PT4_ZERO);

        mXRange.mul(x, temp);
        mPosition.add(temp, temp2);
        mYRange.mul(y, temp);
        temp2.addInPlace(temp);
        mZRange.mul(z, temp);
        temp2.addInPlace(temp);
        particle.position.set(temp);

        // Generate complex data by reference
        genEmissionColour(particle.colour);
        genEmissionDirection(particle.direction);
        genEmissionVelocity(particle.direction);

        // Generate simpler data
        particle.timeToLive = particle.totalTimeToLive = genEmissionTTL();
    }

}
