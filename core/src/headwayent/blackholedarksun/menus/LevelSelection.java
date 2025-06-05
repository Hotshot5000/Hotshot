/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.menusystemsimpleview.menulisteners.LevelSelectionOnClickListener;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;

/**
 * Created by Sebi on 17.05.2014.
 */
public class LevelSelection extends GenericMenu {

    public static class LevelSelectionFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new LevelSelection(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation */
    public LevelSelection(String name, Bundle bundle) {
        super(name, bundle);

        float bottomsButtonsBeginningPos = getBottomsButtonsBeginningPos();
        float bottomButtonsHeight = getBottomButtonsHeight();

        for (int i = 0; i < APP_Game.levelTitleList.length; ++i) {
            Bundle otherBundle = new Bundle();
            otherBundle.putInt("finalI", i);
            setOnClickListener(APP_Game.levelTitleList[i],
                    SimpleViewGameMenuManager.getSingleton().createOnClickListenerWithType(
                            LevelSelectionOnClickListener.LevelSelectionOnClickListenerFactory.TYPE, otherBundle)
            );
        }
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }
}
