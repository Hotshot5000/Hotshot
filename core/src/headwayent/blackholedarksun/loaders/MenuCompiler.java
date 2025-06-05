/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/20/21, 3:58 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menuresource.Menu;
import headwayent.blackholedarksun.menuresource.MenuSelection;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.DataInputStream;
import java.util.Iterator;

public class MenuCompiler extends ENG_AbstractCompiler<Menu> {

    private static final String MENU = "menu";
    private static final String MENU_TITLE = "title";
    private static final String MENU_SELECTION = "selection";
    private static final String CONTAINER_LISTENER = "container_listener";
    private static final String CONTAINER_TYPE = "container_type";
    private static final String PREVIOUS_MENU = "previous_menu";

    private static MenuSelection parseMenuSelection(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
    /*	if (!s.equalsIgnoreCase(MENU_SELECTION)) {
			throw new ENG_InvalidFormatParsingException("selection must be the first" +
					" determining the current name and next menu");
		}*/
        MenuSelection menuSelection = new MenuSelection();
        if (MainApp.Platform.isMobile()) {
            if (s.equalsIgnoreCase("Exit")) {
                return menuSelection;
            }
        }
        if (!MainApp.Features.MULTIPLAYER.isFeatureEnabled(MainApp.FEATURES_ENABLED)) {
            if (s.equalsIgnoreCase("Multiplayer")) {
                return menuSelection;
            }
        }
        menuSelection.name = s.replace("_", " ");
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(PREVIOUS_MENU)) {
            menuSelection.goToPreviousMenu = true;
        } else {
            menuSelection.nextMenu = s;
        }
        return menuSelection;
    }

    private static String parseContainerListener(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        return s;
    }

    private static String parseContainerType(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        return s;
    }

    public Menu compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            Menu menu = new Menu();
            if (s.equalsIgnoreCase(MENU)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                menu.name = s;
            } else {
                throw new ENG_InvalidFormatParsingException("menu must be first word" +
                        " in file");
            }
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            incrementBracketLevel(s, "{ mandatory after menu name");
            boolean containerTypeSet = false;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(MENU_TITLE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    menu.menuTitle = s.replace("_", " ");
                } else if (s.equalsIgnoreCase(MENU_SELECTION)) {
                    menu.selectionList.add(parseMenuSelection(fp0));
                } else if (s.equalsIgnoreCase(CONTAINER_LISTENER)) {
                    menu.containerListenerList.add(parseContainerListener(fp0));
                } else if (s.equalsIgnoreCase(CONTAINER_TYPE)) {
                    if (containerTypeSet) {
                        throw new ENG_InvalidFormatParsingException(
                                "Only one container type allowed");
                    }
                    menu.containerType = parseContainerType(fp0);
                    containerTypeSet = true;
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            // Hack to get rid of the Exit menu issue when on mobile devices.
            for (Iterator<MenuSelection> iterator = menu.selectionList.iterator(); iterator.hasNext(); ) {
                MenuSelection menuSelection = iterator.next();
                if (menuSelection.name == null) {
                    iterator.remove();
                }
            }

            return menu;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }
}
