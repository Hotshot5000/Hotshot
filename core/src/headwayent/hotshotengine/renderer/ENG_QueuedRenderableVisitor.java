/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_QueuedRenderableVisitor {

    public ENG_QueuedRenderableVisitor() {

    }

    public abstract void visit(ENG_RenderablePass rp);

    public abstract boolean visit(ENG_Pass p);

    public abstract void visit(ENG_Renderable r);
}
