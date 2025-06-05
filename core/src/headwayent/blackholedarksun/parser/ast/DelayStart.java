/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelEvent;

public class DelayStart extends ObjectEventParam {

    public static final String TYPE = "DelayStart";
    private final int time;
    private final LevelEvent.DelayType delayType;

    public DelayStart(int time, String delayType) {
        super(TYPE);
        this.time = time;
        this.delayType = LevelEvent.DelayType.getDelayType(delayType);
    }

    public int getTime() {
        return time;
    }

    public LevelEvent.DelayType getDelayType() {
        return delayType;
    }
}
