/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.ArrayList;

public abstract class ENG_RenderObjectListener {


    /** @noinspection deprecation*/
    public abstract void notifyRenderSingleObject(ENG_Renderable rend, ENG_Pass pass,
                                                  ENG_AutoParamDataSource source, ArrayList<ENG_Light> lightList,
                                                  boolean suppressRenderStateChanges);
}
