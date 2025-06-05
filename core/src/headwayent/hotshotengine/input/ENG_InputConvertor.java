/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/22/18, 9:07 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import headwayent.hotshotengine.ENG_Vector2D;

public abstract class ENG_InputConvertor {

    public enum TouchEventType {
        DOWNED, DOWN, UP, MOVE
    }

    public enum KeyEventType {
        DOWN, UP
    }

    public enum EventType {
        KEY_CODE, CHAR, TOUCH
    }

    public static class Event {
        public final ENG_Vector2D pos = new ENG_Vector2D();
        public TouchEventType touchEventType;
        public KeyEventType keyEventType;
        public EventType eventType;
        public int keyCode;
        public char character;

        public Event() {

        }

        public Event(float x, float y, TouchEventType type) {
            pos.set(x, y);
            this.touchEventType = type;
            eventType = EventType.TOUCH;
        }

        public Event(int keyCode, KeyEventType type) {
            this.keyCode = keyCode;
            this.keyEventType = type;
            eventType = EventType.KEY_CODE;
        }

        public Event(char character) {
            this.character = character;
            eventType = EventType.CHAR;
        }
    }

    public void reset() {

    }

    public ENG_InputConvertor() {
        super();
    }

    public abstract Object read();

}