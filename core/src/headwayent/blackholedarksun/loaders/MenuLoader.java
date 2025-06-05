/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.menuresource.Menu;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewMenuManager;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.util.ArrayList;

public class MenuLoader {

    public static void loadMenuList(String fileName, String path) {
        ArrayList<String> list = ENG_CompilerUtil.loadListFromFile(fileName, path);

        for (String menu : list) {
            String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(menu);
            loadMenu(pathAndFileName[1], pathAndFileName[0]);
        }
    }

    /** @noinspection deprecation */
    public static void loadMenu(String fileName, String path) {
        Menu menu = new MenuCompiler().compile(fileName, path, true);
//		MenuManager.getSingleton().createMenuOverlay(menu);
        if (menu.containerType == null) {
            menu.containerType = "GenericMenu";
        }
        SimpleViewMenuManager.getSingleton().addMenu(menu);
        Bundle bundle = new Bundle();
        bundle.putObject(GenericMenu.BUNDLE_MENU, menu);

        ENG_ContainerManager.ContainerListenerObject[] objs =
                new ENG_ContainerManager.ContainerListenerObject[menu.containerListenerList.size()];
        int i = 0;
        for (String s : menu.containerListenerList) {
            objs[i++] = new ENG_ContainerManager.ContainerListenerObject(s);
        }

        ENG_ContainerManager.getSingleton().createContainer(
                menu.name, menu.containerType, bundle, true, objs);
    }
}
