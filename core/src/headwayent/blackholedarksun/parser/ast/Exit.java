/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class Exit extends ObjectEventParam {

    public static final String TYPE = "Exit";
    private final String exitName;

    public Exit(String exitName) {
        super(TYPE);
        this.exitName = exitName;
    }

    public String getExitName() {
        return exitName;
    }
}
