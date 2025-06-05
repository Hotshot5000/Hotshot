/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;

public abstract class ENG_ParticleAffectorFactory {

    protected final ArrayList<ENG_ParticleAffector> mAffectors =
            new ArrayList<>();

    public abstract String getName();

    public abstract ENG_ParticleAffector createAffector(ENG_ParticleSystem psys);

    public void destroyAffector(ENG_ParticleAffector e) {
        mAffectors.remove(e);
    }
}
