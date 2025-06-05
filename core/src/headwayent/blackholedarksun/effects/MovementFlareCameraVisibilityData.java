/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/23/18, 2:18 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.effects;

import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.util.objectpool.ENG_PoolObject;

public class MovementFlareCameraVisibilityData implements ENG_PoolObject {

    public ENG_Boolean visibility = new ENG_Boolean();
    public ENG_Boolean retSet = new ENG_Boolean();

    @Override
    public void reset() {
        visibility = new ENG_Boolean();
        retSet = new ENG_Boolean();
    }
}
