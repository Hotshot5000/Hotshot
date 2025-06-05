/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.renderer.ENG_ParticleAffector;
import headwayent.hotshotengine.renderer.ENG_ParticleAffectorFactory;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class ColourFaderAffectorFactory2 extends ENG_ParticleAffectorFactory {

    @Override
    public String getName() {
        
        return "ColourFader2";
    }

    @Override
    public ENG_ParticleAffector createAffector(ENG_ParticleSystem psys) {
        
        ColourFaderAffector2 a = new ColourFaderAffector2(psys);
        mAffectors.add(a);
        return a;
    }

}
