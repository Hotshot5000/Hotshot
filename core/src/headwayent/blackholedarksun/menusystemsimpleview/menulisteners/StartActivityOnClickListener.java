/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
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
public class StartActivityOnClickListener extends OnClickListenerWithType {

    private final String activity;
    private final boolean disableDemoShow;
    private final boolean reenableDemo;
    private final boolean savePreviousMenu;

    /** @noinspection deprecation*/
    public StartActivityOnClickListener(String type, Bundle bundle) {
        super(type, bundle);

        activity = bundle.getString("activity");
        if (activity == null) {
            throw new IllegalArgumentException("activity not found");
        }
        disableDemoShow = bundle.getBoolean("disableDemoShow", false);
        reenableDemo = bundle.getBoolean("reenableDemo", false);
        savePreviousMenu = bundle.getBoolean("savePreviousMenu", true);
    }

    public static class StartActivityOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "StartActivityOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new StartActivityOnClickListener(type, bundle);
        }
    }

    @Override
    public boolean onClick(int x, int y) {
        SimpleViewGameMenuManager.startActivity(activity, disableDemoShow, reenableDemo, savePreviousMenu);
        return true;
    }
}
