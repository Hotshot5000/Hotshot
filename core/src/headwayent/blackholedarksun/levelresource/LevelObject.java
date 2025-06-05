/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.ArrayList;

public class LevelObject {

    public static final int DEFAULT_DAMAGE_DEALT = 1;

    public enum LevelObjectType {
        PLAYER_SHIP, FIGHTER_SHIP, CARGO_SHIP, ASTEROID, FLAG_RED, FLAG_BLUE, CARGO, PLAYER_SHIP_SELECTION, WAYPOINT, STATIC;

        public static LevelObjectType getLevelObjectType(String type) {
            if (PLAYER_SHIP.toString().equalsIgnoreCase(type)) {
                return PLAYER_SHIP;
            } else if (FIGHTER_SHIP.toString().equalsIgnoreCase(type)) {
                return FIGHTER_SHIP;
            } else if (CARGO_SHIP.toString().equalsIgnoreCase(type)) {
                return CARGO_SHIP;
            } else if (ASTEROID.toString().equalsIgnoreCase(type)) {
                return ASTEROID;
            } else if (FLAG_RED.toString().equalsIgnoreCase(type)) {
                return FLAG_RED;
            } else if (FLAG_BLUE.toString().equalsIgnoreCase(type)) {
                return FLAG_BLUE;
            } else if (CARGO.toString().equalsIgnoreCase(type)) {
                return CARGO;
            } else if (PLAYER_SHIP_SELECTION.toString().equalsIgnoreCase(type)) {
                return PLAYER_SHIP_SELECTION;
            } else if (WAYPOINT.toString().equalsIgnoreCase(type)) {
                return WAYPOINT;
            } else if (STATIC.toString().equalsIgnoreCase(type)) {
                return STATIC;
            }
            throw new IllegalArgumentException(type + " is an invalid LevelObjectType");
        }
    }

    public enum LevelObjectBehavior {
        NEUTRAL, AGGRESSIVE, DEFENSIVE;

        public static LevelObjectBehavior getBehavior(String behavior) {
            if (behavior.equalsIgnoreCase("neutral")) {
                return NEUTRAL;
            } else if (behavior.equalsIgnoreCase("defensive")) {
                return DEFENSIVE;
            } else if(behavior.equalsIgnoreCase("aggressive")) {
                return AGGRESSIVE;
            }
            throw new IllegalArgumentException("Behavior " + behavior + " isn't a valid behavior type");
        }
    }

    public String name; // The level object name to be used with getLevelObject in WorldManagerBase.
    public LevelObjectType type;
    public LevelObjectBehavior behavior = LevelObjectBehavior.NEUTRAL;
    public String meshName;
    public String attackName;
    public final ENG_Vector4D position = new ENG_Vector4D(true);
    public final ENG_Quaternion orientation = new ENG_Quaternion(true);
    public final ENG_Vector4D velocity = new ENG_Vector4D();
    public int health;
    public final int damage = DEFAULT_DAMAGE_DEALT;
    public final ArrayList<String> prioritizeList = new ArrayList<>();
    public float scanRadius;
    public float radius; // For level events beacon position reached
    public boolean ai;
    public ShipData.ShipTeam friendly;
    public boolean invincible;
    public final ENG_Vector4D destination = new ENG_Vector4D(true);
    public boolean reachDestination;
    public long userId;
    public int waypointSectorId;
    public int waypointId;
}
