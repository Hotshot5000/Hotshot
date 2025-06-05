/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MissionDebriefing extends ENG_Container {

    public static class MissionDebriefingContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            
            return new MissionDebriefing(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            
            c.destroy();
        }

    }

/*	public MissionDebriefing(String font) {
        super(font);

	}*/

    /** @noinspection deprecation*/
    public MissionDebriefing(String name, Bundle bundle) {

        super(name, bundle);
//		Bundle bundle = getIntent().getBundleExtra(
//				WorldManager.BUNDLE_MISSION_DEBRIEFING);
        String title = bundle.getString("title");
        if (title == null) {
            throw new IllegalArgumentException("title not found");
        }
//		title.setText(t);
        String text = bundle.getString("text");
        if (text == null) {
            throw new IllegalArgumentException("text not found");
        }
//		text.setText(t);
//		text.setMovementMethod(new ScrollingMovementMethod());

        final int level = bundle.getInt("level");

        boolean loss = bundle.getBoolean("loss");

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ENG_TextView textView = (ENG_TextView) createView("text", "textview", 0.0f, 17.0f, 100.0f, 40.0f);
        ENG_Button nextButton = null;
        if (!loss) {
            nextButton = (ENG_Button) createView("next", "button", 0.0f, 42.0f, 100.0f, 56.0f);
        }
        ENG_Button replayButton = (ENG_Button) createView("replay", "button", 0.0f, 62.0f, 100.0f, 76.0f);
        ENG_Button mainMenuButton = (ENG_Button) createView("back", "button", 0.0f, 82.0f, 100.0f, 96.0f);

        titleView.setText(title);
        textView.setText(text, false);
        if (!loss) {
            nextButton.setText(MISSION_DEBRIEFING_NEXT_MISSION);
        }
        replayButton.setText(MISSION_DEBRIEFING_REPLAY_MISSION);
        mainMenuButton.setText(MISSION_DEBRIEFING_GO_TO_MAIN_MENU);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        if (!loss) {
            nextButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        }
        replayButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        mainMenuButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        replayButton.setTextColor(ENG_ColorValue.WHITE);
        replayButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        mainMenuButton.setTextColor(ENG_ColorValue.WHITE);
        mainMenuButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        if (!loss) {
            nextButton.setTextColor(ENG_ColorValue.WHITE);
            nextButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        }

        if (!loss) {
            nextButton.setOnClickListener((x, y) -> {

//					destroy();
//					ENG_ContainerManager.getSingleton().destroyContainer(getName());
                createMissionBriefing(level + 1);
                return true;
            });
        }

        replayButton.setOnClickListener((x, y) -> {

//				destroy();
//				ENG_ContainerManager.getSingleton().destroyContainer(getName());
            createMissionBriefing(level);
            return true;
        });

        mainMenuButton.setOnClickListener((x, y) -> {

//				destroy();
//				ENG_ContainerManager.getSingleton().destroyContainer(getName());
//				ENG_ContainerManager.getSingleton().createContainer(getName(),
//						"MissionBriefing", GameMenuManager.getSingleton()
//						.createLevelBriefingBundle(level));
//				recreateGameMenus();
            goToMainMenu();
            return true;
        });
    }

    private void goToMainMenu() {
        
        // Reset the bundle to avoid reloading the debriefing screen
//		MainApp.getGame().goToMainMenu();
//		finish();
        SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.MAIN_MENU);
    }

    /** @noinspection deprecation */
    private void createMissionBriefing(final int level) {
        if (level >= APP_Game.MAX_LEVEL) {
            Bundle bundle = new Bundle();
            bundle.putString("text", MainApp.DEMO ? MISSION_DEBRIEFING_END_GAME_DEMO : MISSION_DEBRIEFING_END_GAME_FULL);
            bundle.putBoolean("link", MainApp.DEMO);
		/*	MainActivity.getInstance().startActivity(
					new Intent().setClass(MainActivity.getInstance(),
							EndGameActivity.class).putExtra(
							EndGameActivity.BUNDLE_NAME, bundle));*/
//			ENG_ContainerManager.getSingleton().setCurrentContainer(
//					new EndGame(bundle));
            ENG_ContainerManager.getSingleton().setCurrentContainer(ENG_ContainerManager.getSingleton().createContainer("EndGame", "EndGame", bundle));
        } else {

		/*	MainActivity.getInstance().startActivityForResult(
					new Intent().setClass(
							MainActivity.getInstance().getApplicationContext(),
							MissionBriefingActivity.class).putExtra(
							MissionBriefingActivity.BUNDLE,
							GameMenuManager.getSingleton()
									.createLevelBriefingBundle(level)), 0);*/

//			ENG_ContainerManager.getSingleton().setCurrentContainer(
//					new MissionBriefing(GameMenuManager.getSingleton()
//									.createLevelBriefingBundle(level)));
            String briefingName = "MissionBriefing_level " + level;
//			ENG_Container container = ENG_ContainerManager.getSingleton()
//					.getContainer(briefingName);
            // If we don't have a container it means we have a new level limit
            // reached.
//			if (container == null) {
//				recreateGameMenus();
//			}
//			ENG_ContainerManager.getSingleton().setCurrentContainer(
//				ENG_ContainerManager.getSingleton().createContainer(
//						"MissionBriefing_level_from_debriefing " + level,
//						"MissionBriefing",
//						GameMenuManager.getSingleton()
//								.createLevelBriefingBundle(level)));
            SimpleViewGameMenuManager.setCurrentMenu(briefingName);
        }
    }

//	private void recreateGameMenus() {
//		ENG_ContainerManager.getSingleton().destroyAllContainers();
//		MenuManager.getSingleton().destroyAllMenuOverlays();
//		GameMenuManager.getSingleton().initMenus();
//	}

}
