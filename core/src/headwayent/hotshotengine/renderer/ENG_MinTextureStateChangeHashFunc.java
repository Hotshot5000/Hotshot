/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_MinTextureStateChangeHashFunc extends ENG_HashFunc {

    public static final ENG_MinTextureStateChangeHashFunc sMinTextureStateChangeHashFunc =
            new ENG_MinTextureStateChangeHashFunc();

    private ENG_MinTextureStateChangeHashFunc() {

    }

    @Override
    public int hash(ENG_Pass p) {
        
//        p.mTexUnitChangeMutex.lock();
        int hash = p.getIndex().getValue() << 28;
        int c = p.getNumTextureUnitStates();
        ENG_TextureUnitState t0 = null;
        ENG_TextureUnitState t1 = null;
        if (c > 0) {
            t0 = p.getTextureUnitState((short) 0);
        }
        if (c > 1) {
            t1 = p.getTextureUnitState((short) 1);
        }

        if ((t0 != null) && (!t0.getTextureName().isEmpty())) {
            hash += (t0.getTextureName().hashCode() % (1 << 14)) << 14;
        }
        if ((t1 != null) && (!t1.getTextureName().isEmpty())) {
            hash += (t1.getTextureName().hashCode() % (1 << 14));
        }
//        p.mTexUnitChangeMutex.unlock();
        return hash;
    }

}
