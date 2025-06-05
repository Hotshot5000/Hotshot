/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/29/21, 10:03 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview;

import com.badlogic.gdx.Input;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.automationframework.SinglePlayerMenuAutomation;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.loaders.MenuLoader;
import headwayent.blackholedarksun.menuresource.Menu;
import headwayent.blackholedarksun.menus.Console;
import headwayent.blackholedarksun.menus.InGameMenu;
import headwayent.blackholedarksun.menus.MultiplayerCreateSession;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.*;
import headwayent.blackholedarksun.menusystemsimpleview.menulisteners.*;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.*;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.MenuStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_DateUtils;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_DropdownList;
import headwayent.hotshotengine.input.ENG_InputManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class SimpleViewGameMenuManager {

    public static final String MAIN_MENU = "main_menu";
    public static final String LEVEL_SELECTION = "level_selection";
    public static final String GAME_MENU = "game_menu";
    public static final String MULTIPLAYER_LOGIN_MENU = "multiplayer_login_menu";
    public static final String MULTIPLAYER_LOGGED_IN_MENU = "multiplayer_logged_in_menu";

    public static final String MULTIPLAYER = "Multiplayer";
    //    public static final String MULTIPLAYER_LOGGED_IN = "MultiplayerLoggedIn";
    public static final String MULTIPLAYER_CREATE_SESSION = "MultiplayerCreateSession";
    public static final String MULTIPLAYER_JOIN_SESSION = "MultiplayerJoinSession";
    public static final String MULTIPLAYER_ADD_FRIEND = "MultiplayerAddFriend";
    public static final String MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS = "MultiplayerCreateSessionWithFriends";
    public static final String MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS = "MultiplayerJoinSessionWithFriends";
    public static final String MULTIPLAYER_LOBBY = "MultiplayerLobby";
    public static final String MULTIPLAYER_CREATE_ACCOUNT = "MultiplayerCreateAccount";
    public static final String MULTIPLAYER_LOGIN = "MultiplayerLogin";
    public static final String OPTIONS = "Options";
    public static final String HELP = "Help";
    public static final String CREDITS = "Credits";
    public static final String CONSOLE = "Console";
    public static final String SHIP_SELECTION = "ShipSelection";
    public static final String SUBTITLES = "Subtitles";
    public static final String NEW_GAME_BUTTON = "New Game";
    public static final String MULTIPLAYER_BUTTON = "Multiplayer";
    public static final String TUTORIAL = "Tutorial";
    public static final String TUTORIAL_BUTTON = "Tutorial";
    public static final String RESUME_GAME_BUTTON = "Resume Game";
    public static final String EXIT_TO_MAIN_MENU_BUTTON = "Exit to Main Menu";
    public static final String OPTIONS_BUTTON = "Options";
    public static final String CREDITS_BUTTON = "Credits";
    public static final String EXIT_BUTTON = "Exit";
    public static final String LOGIN_BUTTON = "Login";
    public static final String CREATE_ACCOUNT_BUTTON = "Create Account";
    public static final String CREATE_SESSION_BUTTON = "Create Session";
    public static final String JOIN_SESSION_BUTTON = "Join Session";
    public static final String CREATE_SESSION_WITH_FRIENDS_BUTTON = "Create Lobby";
    public static final String JOIN_SESSION_WITH_FRIENDS_BUTTON = "Join Lobby";
    public static final String ADD_FRIEND_BUTTON = "Add Friend";
    // TBD if there will be only one lobby or there will be a create lobby menu and then a lobby menu...
    // For now we're going with 2 menus.
    public static final String CREATE_LOBBY = "Create Lobby";
    public static final String SIGN_OUT_BUTTON = "Sign out";
    public static final String MISSION_BRIEFING_LEVEL = "MissionBriefing_level ";
//    private static SimpleViewGameMenuManager mgr = null;
    private static MenuState menuState;

    private final HashMap<String, OnCharacterListenerWithTypeFactory> onCharacterListenerWithTypeFactories = new HashMap<>();
    private final HashMap<String, OnKeyCodeListenerWithTypeFactory> onKeyCodeListenerWithTypeFactories = new HashMap<>();
    private final HashMap<String, OnFocusListenerWithTypeFactory> onFocusListenerWithTypeFactories = new HashMap<>();
    private final HashMap<String, OnClickListenerWithTypeFactory> onClickListenerWithTypeFactories = new HashMap<>();

    public enum MenuState {
        IN_MENU, IN_GAME_MENU, IN_SHIP_SELECTION, IN_GAME_OVERLAY, CONSOLE
    }

//    private GenericMenu mainMenu;

    public SimpleViewGameMenuManager() {
        
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }

        addOnKeyCodeListenerFactory(
                ResumeGameOnKeyCodeListener.ResumeGameOnKeyCodeListenerFactory.TYPE,
                new ResumeGameOnKeyCodeListener.ResumeGameOnKeyCodeListenerFactory());
        addOnClickListenerFactory(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                new StartActivityOnClickListener.StartActivityOnClickListenerFactory());
        addOnClickListenerFactory(ResumeGameOnClickListener.ResumeGameOnClickListenerFactory.TYPE,
                new ResumeGameOnClickListener.ResumeGameOnClickListenerFactory());
        addOnClickListenerFactory(ExitGameOnClickListener.ExitGameOnClickListenerFactory.TYPE,
                new ExitGameOnClickListener.ExitGameOnClickListenerFactory());
        addOnClickListenerFactory(
                LevelSelectionOnClickListener.LevelSelectionOnClickListenerFactory.TYPE,
                new LevelSelectionOnClickListener.LevelSelectionOnClickListenerFactory());
        addOnClickListenerFactory(
                ExitToMainMenuOnClickListener.ExitToMainMenuOnClickListenerFactory.TYPE,
                new ExitToMainMenuOnClickListener.ExitToMainMenuOnClickListenerFactory());
        addOnClickListenerFactory(
                SignOutOnClickListener.SignOutOnClickListenerFactory.TYPE,
                new SignOutOnClickListener.SignOutOnClickListenerFactory()
        );
    }

    public OnClickListenerWithType createOnClickListenerWithType(String type) {
        return createOnClickListenerWithType(type, null);
    }

    public OnClickListenerWithType createOnClickListenerWithType(String type, Bundle bundle) {
        OnClickListenerWithTypeFactory factory = onClickListenerWithTypeFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException(type + " is not a valid factory");
        }
        return factory.createOnClickListener(type, bundle);
    }

    public OnCharacterListenerWithType createOnCharacterListenerWithType(String type) {
        return createOnCharacterListenerWithType(type, null);
    }

    /** @noinspection deprecation*/
    public OnCharacterListenerWithType createOnCharacterListenerWithType(String type, Bundle bundle) {
        OnCharacterListenerWithTypeFactory factory = onCharacterListenerWithTypeFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException(type + " is not a valid factory");
        }
        return factory.createOnCharacterListener(type, bundle);
    }

    public OnKeyCodeListenerWithType createOnKeyCodeListenerWithType(String type) {
        return createOnKeyCodeListenerWithType(type, null);
    }

    /** @noinspection deprecation*/
    public OnKeyCodeListenerWithType createOnKeyCodeListenerWithType(String type, Bundle bundle) {
        OnKeyCodeListenerWithTypeFactory factory = onKeyCodeListenerWithTypeFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException(type + " is not a valid factory");
        }
        return factory.createOnKeyCodeListener(type, bundle);
    }

    public OnFocusListenerWithType createOnFocusListenerWithType(String type) {
        return createOnFocusListenerWithType(type, null);
    }

    public OnFocusListenerWithType createOnFocusListenerWithType(String type, Bundle bundle) {
        OnFocusListenerWithTypeFactory factory = onFocusListenerWithTypeFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException(type + " is not a valid factory");
        }
        return factory.createOnFocusListener(type, bundle);
    }

    public void addOnCharacterListenerFactory(String type, OnCharacterListenerWithTypeFactory f) {
        OnCharacterListenerWithTypeFactory put = onCharacterListenerWithTypeFactories.put(type, f);
        if (put != null) {
            throw new IllegalArgumentException(type + " already exists as a factory");
        }
    }

    public void removeOnCharacterListenerFactory(String type) {
        OnCharacterListenerWithTypeFactory remove = onCharacterListenerWithTypeFactories.remove(type);
        if (remove == null) {
            throw new IllegalArgumentException(type + " does not exist as a factory");
        }
    }

    public void clearOnCharacterListenerFactories() {
        onCharacterListenerWithTypeFactories.clear();
    }

    public void addOnKeyCodeListenerFactory(String type, OnKeyCodeListenerWithTypeFactory f) {
        OnKeyCodeListenerWithTypeFactory put = onKeyCodeListenerWithTypeFactories.put(type, f);
        if (put != null) {
            throw new IllegalArgumentException(type + " already exists as a factory");
        }
    }

    public void removeOnKeyCodeListenerFactory(String type) {
        OnKeyCodeListenerWithTypeFactory remove = onKeyCodeListenerWithTypeFactories.remove(type);
        if (remove == null) {
            throw new IllegalArgumentException(type + " does not exist as a factory");
        }
    }

    public void clearOnKeyCodeListenerFactories() {
        onKeyCodeListenerWithTypeFactories.clear();
    }

    public void addOnFocusListenerFactory(String type, OnFocusListenerWithTypeFactory f) {
        OnFocusListenerWithTypeFactory put = onFocusListenerWithTypeFactories.put(type, f);
        if (put != null) {
            throw new IllegalArgumentException(type + " already exists as a factory");
        }
    }

    public void removeOnFocusListenerFactory(String type) {
        OnFocusListenerWithTypeFactory remove = onFocusListenerWithTypeFactories.remove(type);
        if (remove == null) {
            throw new IllegalArgumentException(type + " does not exist as a factory");
        }
    }

    public void clearOnFocusListenerFactories() {
        onFocusListenerWithTypeFactories.clear();
    }

    public void addOnClickListenerFactory(String type, OnClickListenerWithTypeFactory f) {
        OnClickListenerWithTypeFactory put = onClickListenerWithTypeFactories.put(type, f);
        if (put != null) {
            throw new IllegalArgumentException(type + " already exists as a factory");
        }
    }

    public void removeOnClickListenerFactory(String type) {
        OnClickListenerWithTypeFactory remove = onClickListenerWithTypeFactories.remove(type);
        if (remove == null) {
            throw new IllegalArgumentException(type + " does not exist as a factory");
        }
    }

    public void clearOnClickListenerFactories() {
        onClickListenerWithTypeFactories.clear();
    }

    /** @noinspection deprecation */
    public void initMenus() {
        MenuLoader.loadMenuList("menu_list.txt", MainApp.getGame().getGameResourcesDir());
        final ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        GenericMenu mainMenu = (GenericMenu) containerManager.getContainer(MAIN_MENU);
        GenericMenu levelSelection = (GenericMenu) containerManager.getContainer(LEVEL_SELECTION);
        InGameMenu gameMenu = (InGameMenu) containerManager.getContainer(GAME_MENU);
        GenericMenu multiplayerLoginMenu = (GenericMenu) containerManager.getContainer(MULTIPLAYER_LOGIN_MENU);
        GenericMenu multiplayerLoggedInMenu = (GenericMenu) containerManager.getContainer(MULTIPLAYER_LOGGED_IN_MENU);
        SimpleViewMenuManager menuManager = SimpleViewMenuManager.getSingleton();
        Menu mainMenuMenu = menuManager.getMenu(MAIN_MENU);
        Menu levelSelectionMenu = menuManager.getMenu(LEVEL_SELECTION);
        Menu gameMenuMenu = menuManager.getMenu(GAME_MENU);


        containerManager.createContainer(MULTIPLAYER_CREATE_ACCOUNT,
                "MultiplayerCreateAccountMenu", null, true,
                new ENG_ContainerManager.ContainerListenerObject(
                        MultiplayerCreateAccountContainerListener.MultiplayerCreateAccountMenuContainerListenerFactory.TYPE));
        containerManager.createContainer(MULTIPLAYER_LOGIN,
                "MultiplayerLoginMenu", null, true,
                new ENG_ContainerManager.ContainerListenerObject(
                        MultiplayerLoginContainerListener.MultiplayerLoginContainerListenerFactory.TYPE
                ));
        Bundle bundle = null;
        if (APP_Game.CLASSIC_MULTIPLAYER_MENUS) {
            bundle = createMultiplayerCreateSessionMapBundle();
            containerManager.createContainer(MULTIPLAYER_CREATE_SESSION,
                    "MultiplayerCreateSessionMenu", bundle, true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            MultiplayerCreateSessionContainerListener.MultiplayerCreateSessionContainerListenerFactory.TYPE
                    ));
            bundle = createMultiplayerJoinSessionMapBundle();
            containerManager.createContainer(MULTIPLAYER_JOIN_SESSION,
                    "MultiplayerJoinSessionMenu", bundle, true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            MultiplayerJoinSessionContainerListener.MultiplayerJoinSessionContainerListenerFactory.TYPE
                    ));
        } else {
            containerManager.createContainer(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS,
                    "MultiplayerCreateSessionWithFriendsMenu", null, true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            MultiplayerCreateSessionWithFriendsContainerListener.MultiplayerCreateSessionWithFriendsContainerListenerFactory.TYPE
                    ));
            containerManager.createContainer(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS,
                    "MultiplayerJoinSessionWithFriendsMenu", null, true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            MultiplayerJoinSessionWithFriendsContainerListener.MultiplayerJoinSessionWithFriendsContainerListenerFactory.TYPE
                    ));
            containerManager.createContainer(MULTIPLAYER_ADD_FRIEND,
                    "MultiplayerAddFriendMenu", null, true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            MultiplayerAddFriendContainerListener.MultiplayerAddFriendContainerListenerFactory.TYPE
                    ));
            // We are putting the container listener just as we are setting the container
            // to be in front.
            containerManager.createContainer(MULTIPLAYER_LOBBY,
                    "MultiplayerLobbyMenu", null, true,
                    null);
        }
//        containerManager.createContainer(MULTIPLAYER,
//                "MultiplayerMenu", null, true,
//                new ENG_ContainerManager.ContainerListenerObject(
//                        MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory.TYPE));
        containerManager.createContainer(OPTIONS, "OptionsMenu", null, true);
        containerManager.createContainer(HELP, "Help", null, true);
        containerManager.createContainer(CREDITS, "Credits", null, true);
        Console console = (Console) containerManager.createContainer(CONSOLE, "Console", null, true);
        containerManager.createContainer(SHIP_SELECTION, "ShipSelection", null, true,
                new ENG_ContainerManager.ContainerListenerObject(
                        ShowDemoContainerListener.ShowDemoContainerListenerFactory.TYPE));
        containerManager.createContainer(SUBTITLES, "Subtitles", null, true);

        for (int i = 0; i < APP_Game.levelTitleList.length; ++i) {
            containerManager.createContainer(MISSION_BRIEFING_LEVEL + i,
                    "MissionBriefing", createLevelBriefingBundle(i), true,
                    new ENG_ContainerManager.ContainerListenerObject(
                            HideDemoContainerListener.HideDemoContainerListenerFactory.TYPE));
        }

        containerManager.createContainerListener(gameMenu,
                InGameMenuContainerListener.InGameMenuContainerListenerFactory.TYPE, null);

        containerManager.createContainerListener(console,
                ConsoleContainerListener.ConsoleContainerListenerFactory.TYPE, null);

        gameMenu.setOnClickListener(RESUME_GAME_BUTTON, createOnClickListenerWithType(
                ResumeGameOnClickListener.ResumeGameOnClickListenerFactory.TYPE));

        gameMenu.setOnKeyCodeListener(RESUME_GAME_BUTTON, createOnKeyCodeListenerWithType(
                ResumeGameOnKeyCodeListener.ResumeGameOnKeyCodeListenerFactory.TYPE));

        bundle = new Bundle();
        bundle.putInt(ResumeGameOnKeyCodeListener.KEY, Input.Keys.GRAVE);
        console.setOnKeyCodeListener(createOnKeyCodeListenerWithType(
                ResumeGameOnKeyCodeListener.ResumeGameOnKeyCodeListenerFactory.TYPE, bundle));

        bundle = new Bundle();
        bundle.putString("activity", OPTIONS);
        gameMenu.setOnClickListener(OPTIONS_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE, bundle));

        bundle = new Bundle();
        bundle.putString("activity", HELP);
        gameMenu.setOnClickListener(TUTORIAL_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE, bundle));

        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            gameMenu.setOnClickListener(EXIT_BUTTON, createOnClickListenerWithType(
                    ExitGameOnClickListener.ExitGameOnClickListenerFactory.TYPE));
        }

        gameMenu.setOnClickListener(EXIT_TO_MAIN_MENU_BUTTON, createOnClickListenerWithType(
                ExitToMainMenuOnClickListener.ExitToMainMenuOnClickListenerFactory.TYPE));

        bundle = new Bundle();
        bundle.putString("activity", LEVEL_SELECTION);
        bundle.putBoolean("disableDemoShow", false);
        bundle.putBoolean("reenableDemo", true);
        mainMenu.setOnClickListener(NEW_GAME_BUTTON,
                createOnClickListenerWithType(StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE, bundle));

        if (MainApp.Features.MULTIPLAYER.isFeatureEnabled(MainApp.FEATURES_ENABLED)) {
            recreateMainMenuMultiplayerOnClickListener();
        }


        bundle = new Bundle();
        bundle.putString("activity", HELP);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        mainMenu.setOnClickListener(TUTORIAL_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));

        bundle = new Bundle();
        bundle.putString("activity", OPTIONS);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        mainMenu.setOnClickListener(OPTIONS_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));

        bundle = new Bundle();
        bundle.putString("activity", CREDITS);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        mainMenu.setOnClickListener(CREDITS_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));


        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            mainMenu.setOnClickListener(EXIT_BUTTON, createOnClickListenerWithType(
                    ExitGameOnClickListener.ExitGameOnClickListenerFactory.TYPE));
        }

        bundle = new Bundle();
        bundle.putString("activity", MULTIPLAYER_LOGIN);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        multiplayerLoginMenu.setOnClickListener(LOGIN_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));

        bundle = new Bundle();
        bundle.putString("activity", MULTIPLAYER_CREATE_ACCOUNT);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        multiplayerLoginMenu.setOnClickListener(CREATE_ACCOUNT_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));

        if (APP_Game.CLASSIC_MULTIPLAYER_MENUS) {
            bundle = new Bundle();
            bundle.putString("activity", MULTIPLAYER_CREATE_SESSION);
            bundle.putBoolean("disableDemoShow", true);
            bundle.putBoolean("reenableDemo", true);
            multiplayerLoggedInMenu.setOnClickListener(CREATE_SESSION_BUTTON, createOnClickListenerWithType(
                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                    bundle));

            bundle = new Bundle();
            bundle.putString("activity", MULTIPLAYER_JOIN_SESSION);
            bundle.putBoolean("disableDemoShow", true);
            bundle.putBoolean("reenableDemo", true);
            multiplayerLoggedInMenu.setOnClickListener(JOIN_SESSION_BUTTON, createOnClickListenerWithType(
                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                    bundle));
        } else {

            bundle = new Bundle();
            bundle.putString("activity", MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS);
            bundle.putBoolean("disableDemoShow", true);
            bundle.putBoolean("reenableDemo", true);
            multiplayerLoggedInMenu.setOnClickListener(CREATE_SESSION_WITH_FRIENDS_BUTTON, createOnClickListenerWithType(
                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                    bundle));

            bundle = new Bundle();
            bundle.putString("activity", MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS);
            bundle.putBoolean("disableDemoShow", true);
            bundle.putBoolean("reenableDemo", true);
            multiplayerLoggedInMenu.setOnClickListener(JOIN_SESSION_WITH_FRIENDS_BUTTON, createOnClickListenerWithType(
                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                    bundle));

            bundle = new Bundle();
            bundle.putString("activity", MULTIPLAYER_ADD_FRIEND);
            bundle.putBoolean("disableDemoShow", true);
            bundle.putBoolean("reenableDemo", true);
            multiplayerLoggedInMenu.setOnClickListener(ADD_FRIEND_BUTTON, createOnClickListenerWithType(
                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                    bundle));

            // Since the multiplayerLoggedInMenu is a GenericMenu we are forcing a container listener on it.
            containerManager.createContainerListener(multiplayerLoggedInMenu, new ENG_ContainerManager.ContainerListenerObject(
                    MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory.TYPE));

//            bundle = new Bundle();
//            bundle.putString("activity", MULTIPLAYER_LOBBY);
//            bundle.putBoolean("disableDemoShow", true);
//            bundle.putBoolean("reenableDemo", true);
//            multiplayerLoggedInMenu.setOnClickListener(CREATE_LOBBY, createOnClickListenerWithType(
//                    StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
//                    bundle));
        }

        bundle = new Bundle();
        bundle.putString("activity", MULTIPLAYER_LOGIN_MENU);
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        bundle.putBoolean("savePreviousMenu", false);
        multiplayerLoggedInMenu.setOnClickListener(SIGN_OUT_BUTTON, createOnClickListenerWithType(
                SignOutOnClickListener.SignOutOnClickListenerFactory.TYPE,
                bundle));
    }

    /** @noinspection deprecation*/
    private Bundle createMultiplayerJoinSessionMapBundle() {
        Bundle bundle = new Bundle();
        addTeamsToBundle(bundle);
        return bundle;
    }

    /** @noinspection deprecation */
    private Bundle createMultiplayerCreateSessionMapBundle() {
        Bundle bundle = new Bundle();
        Bundle mapListBundle = new Bundle();
        TreeSet<Long> mapIds = MainApp.getGame().getMapIds();
        ArrayList<Long> mapIdsList = new ArrayList<>(mapIds);
//        for (int i = 0; i < APP_Game.levelTitleList.length; ++i) {
//            levelStrings.add(APP_Game.levelTitleList[i]);
//        }
//        ArrayList<String> mapList = new ArrayList<>();
//        for (String s : APP_Game.multiplayerLevelTitleList) {
//            mapList.add(s);
//        }
        mapListBundle.putObject(ENG_DropdownList.TEXT_LIST, mapIdsList);
//        mapListBundle.putObject(ENG_DropdownList.TEXT_LIST, mapList);
        Bundle playerNumBundle = new Bundle();
        ArrayList<String> playerNumStrings = new ArrayList<>();
        for (int i = 2; i <= APP_Game.MULTIPLAYER_MAX_PLAYER_NUM; ++i) {
            playerNumStrings.add(String.valueOf(i));
        }
        playerNumBundle.putObject(ENG_DropdownList.TEXT_LIST, playerNumStrings);
        bundle.putBundle(MultiplayerCreateSession.MAP_LIST, mapListBundle);
        bundle.putBundle(MultiplayerCreateSession.PLAYER_NUM, playerNumBundle);
        addTeamsToBundle(bundle);
        return bundle;
    }

    /** @noinspection deprecation*/
    private void addTeamsToBundle(Bundle bundle) {
        ArrayList<String> teamList = new ArrayList<>();
        teamList.add(headwayent.blackholedarksun.entitydata.ShipData.ShipTeam.HUMAN.toString());
        teamList.add(headwayent.blackholedarksun.entitydata.ShipData.ShipTeam.ALIEN.toString());
        Bundle teamBundle = new Bundle();
        teamBundle.putObject(ENG_DropdownList.TEXT_LIST, teamList);
        bundle.putBundle(MultiplayerCreateSession.TEAM_LIST, teamBundle);
    }

    public void recreateMainMenuMultiplayerOnClickListener() {
        Bundle bundle;// Depending on whether we have a user logged in we either show the login/create account menu or show the logged in menu directly
        User user = MainApp.getGame().getUser();
        bundle = new Bundle();
        if (user != null && user.getAuthToken() != null) {
            bundle.putString("activity", MULTIPLAYER_LOGGED_IN_MENU);
        } else {
            bundle.putString("activity", MULTIPLAYER_LOGIN_MENU);
        }
        bundle.putBoolean("disableDemoShow", true);
        bundle.putBoolean("reenableDemo", true);
        GenericMenu mainMenu = (GenericMenu) ENG_ContainerManager.getSingleton().getContainer(MAIN_MENU);
        mainMenu.setOnClickListener(MULTIPLAYER_BUTTON, createOnClickListenerWithType(
                StartActivityOnClickListener.StartActivityOnClickListenerFactory.TYPE,
                bundle));
    }

    public static void resumeGame() {
        System.out.println("resumeGame()");
        ENG_ContainerManager.getSingleton().removeCurrentContainer();
        if (WorldManager.getSingleton() != null && WorldManager.getSingleton().getLevelState() == WorldManagerBase.LevelState.STARTED) {
            // Set the scroll overlay in the in game listener
            setScrollOverlayToInGameListener();
            ((InGameInputConvertorListener) ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_IN_GAME_LISTENER)).resetMouseMovements();
            HudManager.getSingleton().reset();
            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_GAME_OVERLAY);
        }
    }

    public static void setScrollOverlayToInGameListener() {
        ((InGameInputConvertorListener) ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_IN_GAME_LISTENER))
                .setSpeedScrollContainer(HudManager.getSingleton().getSpeedScrollOverlay());
    }

//    private static void showInGameMenu() {
//        showInGameMenu(true);
//    }

    private static void showInGameMenu(final boolean destroyPrevious, final boolean savePreviousMenu) {
        MainApp.getMainThread().runOnMainThread(() -> {
            MainApp.getGame().setInGamePaused(true);
            HudManager.getSingleton().setVisible(false);
            ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
//                MenuManager.getSingleton().showMenuOverlay("game_menu");
            setCurrentMenu(GAME_MENU, destroyPrevious, savePreviousMenu);

            InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
            if (statistics.currentMenuSection == InGameStatistics.MenuSection.IN_GAME) {
                statistics.timeSpentInGame += ENG_Utility.currentTimeMillis() - statistics.currentMenuTimeBeginTime;
                statistics.currentMenuTimeBeginTime = ENG_Utility.currentTimeMillis();
                statistics.currentMenuSection = InGameStatistics.MenuSection.IN_MENU;
            }
        });

    }

    private static void showConsole() {
        MainApp.getMainThread().runOnMainThread(() -> {
            System.out.println("showConsole()");
            // We need the game to run for some commands to execute.
//            MainApp.getGame().setInGamePaused(true);

            // HUD is on top of console so disable it in order to see the console.
            HudManager.getSingleton().setVisible(false);
            ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
            setCurrentMenu(CONSOLE, true, false);
        });
    }

    public static void reloadCurrentMenuState() {
        updateBasedOnMenuState(menuState);
    }

    public static void updateMenuState(MenuState menuState) {
        if (SimpleViewGameMenuManager.menuState == menuState) {
            return;
        }
        SimpleViewGameMenuManager.menuState = menuState;
        updateBasedOnMenuState(menuState);
    }

    private static void updateBasedOnMenuState(SimpleViewGameMenuManager.MenuState menuState) {
        if (menuState == null) {
            throw new IllegalArgumentException("menuState == null");
        }
        switch (menuState) {
            case IN_MENU:
                showMainMenu();
                break;
            case IN_GAME_MENU:
                showInGameMenu(!MainApp.getGame().isReloadingResources(), !MainApp.getGame().isReloadingResources());
                break;
            case IN_SHIP_SELECTION:
                showShipSelection();
                break;
            case IN_GAME_OVERLAY:
                showInGameOverlay();
                break;
            case CONSOLE:
                showConsole();
                break;
            default:
                throw new IllegalArgumentException("Invalid menu state: " + menuState);
        }
    }

    public static MenuState getMenuState() {
        return menuState;
    }

    public static void setMenuState(MenuState menuState) {
        SimpleViewGameMenuManager.menuState = menuState;
    }

    public static void setCurrentMenu(String menuName) {
        ENG_ContainerManager.getSingleton().setCurrentContainer(menuName);
        saveMenuStatistics(menuName);
    }

    public static void setCurrentMenu(String menuName, boolean destroyPrevious, boolean savePrevious) {
        ENG_ContainerManager.getSingleton().setCurrentContainer(menuName, destroyPrevious, savePrevious);
        saveMenuStatistics(menuName);
    }

    private static void saveMenuStatistics(String menuName) {
        InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
        SessionStatistics sessionStatistics = statistics.getLatestSessionStatistics();
        if (sessionStatistics != null) {
            MenuStatistics latestMenuStatistics = sessionStatistics.getLatestMenuStatistics();
            if (latestMenuStatistics != null) {
                latestMenuStatistics.endTime = ENG_DateUtils.getCurrentDateTimestamp();
                latestMenuStatistics.duration = ENG_Utility.currentTimeMillis() - latestMenuStatistics.currentMenuBeginTime;
            }
            MenuStatistics menuStatistics = new MenuStatistics();
            menuStatistics.name = menuName;
            menuStatistics.beginTime = ENG_DateUtils.getCurrentDateTimestamp();
            menuStatistics.currentMenuBeginTime = ENG_Utility.currentTimeMillis();
            sessionStatistics.menuStatisticsList.add(menuStatistics);
        }
    }

    private static void showMainMenu() {
        setCurrentMenu(SimpleViewGameMenuManager.MAIN_MENU);

        InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
        if (statistics.currentMenuTimeBeginTime == 0) {
            // We just started the game.
            statistics.currentMenuTimeBeginTime = ENG_Utility.currentTimeMillis();
            statistics.currentMenuSection = InGameStatistics.MenuSection.IN_MENU;
        }
        if (statistics.currentMenuSection == InGameStatistics.MenuSection.IN_GAME) {
            statistics.timeSpentInGame += ENG_Utility.currentTimeMillis() - statistics.currentMenuTimeBeginTime;
            statistics.currentMenuTimeBeginTime = ENG_Utility.currentTimeMillis();
            statistics.currentMenuSection = InGameStatistics.MenuSection.IN_MENU;
        }
    }

    /** @noinspection deprecation*/
    private static void showShipSelection() {
        // Do it now don't wait for rendering thread to resume cause then it's too late
        // The reloadResources will already be called and we we see the
        // debriefing screen again.
        // Got rid of this shit.
//        WorldManager.getSingleton().resetCurrentBundle();

        // Remove the current ShipDataInjectorContainerListener if any.
        ArrayList<ENG_Container.ContainerListener> listeners = ENG_ContainerManager.getSingleton().getContainer(SimpleViewGameMenuManager.SHIP_SELECTION).getListeners();
        for (Iterator<ENG_Container.ContainerListener> iterator = listeners.iterator(); iterator.hasNext(); ) {
            ENG_Container.ContainerListener listener = iterator.next();
            if (listener.getType().equals(ShipDataInjectorContainerListener.ShipDataInjectorContainerListenerFactory.TYPE)) {
                iterator.remove();
            }
        }


        int levelNum = WorldManager.getSingleton().getCurrentLevel();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ShipDataInjectorContainerListener.BUNDLE_MULTIPLAYER, false);
        bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_TEAM, headwayent.blackholedarksun.entitydata.ShipData.ShipTeam.HUMAN.getTeamNum());
        bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_LEVEL_NUM, levelNum);
        ENG_ContainerManager.getSingleton().createContainerListener(
                SimpleViewGameMenuManager.SHIP_SELECTION,
                ShipDataInjectorContainerListener.ShipDataInjectorContainerListenerFactory.TYPE,
                bundle);
        setCurrentMenu(SimpleViewGameMenuManager.SHIP_SELECTION);
        if (MainApp.getMainThread().isAutomationEnabled(SinglePlayerMenuAutomation.NAME)) {
            MainApp.getMainThread().setParameterForAutomation(SinglePlayerMenuAutomation.NAME, SinglePlayerMenuAutomation.PARAM_ACTIVITY_FINISHED, true);
        }
    }

    private static void showInGameOverlay() {
        HudManager.getSingleton().setVisible(true);
        ENG_InputManager.getSingleton().setInputStack(APP_Game.IN_GAME_INPUT_STACK);
        setScrollOverlayToInGameListener();
        MainApp.getGame().setInGamePaused(false);

        InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
        if (statistics.currentMenuSection == InGameStatistics.MenuSection.IN_MENU) {
            // We are coming from the menu so add the menu time to total menu time.
            statistics.timeSpentInMenus += ENG_Utility.currentTimeMillis() - statistics.currentMenuTimeBeginTime;
            statistics.currentMenuTimeBeginTime = ENG_Utility.currentTimeMillis();
            statistics.currentMenuSection = InGameStatistics.MenuSection.IN_GAME;
        }
    }

    public static void exitGame() {
        WorldManager.LevelState levelState = null;
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
            levelState = WorldManagerServerSide.getSingleton().getLevelState();
        } else {
            levelState = WorldManager.getSingleton().getLevelState();
        }
        if (levelState == WorldManagerBase.LevelState.STARTED ||
                levelState == WorldManagerBase.LevelState.PAUSED) {
            // Save the current game
        }
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
            MainApp.getMainThread().setShouldStop();
            // Now that we are out of the main thread loop we must make sure we deinitialize
            // the native side before killing the rendering thread.
            // This now happens in the main thread at the shouldStop check.
        } else {
            MainApp.getGame().exitGame();
        }
        //	MainApp.getMainThread().setShouldStop();
        //	GLRenderSurface.getSingleton().onPause();

    }

    private static void destroyActivity(String activity) {
        ENG_ContainerManager.getSingleton().destroyContainer(activity);
    }

    public static void startActivity(String activityClass,
//                                      String previousMenuName,
                                     boolean disableDemoShow,
                                     boolean reenableDemo) {
        startActivity(activityClass, disableDemoShow, reenableDemo, true);
    }

    public static void startActivity(String activityClass,
//                                      String previousMenuName,
                                     boolean disableDemoShow,
                                     boolean reenableDemo,
                                     boolean savePreviousMenu) {
        if (disableDemoShow) {
            removeBackgroundAndDemo();
        }
//        MainApp.getGame().setPreviousMenuName(previousMenuName);
//        MainApp.getGame().reenableDemo(reenableDemo);
//        MenuManager.getSingleton().hideMenuOverlay();
        setCurrentMenu(activityClass, true, savePreviousMenu);
    }

    public static void removeBackgroundAndDemo() {
        WorldManager.getSingleton().setShowDemo(false);
        MainApp.getGame().setMainMenuBackgroundCreated(false);
    }

    public void update() {

    }

    /** @noinspection deprecation*/
    public static Bundle createLevelBriefingBundle(int currentLevel) {
        Bundle bundle = new Bundle();
        bundle.putString("title", APP_Game.levelTitleList[currentLevel]);
        bundle.putString("text", APP_Game.missionBriefingList[currentLevel]);
        bundle.putInt("level", currentLevel);
        return bundle;
    }

    public static SimpleViewGameMenuManager getSingleton() {
//        if (mgr == null && MainActivity.isDebugmode()) {
//            throw new NullPointerException("SimpleViewGameMenuManager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getSimpleViewGameMenuManager();
    }

}
