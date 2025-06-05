/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.MultiplayerJoinSession;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;

/**
 * Created by sebas on 27.05.2016.
 */
public class MultiPlayerJoinServerMenuAutomation extends AutomationFramework {

    public static final String NAME = "multiplayer_join_server";

    private enum State {
        MAIN_MENU, MULTIPLAYER_MENU, SERVER_JOIN_MENU, WAIT, SHIP_SELECTION, IN_GAME
    }

    private State state = State.MAIN_MENU;

    public MultiPlayerJoinServerMenuAutomation() {
        super(NAME);
    }

    @Override
    public void execute() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        switch (state) {
            case MAIN_MENU: {
                GenericMenu mainMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MAIN_MENU);
                ENG_Button multiplayerButton = mainMenu.getButton(SimpleViewGameMenuManager.MULTIPLAYER_BUTTON);
                multiplayerButton.handleOnClickListener(0, 0);
                state = State.MULTIPLAYER_MENU;
            }
            break;
            case MULTIPLAYER_MENU: {
                GenericMenu multiplayerMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU);
                ENG_Button createSessionButton = multiplayerMenu.getButton(SimpleViewGameMenuManager.JOIN_SESSION_BUTTON);
                createSessionButton.handleOnClickListener(0, 0);
                state = State.SERVER_JOIN_MENU;
            }
            break;
            case SERVER_JOIN_MENU: {
                MultiplayerJoinSession multiplayerJoinSessionMenu = (MultiplayerJoinSession) containerManager.getContainer(
                        SimpleViewGameMenuManager.MULTIPLAYER_JOIN_SESSION);
            }
            break;
        }
    }

    @Override
    public void notifyParameterSet(String name) {

    }
}
