/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_RenderablePass {

    public ENG_Renderable renderable;
    public ENG_Pass pass;

    public ENG_RenderablePass(ENG_Renderable renderable, ENG_Pass pass) {
        set(renderable, pass);
    }

    public void set(ENG_Renderable renderable, ENG_Pass pass) {
        this.renderable = renderable;
        this.pass = pass;
    }
}
