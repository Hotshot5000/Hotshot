/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.Comparator;

public class ENG_RadixSortFunctorDistance implements
        Comparator<ENG_RenderablePass> {

    public ENG_Camera camera;

    public ENG_RadixSortFunctorDistance() {

    }

    public ENG_RadixSortFunctorDistance(ENG_Camera camera) {
        this.camera = camera;
    }

    public void set(ENG_Camera camera) {
        this.camera = camera;
    }

/*	public ENG_RadixSortFunctorDistance(ENG_Renderable renderable, ENG_Pass pass,
			ENG_Camera camera) {
		super(renderable, pass);
		
	}*/

    @Override
    public int compare(ENG_RenderablePass object1, ENG_RenderablePass object2) {

        float dista = object1.renderable.getSquaredViewDepth(camera);
        float distb = object2.renderable.getSquaredViewDepth(camera);
        if (dista < distb) {
            return 1;
        } else if (dista > distb) {
            return -1;
        }
        return 0;
    }

}
