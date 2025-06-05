/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.hotshotengine.ENG_Vector3D;

public class CameraLookAt extends ObjectEventParam {

    public static final String TYPE = "CameraLookAt";
    private String objectName;
    private ENG_Vector3D position;
    private int time;
    private LevelEvent.DelayType delayType;
    // Helpers for the dispatcher.
    private long beginTime;
    private boolean delayActive;

    public CameraLookAt(String name) {
        super(TYPE);
        this.objectName = name;
    }

    public CameraLookAt(ENG_Vector3D vec) {
        super(TYPE);
        this.position = vec;
    }

    public CameraLookAt(String name, int time, String delayType) {
        super(TYPE);
        this.objectName = name;
        initTime(time, delayType);
    }

    public CameraLookAt(ENG_Vector3D vec, int time, String delayType) {
        super(TYPE);
        this.position = vec;
        initTime(time, delayType);
    }

    private void initTime(int time, String delayType) {
        this.time = time;
        this.delayType = LevelEvent.DelayType.getDelayType(delayType);
        delayActive = true;
    }

    public String getObjectName() {
        return objectName;
    }

    public ENG_Vector3D getPosition() {
        return position;
    }

    public int getTime() {
        return time;
    }

    public LevelEvent.DelayType getDelayType() {
        return delayType;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isDelayActive() {
        return delayActive;
    }
}
