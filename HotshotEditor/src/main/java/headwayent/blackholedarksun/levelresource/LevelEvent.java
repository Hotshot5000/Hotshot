/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/16/21, 7:25 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.levelresource.LevelEndCond.EndCond;
//import headwayent.blackholedarksun.levelresource.levelevents.AfterburnerButtonPressedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.CountermeasuresButtonPressedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.MissileEvadedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.ReloaderButtonPressedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.ShipDestroyedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.ShipSpawnedValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.SpeedPercentageValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.WeaponCycledValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.WeaponLoadValidator;
//import headwayent.blackholedarksun.levelresource.levelevents.WeaponUsedValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LevelEvent implements Comparable<LevelEvent> {

    public enum DelayType {
        SECS, MSECS;

        public static DelayType getDelayType(String type) {
            if (SECS.toString().equalsIgnoreCase(type)) {
                return SECS;
            }
            if (MSECS.toString().equalsIgnoreCase(type) || "ms".equalsIgnoreCase(type)) {
                return MSECS;
            }
            throw new IllegalArgumentException(type + " is an invalid delay type");
        }
    }

    public enum EventState {
        NONE, STARTABLE, STARTED, WON, LOST, CONNECTION_LOST;

        @Override
        public String toString() {
            switch (ordinal()) {
                case 0:
                    return "none";
                case 1:
                    return "startable";
                case 2:
                    return "started";
                case 3:
                    return "won";
                case 4:
                    return "lost";
                case 5:
                    return "connection_lost";
                default:
                    throw new IllegalStateException("Unexpected value: " + ordinal());
            }
        }
    }

//    public static final HashMap<String, LevelEventValidatorFactory> levelEventValidatorFactoryMap = new HashMap<>();
//
//    static {
//        levelEventValidatorFactoryMap.put(AfterburnerButtonPressedValidator.AfterburnerButtonPressedValidatorFactory.TYPE, new AfterburnerButtonPressedValidator.AfterburnerButtonPressedValidatorFactory());
//        levelEventValidatorFactoryMap.put(CountermeasuresButtonPressedValidator.CountermeasuresButtonPressedValidatorFactory.TYPE, new CountermeasuresButtonPressedValidator.CountermeasuresButtonPressedValidatorFactory());
//        levelEventValidatorFactoryMap.put(ReloaderButtonPressedValidator.ReloaderButtonPressedValidatorFactory.TYPE, new ReloaderButtonPressedValidator.ReloaderButtonPressedValidatorFactory());
//        levelEventValidatorFactoryMap.put(MissileEvadedValidator.MissileEvadedValidatorFactory.TYPE, new MissileEvadedValidator.MissileEvadedValidatorFactory());
//        levelEventValidatorFactoryMap.put(ShipDestroyedValidator.ShipDestroyedValidatorFactory.TYPE, new ShipDestroyedValidator.ShipDestroyedValidatorFactory());
//        levelEventValidatorFactoryMap.put(ShipSpawnedValidator.ShipSpawnedValidatorFactory.TYPE, new ShipSpawnedValidator.ShipSpawnedValidatorFactory());
//        levelEventValidatorFactoryMap.put(SpeedPercentageValidator.SpeedPercentageValidatorFactory.TYPE, new SpeedPercentageValidator.SpeedPercentageValidatorFactory());
//        levelEventValidatorFactoryMap.put(WeaponCycledValidator.WeaponCycledValidatorFactory.TYPE, new WeaponCycledValidator.WeaponCycledValidatorFactory());
//        levelEventValidatorFactoryMap.put(WeaponLoadValidator.WeaponLoadValidatorFactory.TYPE, new WeaponLoadValidator.WeaponLoadValidatorFactory());
//        levelEventValidatorFactoryMap.put(WeaponUsedValidator.WeaponUsedValidatorFactory.TYPE, new WeaponUsedValidator.WeaponUsedValidatorFactory());
//    }

    public String name;
    public final ArrayList<String> prevCondList = new ArrayList<>();
    public final ComparatorNode prevCondEndRoot = new ComparatorNode();
    public int delay;
    public DelayType delayType;
    public final ArrayList<LevelObject> spawn = new ArrayList<>();
    public final ArrayList<String> exitObjects = new ArrayList<>();
    public LevelEndCond endCond;
    public String textShown;
    public long textShownDuration;
//    public final ArrayList<LevelEventValidator> levelEventValidatorList = new ArrayList<>();

    // For use in World Manager
    public EventState state = EventState.NONE;
    /*	public boolean eventWon, eventLost;
        public boolean eventCompleted;
        public boolean eventStarted;
        public boolean eventStartable;*/
    public long currentStartingTime;
    public final HashMap<String, EndCond> winEndCondList = new HashMap<>();
    public final HashMap<String, EndCond> lossEndCondList = new HashMap<>();
//	public ArrayList<Entity> exitEntities = new ArrayList<Entity>();

//    public boolean validateLevelEvent() {
//        for (LevelEventValidator levelEventValidator : levelEventValidatorList) {
//            if (!levelEventValidator.validate(this)) {
//                return false;
//            }
//        }
//        return true;
//    }


    public LevelEvent() {
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LevelEvent that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public int compareTo(LevelEvent o) {
        return name.compareTo(o.name);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPrevCondList() {
        return prevCondList;
    }

    public ComparatorNode getPrevCondEndRoot() {
        return prevCondEndRoot;
    }

    public int getDelay() {
        return delay;
    }

    public DelayType getDelayType() {
        return delayType;
    }

    public ArrayList<LevelObject> getSpawn() {
        return spawn;
    }

    public ArrayList<String> getExitObjects() {
        return exitObjects;
    }

    public LevelEndCond getEndCond() {
        return endCond;
    }

    public String getTextShown() {
        return textShown;
    }

    public long getTextShownDuration() {
        return textShownDuration;
    }

    public EventState getState() {
        return state;
    }

    public long getCurrentStartingTime() {
        return currentStartingTime;
    }

    public HashMap<String, EndCond> getWinEndCondList() {
        return winEndCondList;
    }

    public HashMap<String, EndCond> getLossEndCondList() {
        return lossEndCondList;
    }
}
