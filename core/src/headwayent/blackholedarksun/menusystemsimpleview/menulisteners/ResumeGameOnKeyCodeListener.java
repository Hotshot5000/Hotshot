/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.menulisteners;

import com.badlogic.gdx.Input;

import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnKeyCodeListenerWithType;
import headwayent.blackholedarksun.menusystemsimpleview.viewlisteners.OnKeyCodeListenerWithTypeFactory;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.input.ENG_InputConvertor;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

/**
 * Created by Sebi on 24.05.2014.
 */
public class ResumeGameOnKeyCodeListener extends OnKeyCodeListenerWithType {

    public static final String KEY = "key";
    private long escKeyDelay;
    private int key = Input.Keys.ESCAPE;

    public ResumeGameOnKeyCodeListener(String type, Bundle bundle) {
        super(type, bundle);
        if (bundle != null) {
            int keyToSet = bundle.getInt(KEY, -1);
            if (keyToSet != -1) {
                key = keyToSet;
            }
        }
    }

    public static class ResumeGameOnKeyCodeListenerFactory extends OnKeyCodeListenerWithTypeFactory {

        public static final String TYPE = "ResumeGameOnKeyCodeListener";

        /** @noinspection deprecation*/
        @Override
        public OnKeyCodeListenerWithType createOnKeyCodeListener(String type, Bundle bundle) {
            return new ResumeGameOnKeyCodeListener(type, bundle);
        }
    }

    @Override
    public boolean onKeyCode(int keyCode, ENG_InputConvertor.KeyEventType type) {

        // Horrible hack
        // If key escape while with MouseAndKeyboardInput then it will
        // also resume game when changing inputs to TouchInput so we need to force a
        // small delay.
        if (escKeyDelay == 0) {
            escKeyDelay = currentTimeMillis();
            return true;
        }
        if (keyCode == key && type == ENG_InputConvertor.KeyEventType.DOWN
                && ENG_Utility.hasTimePassed(FrameInterval.RESUME_GAME_KEY_CODE, escKeyDelay, 150)) {
            SimpleViewGameMenuManager.resumeGame();
        }
        return true;
    }
}
