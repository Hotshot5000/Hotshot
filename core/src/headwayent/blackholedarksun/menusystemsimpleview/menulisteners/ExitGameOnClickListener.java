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
public class ExitGameOnClickListener extends OnClickListenerWithType {

    public static class ExitGameOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "ExitGameOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new ExitGameOnClickListener(type, bundle);
        }
    }


    /** @noinspection deprecation*/
    public ExitGameOnClickListener(String type, Bundle bundle) {
        super(type, bundle);
    }

    @Override
    public boolean onClick(int x, int y) {
        WorldManager.getSingleton().exitGame();
        return true;
    }
}
