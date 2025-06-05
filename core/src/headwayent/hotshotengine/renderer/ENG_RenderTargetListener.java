/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public interface ENG_RenderTargetListener {

/*	public class RenderTargetEvent {

		
	}
	
	class RenderTargetViewportEvent {
		
	}*/

    void preRenderTargetUpdate(ENG_RenderTargetEvent evt);

    void postRenderTargetUpdate(ENG_RenderTargetEvent evt);

    void preViewportUpdate(ENG_RenderTargetViewportEvent evt);

    void postViewportUpdate(ENG_RenderTargetViewportEvent evt);

    void viewportAdded(ENG_RenderTargetViewportEvent evt);

    void viewportRemoved(ENG_RenderTargetViewportEvent evt);
}
