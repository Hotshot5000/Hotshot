/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class SetSpeed extends ObjectEventParam {

    public static final String TYPE = "SetSpeed";

    private final float speed;

    public SetSpeed(float speed) {
        super(TYPE);
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
