/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.MultiplayerCreateSession;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerCreateSessionContainerListener;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_DropdownList;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;

/**
 * Created by sebas on 03.12.2015.
 */
public class MultiPlayerCreateServerMenuAutomation extends AutomationFramework {

    public static final String NAME = "multiplayer_create_server";

    public static final String PARAM_SERVER_CREATED = "server_created";

    private enum State {
        MAIN_MENU, MULTIPLAYER_MENU, SERVER_CREATION_MENU, WAIT, SHIP_SELECTION, IN_GAME
    }

    private State state = State.MAIN_MENU;

    public MultiPlayerCreateServerMenuAutomation() {
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
                ENG_Button createSessionButton = multiplayerMenu.getButton(SimpleViewGameMenuManager.CREATE_SESSION_BUTTON);
                createSessionButton.handleOnClickListener(0, 0);
                state = State.SERVER_CREATION_MENU;
            }
            break;
            case SERVER_CREATION_MENU: {
                MultiplayerCreateSession multiplayerCreateSessionMenu = (MultiplayerCreateSession) containerManager.getContainer(
                        SimpleViewGameMenuManager.MULTIPLAYER_CREATE_SESSION);
                ENG_TextField sessionName = (ENG_TextField) multiplayerCreateSessionMenu._getView(MultiplayerCreateSession.SESSION_NAME_TEXT_FIELD);
                ENG_DropdownList mapSelection = (ENG_DropdownList) multiplayerCreateSessionMenu._getView(MultiplayerCreateSession.MAP_SELECTION_DROPDOWN_LIST);
                ENG_DropdownList playerNumSelection = (ENG_DropdownList) multiplayerCreateSessionMenu.
                        _getView(MultiplayerCreateSession.PLAYER_NUM_SELECTION_DROPDOWN_LIST);
                ENG_DropdownList teamSelection = (ENG_DropdownList) multiplayerCreateSessionMenu._getView(MultiplayerCreateSession.TEAM_SELECTION_DROPDOWN_LIST);
                ENG_Button createSessionButton = (ENG_Button) multiplayerCreateSessionMenu._getView(MultiplayerCreateSession.CREATE_SESSION_BUTTON);
                ENG_Button cancelButton = (ENG_Button) multiplayerCreateSessionMenu._getView(MultiplayerCreateSession.CANCEL_BUTTON);

                sessionName.setText("sessionNew");
                mapSelection.setCurrentElement(0);
                playerNumSelection.setCurrentElement(2);
                teamSelection.setCurrentElement(0);
                ArrayList<ENG_Container.ContainerListener> listeners = multiplayerCreateSessionMenu.getListeners();
                for (ENG_Container.ContainerListener cl : listeners) {
                    if (cl instanceof MultiplayerCreateSessionContainerListener) {
                        cl.onActivation();
                    }
                }


                createSessionButton.handleOnClickListener(0, 0);
                // Now we must wait for the server to respond. We can't just force moving to the ship selection.
                state = State.WAIT;
            }
            break;
            case WAIT:
                break;
            case SHIP_SELECTION: {
                AutomationFrameworkUtils.selectShip();
                state = State.IN_GAME;
            }
            break;
            case IN_GAME: {
                MainApp.getMainThread().runOnMainThread(() -> MainApp.getMainThread().removeAutomation(NAME));
            }
            break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void notifyParameterSet(String name) {
        if (name.equals(PARAM_SERVER_CREATED)) {
            // We can move on to the ship selection.
            state = State.SHIP_SELECTION;
        }
    }
}
