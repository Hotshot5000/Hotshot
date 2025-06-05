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

public class CameraDetachEvent extends DelayedEvent {

    public static final String TYPE = "CameraDetachEvent";
    private final HashMap<String, Param> map;
    private CameraDetach detach;

    public CameraDetachEvent(HashMap<String,Param> map) {
        super(TYPE);
        this.map = map;
    }

    @Override
    public void init() {
        super.init();
        for (Map.Entry<String, Param> entry : map.entrySet()) {
            String s = entry.getKey();
            Param param = entry.getValue();
            if (s.equalsIgnoreCase(CameraDetach.TYPE)) {
                detach = (CameraDetach) param;
                param.init();
            } else if (s.equalsIgnoreCase(DelayStart.TYPE)) {
                delayStart = (DelayStart) param;
                param.init();
            } else if (s.equalsIgnoreCase(DelayEnd.TYPE)) {
                delayEnd = (DelayEnd) param;
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

    public CameraDetach getDetach() {
        return detach;
    }

}
