/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menuresource.Menu;

import java.util.HashMap;

public class SimpleViewMenuManager {

//    private static SimpleViewMenuManager mgr;

    private final HashMap<String, Menu> menuList = new HashMap<>();

    public SimpleViewMenuManager() {
        
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public void addMenu(Menu menu) {
        Menu put = menuList.put(menu.name, menu);
        if (put != null) {
            throw new IllegalArgumentException(menu.name + " is already in the menu list");
        }
    }

    public void removeMenu(String name) {
        Menu remove = menuList.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid menu name");
        }
    }

    public void removeAllMenus() {
        menuList.clear();
    }

    public Menu getMenu(String name) {
        Menu menu = menuList.get(name);
        if (menu == null) {
            throw new IllegalArgumentException(name + " is not a valid menu name");
        }
        return menu;
    }

    public static SimpleViewMenuManager getSingleton() {
//        if (mgr == null && MainActivity.isDebugmode()) {
//            throw new NullPointerException("SimpleView " +
//                    "Menu manager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getSimpleViewMenuManager();
    }

}
