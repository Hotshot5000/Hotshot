/*
 * Created by Sebastian Bugiu on 16/02/2025, 11:29
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 11:29
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import java.util.ArrayList;
import java.util.Objects;

import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;

public class Waypoint implements Comparable<Waypoint> {

    private final int id;
    private final ENG_Vector4D position = new ENG_Vector4D(true);
    private final ArrayList<ENG_Integer> nextWaypointIds = new ArrayList<>();
    private final ArrayList<ENG_Float> nextWaypointDistances = new ArrayList<>();
    private final ArrayList<ENG_Long> waypointUsers = new ArrayList<>(); // How many entities are targeting this waypoint.
    private WaypointEntranceExit waypointEntranceExit; // If this is a waypoint situated at an entrance or exit from a waypointed area.
    private int maxUserCount;
    private float radius;
    // Not yet used for distance calculations.
    // Can be used to test if it makes more sense to go around a sector than through it.
    private float weight;
    private boolean active;
//    private boolean entranceOrExit; // If this is a waypoint situated at an entrance or exit from a waypointed area.

    public Waypoint(int id) {
        this.id = id;
    }

    public boolean addWaypointUser(long userId) {
        return addWaypointUser(new ENG_Long(userId));
    }

    public boolean addWaypointUser(ENG_Long userId) {
        if (WaypointSystem.DEBUG) {
            if (waypointUsers.contains(userId)) {
                throw new IllegalArgumentException(userId + " already added to waypoint users for waypoint id: " + id);
            }
        }
        if (getWaypointUsersCount() < getMaxUserCount()) {
            waypointUsers.add(userId);
            printCurrentUserCount();
            return true;
        } else {
            if (WaypointSystem.DEBUG) {
                System.out.println("waypointUsersCount reached maxUserCount: " + getMaxUserCount());
            }
        }
        return false;
    }

    public boolean removeWaypointUser(long userId) {
        return removeWaypointUser(new ENG_Long(userId));
    }

    public boolean removeWaypointUser(ENG_Long userId) {
        boolean remove = waypointUsers.remove(userId);
        if (WaypointSystem.DEBUG) {
            if (!remove) {
                System.out.println(userId + " is not part of waypoint id: " + id);
            }
        }
        printCurrentUserCount();
        return remove;
    }

    private void printCurrentUserCount() {
        if (WaypointSystem.DEBUG) {
            System.out.println("waypoint id: " + id + " current user count: " + waypointUsers.size());
        }
    }

    public void removeAllWaypointUsers() {
        waypointUsers.clear();
        printCurrentUserCount();
    }

    public int getWaypointUsersCount() {
        return waypointUsers.size();
    }

    public boolean isUserAddedToWaypoint(ENG_Long userId) {
        for (ENG_Long waypointUser : waypointUsers) {
            if (waypointUser.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public float getDistanceToWaypoint(ENG_Vector4D position) {
        return this.position.distance(position);
    }

    public void addNextWaypointId(int nextId) {
        addNextWaypointId(new ENG_Integer(nextId));
    }

    public void addNextWaypointId(ENG_Integer nextId) {
        nextWaypointIds.add(nextId);
        nextWaypointDistances.add(new ENG_Float(0.0f));
    }

    public void removeNextWaypointId(int nextId) {
        removeNextWaypointId(new ENG_Integer(nextId));
    }

    public void removeNextWaypointId(ENG_Integer nextId) {
        int i = nextWaypointIds.indexOf(nextId);
        if (i == -1) {
            throw new IllegalArgumentException("NextId: " + nextId + " not found!");
        }
        nextWaypointIds.remove(i);
        nextWaypointDistances.remove(i);
    }

    public void removeAllNextWaypointIds() {
        nextWaypointIds.clear();
        nextWaypointDistances.clear();
    }

    public ArrayList<ENG_Integer> getNextWaypointIds() {
        return nextWaypointIds;
    }

    public boolean containsNextWaypointId(int id) {
        return containsNextWaypointId(new ENG_Integer(id));
    }

    public boolean containsNextWaypointId(ENG_Integer id) {
        return nextWaypointIds.contains(id);
    }

    public int getIndexOfNextWaypointId(int id) {
        return getIndexOfNextWaypointId(new ENG_Integer(id));
    }

    public int getIndexOfNextWaypointId(ENG_Integer id) {
        return nextWaypointIds.indexOf(id);
    }

    public ArrayList<ENG_Float> getNextWaypointDistances() {
        return nextWaypointDistances;
    }

    public void setPosition(ENG_Vector4D position) {
        this.position.set(position);
    }

    public ENG_Vector4D getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public int getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(int maxUserCount) {
        this.maxUserCount = maxUserCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isInCone(ENG_Vector4D targetPosition) {
        if (waypointEntranceExit == null) {
            if (WaypointSystem.DEBUG) {
                throw new IllegalStateException("This waypoint is not entrance or exit for the sector");
            } else {
                System.out.println("This waypoint is not entrance or exit for the sector");
                return false;
            }
        }
        return waypointEntranceExit.isInCone(targetPosition, this.position);
    }

    public WaypointEntranceExit getWaypointEntranceExit() {
        return waypointEntranceExit;
    }

    public boolean isEntranceOrExit() {
        return waypointEntranceExit != null;
    }

    public void setEntranceOrExit(ENG_Vector4D direction, float angle, float minDistance) {
        waypointEntranceExit = new WaypointEntranceExit();
        waypointEntranceExit.setEntranceExitDirection(direction);
        waypointEntranceExit.setEntranceExitAngle(angle);
        waypointEntranceExit.setEntranceExitMinDistance(minDistance);
    }

    public void resetEntranceOrExit() {
        waypointEntranceExit = null;
    }

    public boolean reached(ENG_Vector4D pos) {
        if (WaypointSystem.DEBUG) {
            if (radius == 0.0f) {
                throw new IllegalStateException("Invalid radius: " + radius + " for waypoint id: " + id);
            }
        }
        return pos.distance(position) < radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Waypoint o) {
        if (id == o.getId()) {
            throw new IllegalStateException(id + " identical in waypoint sector");
        }
        return id - o.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint waypoint = (Waypoint) o;
        return id == waypoint.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
