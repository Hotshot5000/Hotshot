/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public class ENG_KeyEvent {

    public enum KeyAction {
        UP, DOWN, DOWNED, NONE // For the null event to end the queue
    }

    public int keyCode;
    public char character;
    public KeyAction keyAction;

    public ENG_KeyEvent() {

    }

    public ENG_KeyEvent(char character/*, KeyAction keyAction*/) {
        this.character = character;
//		this.keyAction = keyAction;
    }

    public ENG_KeyEvent(int keyCode, KeyAction keyAction) {
        this.keyCode = keyCode;
        this.keyAction = keyAction;
    }

    public ENG_KeyEvent(int keyCode, char character, KeyAction keyAction) {
        this.keyCode = keyCode;
        this.character = character;
        this.keyAction = keyAction;
    }


}
