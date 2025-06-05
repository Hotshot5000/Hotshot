/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public interface ENG_RaySceneQueryListener {

    boolean queryResult(ENG_MovableObject obj, float distance);

//    boolean queryResult(WorldFragment fragment, float distance);
}
