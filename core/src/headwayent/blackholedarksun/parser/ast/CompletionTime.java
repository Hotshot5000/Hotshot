/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelEvent;

public class CompletionTime extends ObjectEventParam {

    public static final String TYPE = "CompletionTime";
    private final long time;
    private final LevelEvent.DelayType timeType;
    // Helpers for the dispatcher.
    private long beginTime;

    public CompletionTime(long time, String timeType) {
        super(TYPE);
        this.time = time;
        this.timeType = LevelEvent.DelayType.getDelayType(timeType);
    }

    public long getTime() {
        return time;
    }

    public LevelEvent.DelayType getTimeType() {
        return timeType;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }
}
