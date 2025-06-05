/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.gamestatedebugger;

/**
 * Created by sebas on 04.10.2015.
 */
public class TrackerEntity {
    private String name;
    private boolean countermeasureSuccessRand;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCountermeasureSuccessRand() {
        return countermeasureSuccessRand;
    }

    public void setCountermeasureSuccessRand(boolean countermeasureSuccessRand) {
        this.countermeasureSuccessRand = countermeasureSuccessRand;
    }
}
