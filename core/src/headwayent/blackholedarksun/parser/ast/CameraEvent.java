/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.HashMap;
import java.util.Map;

import headwayent.blackholedarksun.parser.dispatchers.AbstractEventDispatcher;

public class CameraEvent extends DelayedEvent {

    public static final String TYPE = "CameraEvent";
    private final HashMap<String, Param> map;
    private ShowText showText;
    private PlaySound playSound;
    private Position position;
    private Orientation orientation;
    private CameraLookAt lookAt;

    public CameraEvent(HashMap<String,Param> map) {
        super(TYPE);
        this.map = map;
    }

    @Override
    public void init() {
        super.init();
        for (Map.Entry<String, Param> entry : map.entrySet()) {
            String s = entry.getKey();
            Param param = entry.getValue();
            if (s.equalsIgnoreCase(ShowText.TYPE)) {
                showText = (ShowText) param;
                param.init();
            } else if (s.equalsIgnoreCase(PlaySound.TYPE)) {
                playSound = (PlaySound) param;
                param.init();
            } else if (s.equalsIgnoreCase(Position.TYPE)) {
                position = (Position) param;
                param.init();
            } else if (s.equalsIgnoreCase(CameraLookAt.TYPE)) {
                lookAt = (CameraLookAt) param;
                param.init();
            } else if (s.equalsIgnoreCase(DelayStart.TYPE)) {
                delayStart = (DelayStart) param;
                param.init();
            } else if (s.equalsIgnoreCase(DelayEnd.TYPE)) {
                delayEnd = (DelayEnd) param;
                param.init();
            } else if (s.equalsIgnoreCase(Orientation.TYPE)) {
                orientation = (Orientation) param;
                param.init();
            }
        }

    }

    @Override
    public boolean accept(AbstractEventDispatcher dispatcher) {
        return dispatcher.dispatch(this);
    }

    public HashMap<String, Param> getMap() {
        return map;
    }

    public ShowText getShowText() {
        return showText;
    }

    public PlaySound getPlaySound() {
        return playSound;
    }

    public Position getPosition() {
        return position;
    }

    public CameraLookAt getLookAt() {
        return lookAt;
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
