/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.ShipSelection;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.LevelSelection;
import headwayent.blackholedarksun.menus.MissionBriefing;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;

public class SinglePlayerMenuAutomation extends AutomationFramework {

    private static final String SHIP_NAME = "Arcturus";
    private static final int LEVEL_NUM = 0;
    public static final String NAME = "AutoInit";
    public static final String PARAM_ACTIVITY_BUTTON = "button";
    public static final String PARAM_ACTIVITY_FINISHED = "finished";

    public SinglePlayerMenuAutomation() {
        super(NAME);
        
    }

    private enum State {
        MAIN_MENU, LEVEL_SELECTION, MISSION_BRIEFING, SHIP_SELECTION, IN_GAME
    }

    private State state = State.MAIN_MENU;

    @Override
    public void execute() {


        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        switch (state) {
            case MAIN_MENU: {
                GenericMenu mainMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MAIN_MENU);
                ENG_Button newGameButton = mainMenu.getButton(SimpleViewGameMenuManager.NEW_GAME_BUTTON);
                newGameButton.handleOnClickListener(0, 0);
                state = State.LEVEL_SELECTION;
            }
            break;
            case LEVEL_SELECTION: {
                LevelSelection levelSelectionMenu = (LevelSelection) containerManager.getContainer(SimpleViewGameMenuManager.LEVEL_SELECTION);
                ENG_Button selectedLevel = levelSelectionMenu.getButton(APP_Game.levelTitleList[1]);
                selectedLevel.handleOnClickListener(0, 0);
                state = State.MISSION_BRIEFING;
            }
            break;
            case MISSION_BRIEFING: {
                MissionBriefing missionBriefing = (MissionBriefing) containerManager.getContainer(SimpleViewGameMenuManager.MISSION_BRIEFING_LEVEL + "0");
                ENG_Button doneButton = (ENG_Button) missionBriefing._getView(MissionBriefing.DONE_BUTTON);
                doneButton.handleOnClickListener(0, 0);
                state = State.SHIP_SELECTION;
            }
            break;
            case SHIP_SELECTION: {
                ShipSelection shipSelection = (ShipSelection) containerManager.getContainer(SimpleViewGameMenuManager.SHIP_SELECTION);
                ENG_Button selectShip = (ENG_Button) shipSelection._getView(ShipSelection.SELECT_SHIP);
                ENG_Button nextShip = (ENG_Button) shipSelection._getView(ShipSelection.NEXT_SHIP);
                ENG_Button previousShip = (ENG_Button) shipSelection._getView(ShipSelection.PREVIOUS_SHIP);
                // Run the listeners at least once because we might not run the gameLoop if things happen fast enough
                shipSelection.update();
                selectShip.handleOnClickListener(0, 0);
                state = State.IN_GAME;
            }
            break;
            case IN_GAME:
                MainApp.getMainThread().runOnMainThread(() -> MainApp.getMainThread().removeAutomation(NAME));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void notifyParameterSet(String name) {

    }

}
