/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ProgressBar;

public class LoadingScreen extends ENG_Container {

    public static class LoadingScreenContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            
            return new LoadingScreen(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            
            c.destroy();
        }

    }

    private final ENG_ProgressBar progressBar;

    /** @noinspection deprecation*/
    public LoadingScreen(String name, Bundle bundle) {
        super(name, bundle);

        progressBar = (ENG_ProgressBar) createView("progressbar", ENG_Container.PROGRESSBAR, 10.0f, 70.0f, 90.0f, 90.0f);
    }

    public ENG_ProgressBar getProgressBar() {
        return progressBar;
    }
}
