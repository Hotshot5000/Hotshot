/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.renderer.ENG_ParticleSystemManager;

public class ParticleFXPlugin {

    public static void install() {
        ENG_ParticleSystemManager mgr = ENG_ParticleSystemManager.getSingleton();

        mgr.addEmitterFactory(new AreaEmitterFactory());
        mgr.addEmitterFactory(new BoxEmitterFactory());
        mgr.addEmitterFactory(new CylinderEmitterFactory());
        mgr.addEmitterFactory(new EllipsoidEmitterFactory());
        mgr.addEmitterFactory(new HollowEllipsoidEmitterFactory());
        mgr.addEmitterFactory(new PointEmitterFactory());
        mgr.addEmitterFactory(new RingEmitterFactory());

        mgr.addAffectorFactory(new ColourFaderAffectorFactory());
        mgr.addAffectorFactory(new ColourFaderAffectorFactory2());
        mgr.addAffectorFactory(new DirectionRandomiserAffectorFactory());
        mgr.addAffectorFactory(new LinearForceAffectorFactory());
    }
}
