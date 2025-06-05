/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;

public class ChangePosition extends ObjectEventParam {

    public static final String TYPE = "ChangePosition";
    private final ENG_Vector3D finalPosition;
    // Helper data for event dispatcher.
    private ENG_Vector3D beginPosition;

    public ChangePosition(ENG_Vector3D finalPosition) {
        super(TYPE);
        this.finalPosition = finalPosition;
    }

    public ENG_Vector3D getFinalPosition() {
        return finalPosition;
    }

    public ENG_Vector3D getBeginPosition() {
        return beginPosition;
    }

    public void setBeginPosition(ENG_Vector3D beginPosition) {
        this.beginPosition = beginPosition;
    }
}
