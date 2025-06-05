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
        SHIP_DESTINATION_REACHED, EXITED_OR_DESTROYED, TEXT_SHOWN;

        public static EndCondType getType(String type) {
            switch (type) {
                case "DESTROYED" -> {
                    return DESTROYED;
                }
                case "TIME_ELAPSED" -> {
                    return TIME_ELAPSED;
                }
                case "PLAYER_SHIP_DESTINATION_REACHED" -> {
                    return PLAYER_SHIP_DESTINATION_REACHED;
                }
                case "EXITED" -> {
                    return EXITED;
                }
                case "CARGO_SCANNED" -> {
                    return CARGO_SCANNED;
                }
                case "SHIP_DESTINATION_REACHED" -> {
                    return SHIP_DESTINATION_REACHED;
                }
                case "EXITED_OR_DESTROYED" -> {
                    return EXITED_OR_DESTROYED;
                }
                case "TEXT_SHOWN" -> {
                    return TEXT_SHOWN;
                }
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }
        }
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

        public EndCond() {
        }

        public String getName() {
            return name;
        }

        public EndCondType getType() {
            return type;
        }

        public int getSecs() {
            return secs;
        }

        public DelayType getDelayType() {
            return delayType;
        }

        public long getObjectiveAchievedDelaySecs() {
            return objectiveAchievedDelaySecs;
        }

        public DelayType getObjectiveAchievedDelayType() {
            return objectiveAchievedDelayType;
        }

        public ArrayList<String> getObjects() {
            return objects;
        }

        public boolean isConditionMet() {
            return conditionMet;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getDelayStartTime() {
            return delayStartTime;
        }
    }

    public String name;
    public final ArrayList<EndCond> winList = new ArrayList<>();
    public final ArrayList<EndCond> lossList = new ArrayList<>();
    public final ComparatorNode winNode = new ComparatorNode();
    public final ComparatorNode lossNode = new ComparatorNode();

    public LevelEndCond() {
    }

    public String getName() {
        return name;
    }

    public ArrayList<EndCond> getWinList() {
        return winList;
    }

    public ArrayList<EndCond> getLossList() {
        return lossList;
    }

    public ComparatorNode getWinNode() {
        return winNode;
    }

    public ComparatorNode getLossNode() {
        return lossNode;
    }
}
