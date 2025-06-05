/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_SceneQuery.WorldFragment;

import java.util.LinkedList;

public class ENG_SceneQueryResult<E> {

    public LinkedList<ENG_MovableObject> movables = new LinkedList<>();
    public LinkedList<WorldFragment> worldFragments = new LinkedList<>();
}
