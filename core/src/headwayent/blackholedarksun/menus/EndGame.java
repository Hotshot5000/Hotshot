/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/13/21, 8:53 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager.ContainerFactory;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;

public class EndGame extends ENG_Container {

    public static class EndGameContainerFactory extends ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            
            return new EndGame(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            
            c.destroy();
        }

    }

/*	public EndGame(String font) {
        super(font);
		
	}*/

    /** @noinspection deprecation*/
    public EndGame(String name, Bundle bundle) {
        
        super(name, bundle);
        String t = bundle.getString("text");
        if (t == null) {
            throw new IllegalArgumentException("text not found");
        }
        boolean demo = bundle.getBoolean("link");

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);
        ENG_TextView textView = (ENG_TextView) createView("text", "textview", 0.0f, 17.0f, 100.0f, 60.0f);
        ENG_Button buyButton = null;
        if (demo) {
            buyButton = (ENG_Button) createView("buy", "button", 0.0f, 62.0f, 100.0f, 76.0f);
        }
        ENG_Button mainMenuButton = (ENG_Button) createView("back", "button", 0.0f, 82.0f, 100.0f, 96.0f);

        titleView.setText(END_GAME_GAME_ENDED);
        textView.setText(t);
        if (demo) {
            buyButton.setText(END_GAME_BUY_FULL_VERSION);
        }
        mainMenuButton.setText(END_GAME_BACK);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        textView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        if (demo) {
            buyButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        }
        mainMenuButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        if (demo) {
            buyButton.setOnClickListener((x, y) -> {

//					destroy();
//					ENG_ContainerManager.getSingleton().destroyContainer(getName());
//					MainActivity.getInstance().runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//
//							Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse("market://details?id=headwayEnt.Blackhole_DarksunFull"));
//                            MainActivity.getInstance().startActivity(intent);
//						}
//					});
                return true;
            });
        }

        mainMenuButton.setOnClickListener((x, y) -> {

//				destroy();
//				ENG_ContainerManager.getSingleton().destroyContainer(getName());
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

}
