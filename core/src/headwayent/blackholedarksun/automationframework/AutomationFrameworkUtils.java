/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.MultiplayerLogin;
import headwayent.blackholedarksun.menus.ShipSelection;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;

public class AutomationFrameworkUtils {

    public static void selectShip() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        ShipSelection shipSelection = (ShipSelection) containerManager.getContainer(SimpleViewGameMenuManager.SHIP_SELECTION);
        ENG_Button selectShip = (ENG_Button) shipSelection._getView(ShipSelection.SELECT_SHIP);
        ENG_Button nextShip = (ENG_Button) shipSelection._getView(ShipSelection.NEXT_SHIP);
        ENG_Button previousShip = (ENG_Button) shipSelection._getView(ShipSelection.PREVIOUS_SHIP);
        // Run the listeners at least once because we might not run the gameLoop if things happen fast enough
        shipSelection.update();
        selectShip.handleOnClickListener(0, 0);
    }

    public static void goToMultiplayerMenu() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        GenericMenu mainMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MAIN_MENU);
        ENG_Button multiplayerButton = mainMenu.getButton(SimpleViewGameMenuManager.MULTIPLAYER_BUTTON);
        multiplayerButton.handleOnClickListener(0, 0);
    }

    public static void signOut() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        GenericMenu multiplayerMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU);
        ENG_Button signOutButton = multiplayerMenu.getButton(SimpleViewGameMenuManager.SIGN_OUT_BUTTON);
        signOutButton.handleOnClickListener(0, 0);
    }

    public static void goToLoginMenu() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        GenericMenu multiplayerMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGIN_MENU);
        ENG_Button loginButton = multiplayerMenu.getButton(SimpleViewGameMenuManager.LOGIN_BUTTON);
        loginButton.handleOnClickListener(0, 0);
    }

    public static void login(String username, String password) {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        MultiplayerLogin loginMenu = (MultiplayerLogin) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGIN);
        ENG_TextField usernameField = (ENG_TextField) loginMenu._getView(MultiplayerLogin.USERNAME);
        ENG_TextField passwordField = (ENG_TextField) loginMenu._getView(MultiplayerLogin.PASSWORD);
        ENG_Button loginButton = (ENG_Button) loginMenu._getView(MultiplayerLogin.LOGIN_BUTTON);
        usernameField.setText(username);
        passwordField._setPasswordText(password);
        loginButton.handleOnClickListener(0, 0);
    }
}
