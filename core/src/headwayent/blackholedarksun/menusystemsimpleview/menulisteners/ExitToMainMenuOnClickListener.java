/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.menulisteners;

import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithTypeFactory;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.Bundle;

/**
 * Created by Sebi on 25.05.2014.
 */
public class ExitToMainMenuOnClickListener extends OnClickListenerWithType {

    public static class ExitToMainMenuOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "ExitToMainMenuOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new ExitToMainMenuOnClickListener(type, bundle);
        }
    }

    /** @noinspection deprecation*/
    public ExitToMainMenuOnClickListener(String type, Bundle bundle) {
        super(type, bundle);
    }

    @Override
    public boolean onClick(int x, int y) {
        WorldManager.getSingleton().exitToMainMenu();
        return true;
    }
}
