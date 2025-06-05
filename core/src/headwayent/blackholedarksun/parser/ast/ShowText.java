/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.hotshotengine.ENG_Vector2D;

public class ShowText extends ObjectEventParam {

    public static final String TYPE = "ShowText";
    private final String text;
    private int time;
    private LevelEvent.DelayType timeType;
    private ENG_Vector2D position;

    public ShowText(String text, int time, String timeType) {
        super(TYPE);
        this.text = text;
        init(time, timeType);
    }

    private void init(int time, String timeType) {
        this.time = time;
        this.timeType = LevelEvent.DelayType.getDelayType(timeType);
        if (time == 0) {
            if (text.contains(" ")) {
                // Assume the average reader reads 3 words per second.
                this.time = text.split(" ").length / 3 * 1000 + 500; // 500 ms padding.
            } else {
                this.time = 1000;
            }
            this.timeType = LevelEvent.DelayType.MSECS;
        }
    }

    public ShowText(String text, ENG_Vector2D pos, int time, String timeType) {
        super(TYPE);
        this.text = text;
        this.position = pos;
        init(time, timeType);
    }

    public String getText() {
        return text;
    }

    public ENG_Vector2D getPosition() {
        return position;
    }

    public int getTime() {
        return time;
    }

    public LevelEvent.DelayType getTimeType() {
        return timeType;
    }
}
