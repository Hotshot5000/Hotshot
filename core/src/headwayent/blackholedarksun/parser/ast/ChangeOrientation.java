/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Quaternion;

public class ChangeOrientation extends ObjectEventParam {

    public static final String TYPE = "ChangeOrientation";
    private final ENG_Quaternion finalOrientation;
    // Helper data for event dispatcher.
    private ENG_Quaternion beginOrientation;

    public ChangeOrientation(ENG_Quaternion finalOrientation) {
        super(TYPE);
        this.finalOrientation = finalOrientation;
    }

    public ENG_Quaternion getFinalOrientation() {
        return finalOrientation;
    }

    public ENG_Quaternion getBeginOrientation() {
        return beginOrientation;
    }

    public void setBeginOrientation(ENG_Quaternion beginOrientation) {
        this.beginOrientation = beginOrientation;
    }
}
