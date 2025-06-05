/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.renderer.ENG_ParticleEmitter;
import headwayent.hotshotengine.renderer.ENG_ParticleEmitterFactory;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class EllipsoidEmitterFactory extends ENG_ParticleEmitterFactory {

    @Override
    public String getName() {

        return "Ellipsoid";
    }

    @Override
    public ENG_ParticleEmitter createEmitter(ENG_ParticleSystem psys) {

        EllipsoidEmitter e = new EllipsoidEmitter(psys);
        mEmitters.add(e);
        return e;
    }

}
