/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
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
public class LevelSelectionOnClickListener extends OnClickListenerWithType {

    public static class LevelSelectionOnClickListenerFactory extends OnClickListenerWithTypeFactory {

        public static final String TYPE = "LevelSelectionOnClickListener";

        /** @noinspection deprecation*/
        @Override
        public OnClickListenerWithType createOnClickListener(String type, Bundle bundle) {
            return new LevelSelectionOnClickListener(type, bundle);
        }
    }

    private final int finalI;

    /** @noinspection deprecation*/
    public LevelSelectionOnClickListener(String type, Bundle bundle) {
        super(type, bundle);
        finalI = bundle.getInt("finalI", -1);
        if (finalI == -1) {
            throw new IllegalArgumentException("finalI not specified");
        }
    }

    @Override
    public boolean onClick(int x, int y) {
        SimpleViewGameMenuManager.setCurrentMenu("MissionBriefing_level " + finalI);
        return true;
    }
}
