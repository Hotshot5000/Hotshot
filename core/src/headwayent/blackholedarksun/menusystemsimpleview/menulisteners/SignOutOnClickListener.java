/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.menulisteners;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnClickListenerWithTypeFactory;
import headwayent.hotshotengine.Bundle;

/**
 * Created by Sebastian on 15.05.2015.
 */
public class SignOutOnClickListener extends StartActivityOnClickListener {

    public static class SignOutOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "SignOutOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new SignOutOnClickListener(type, bundle);
        }
    }

    /** @noinspection deprecation*/
    public SignOutOnClickListener(String type, Bundle bundle) {
        super(type, bundle);
    }

    @Override
    public boolean onClick(int x, int y) {
        MainApp.getGame().setUser(null);
        SimpleViewGameMenuManager.getSingleton().recreateMainMenuMultiplayerOnClickListener();
        return super.onClick(x, y);
    }
}
