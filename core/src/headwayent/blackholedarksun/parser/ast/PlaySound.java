/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;

public class PlaySound extends ObjectEventParam {

    public static final String TYPE = "PlaySound";
    private final String soundName;
    private ENG_Vector3D soundPos;
    private String objName;

    public PlaySound(String soundName) {
        super(TYPE);
        this.soundName = soundName;
    }

    public PlaySound(String soundName, ENG_Vector3D soundPos) {
        super(TYPE);
        this.soundName = soundName;
        this.soundPos = soundPos;
    }

    public PlaySound(String soundName, String objName) {
        super(TYPE);
        this.soundName = soundName;
        this.objName = objName;
    }

    public String getSoundName() {
        return soundName;
    }

    public ENG_Vector3D getSoundPos() {
        return soundPos;
    }

    public String getObjName() {
        return objName;
    }
}
