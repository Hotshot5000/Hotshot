/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/5/21, 7:44 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.levelresource.LevelEvent.DelayType;

import java.util.ArrayList;

public class LevelEndCond {

    public enum EndCondType {
        DESTROYED, TIME_ELAPSED, PLAYER_SHIP_DESTINATION_REACHED, EXITED, CARGO_SCANNED,
        SHIP_DESTINATION_REACHED, EXITED_OR_DESTROYED, TEXT_SHOWN
    }

    public static class EndCond {
        public String name;
        public EndCondType type;
        public int secs;
        public DelayType delayType;
        public long objectiveAchievedDelaySecs;
        public DelayType objectiveAchievedDelayType;
        public ArrayList<String> objects;
        //	public String destinationLevelObject;

        // For world manager
        public boolean conditionMet;
        public long startTime; // For type TIME_ELAPSED
        public long delayStartTime;
    }

    public String name;
    public final ArrayList<EndCond> winList = new ArrayList<>();
    public final ArrayList<EndCond> lossList = new ArrayList<>();
    public final ComparatorNode winNode = new ComparatorNode();
    public final ComparatorNode lossNode = new ComparatorNode();
}
