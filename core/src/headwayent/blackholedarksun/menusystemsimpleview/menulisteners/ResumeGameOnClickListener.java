/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.menulisteners;

import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithTypeFactory;
import headwayent.hotshotengine.Bundle;

/**
 * Created by Sebi on 25.05.2014.
 */
public class ResumeGameOnClickListener extends OnClickListenerWithType {

    public static class ResumeGameOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "ResumeGameOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new ResumeGameOnClickListener(type, bundle);
        }
    }


    /** @noinspection deprecation*/
    public ResumeGameOnClickListener(String type, Bundle bundle) {
        super(type, bundle);
    }

    @Override
    public boolean onClick(int x, int y) {
        SimpleViewGameMenuManager.resumeGame();
        return true;
    }
}
