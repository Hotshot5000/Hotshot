/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/13/21, 3:25 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menuresource;

import java.util.ArrayList;

public class Menu {

    public String name;
    public String menuTitle;
    public final ArrayList<MenuSelection> selectionList = new ArrayList<>();
    public final ArrayList<String> containerListenerList = new ArrayList<>();
    public String containerType;

    public ArrayList<MenuSelection> getValidSelections() {
        ArrayList<MenuSelection> menuSelections = new ArrayList<>();
        for (MenuSelection s : selectionList) {
            if (!s.goToPreviousMenu && s.name != null) {
                menuSelections.add(s);
            }
        }
        return menuSelections;
    }

    public ArrayList<MenuSelection> getGoToPreviousMenuSelections() {
        ArrayList<MenuSelection> menuSelections = new ArrayList<>();
        for (MenuSelection s : selectionList) {
            if (s.goToPreviousMenu) {
                menuSelections.add(s);
            }
        }
        return menuSelections;
    }
}
