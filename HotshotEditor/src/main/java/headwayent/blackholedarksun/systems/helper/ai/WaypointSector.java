/*
 * Created by Sebastian Bugiu on 16/02/2025, 11:50
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 11:50
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;

public class WaypointSector {

    private static final boolean FORCE_WAYPOINT_TABLE_RECALCULATION = true;
    private final int id;
    private final ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
    private final HashMap<ENG_Integer, Waypoint> idToWaypoints = new HashMap<>();
    private final ArrayList<Waypoint> waypoints = new ArrayList<>();
    private final WaypointTable waypointTable = new WaypointTable();
    private final ArrayList<ENG_Integer> nextWaypointSectorIds = new ArrayList<>();
    private int maxUserCount;

    public WaypointSector(int id) {
        this.id = id;
    }

    public void addWaypoint(Waypoint waypoint) {
        Waypoint put = idToWaypoints.put(new ENG_Integer(waypoint.getId()), waypoint);
        if (put != null) {
            throw new IllegalArgumentException("waypoint: " + waypoint.getId() + " already added");
        }
        waypoints.add(waypoint);
    }

    public void removeWaypoint(Waypoint waypoint) {
        Waypoint remove = idToWaypoints.remove(new ENG_Integer(waypoint.getId()));
        if (remove == null) {
            throw new IllegalArgumentException("Waypoint: " + waypoint.getId() + " doesn't exist in sector: " + id);
        }
        waypoints.remove(waypoint);
    }

    public void removeWaypoint(int id) {
        removeWaypoint(new ENG_Integer(id));
    }

    public void removeWaypoint(ENG_Integer id) {
        Waypoint remove = idToWaypoints.remove(id);
        if (remove == null) {
            throw new IllegalArgumentException("Waypoint: " + id + " doesn't exist in sector: " + id);
        }
        waypoints.remove(remove);
    }

    public void removeAllWaypoints() {
        idToWaypoints.clear();
        waypoints.clear();
    }

    public void initialize() {
        checkWaypointsUnique();
        initializeWaypointDistances();

        if (waypointTable.getTable() == null || FORCE_WAYPOINT_TABLE_RECALCULATION) {
            waypointTable.allocateTable(waypoints.size());
            waypointTable.createTable(waypoints);
        } else {
            if (waypointTable.getTable().length != waypoints.size()) {
                throw new IllegalStateException(waypoints.size() +
                        " number of waypoints does not match the table size: " + waypointTable.getTable().length);
            }
        }
    }

    private void checkWaypointsUnique() {
        int currentId = 0;
        for (int i = 0; i < waypoints.size(); ++i) {
            currentId = waypoints.get(i).getId();
            for (int j = 0; j < waypoints.size(); ++j) {
                if (i == j) {
                    continue;
                }
                if (currentId == waypoints.get(j).getId()) {
                    throw new IllegalStateException("2 waypoints have the same id: " + currentId);
                }
            }
        }
    }

    private void initializeWaypointDistances() {
        for (Waypoint waypoint : waypoints) {
            int nextIdsSize = waypoint.getNextWaypointIds().size();
            for (int i = 0; i < nextIdsSize; ++i) {
                ENG_Integer nextWaypointId = waypoint.getNextWaypointIds().get(i);
                Waypoint nextWaypoint = getWaypoint(nextWaypointId);
                // Check if the distance hasn't already been calculated and written for bidirectional waypoints.
                // Distance from A to B is the same as B to A no need to do it twice.
                int origWaypointIdIndex = nextWaypoint.getIndexOfNextWaypointId(waypoint.getId());
                if (origWaypointIdIndex != -1 && nextWaypoint.getNextWaypointDistances().get(origWaypointIdIndex).getValue() > 0.0f) {
                    continue;
                }
                float distance = (float) waypoint.getPosition().distance(nextWaypoint.getPosition());
                // Write the distance to the next waypoint and if the next waypoint also points to this waypoint
                // also write the distance into that waypoint.
                waypoint.getNextWaypointDistances().get(i).setValue(distance);
                if (origWaypointIdIndex != -1) {
                    nextWaypoint.getNextWaypointDistances().get(origWaypointIdIndex).setValue(distance);
                }
            }

        }
        if (WaypointSystem.DEBUG) {
            for (Waypoint waypoint : waypoints) {
                int nextIdsSize = waypoint.getNextWaypointIds().size();
                System.out.println("waypoint id: " + waypoint.getId() + " nextIdsSize: " + nextIdsSize);
                for (int i = 0; i < nextIdsSize; ++i) {
                    System.out.println("nextWaypointId: " + waypoint.getNextWaypointIds().get(i) +
                            " nextWaypoint distance: " + waypoint.getNextWaypointDistances().get(i));
                }
            }

        }
    }

    public float getDistanceToSector(ENG_Vector4D position) {
        return 0.0f;
    }

    public int getNextWaypointToDestination(int currentWaypointId, int destinationWaypointId) {
        return waypointTable.getNextWaypointToDestination(currentWaypointId, destinationWaypointId);
    }

    // The info of if the start or end position is inside the sector must come from outside.
    public int getNextWaypointToDestination(ENG_Vector4D startPosition, ENG_Vector4D endPosition,
                                            boolean startEntranceOrExit, boolean endEntranceOrExit) {
        Waypoint startWaypoint = getClosestWaypoint(startPosition, startEntranceOrExit);
        Waypoint endWaypoint = getClosestWaypoint(endPosition, endEntranceOrExit);
        return waypointTable.getNextWaypointToDestination(startWaypoint.getId(), endWaypoint.getId());
    }

    // The info of if the start or end position is inside the sector must come from outside.
    public ArrayList<ENG_Integer> getWaypointChainFromPosition(ENG_Vector4D startPosition, ENG_Vector4D endPosition,
                                                               boolean startEntranceOrExit, boolean endEntranceOrExit) {
        ArrayList<ENG_Integer> output = new ArrayList<>();
        getWaypointChainFromPosition(startPosition, endPosition, startEntranceOrExit, endEntranceOrExit, output);
        return output;
    }

    // The info of if the start or end position is inside the sector must come from outside.
    public void getWaypointChainFromPosition(ENG_Vector4D startPosition, ENG_Vector4D endPosition,
                                             boolean startEntranceOrExit, boolean endEntranceOrExit,
                                             ArrayList<ENG_Integer> outputList) {
        Waypoint startWaypoint = getClosestWaypoint(startPosition, startEntranceOrExit);
        Waypoint endWaypoint = getClosestWaypoint(endPosition, endEntranceOrExit);
        waypointTable.getWaypointChainToDestination(startWaypoint.getId(), endWaypoint.getId(), outputList);
    }

    public ArrayList<Waypoint> getEntranceOrExitWaypoints() {
        ArrayList<Waypoint> list = new ArrayList<>();
        getEntranceOrExitWaypoints(list);
        return list;
    }

    public void getEntranceOrExitWaypoints(ArrayList<Waypoint> outputList) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.isEntranceOrExit()) {
                outputList.add(waypoint);
            }
        }
    }

    public ArrayList<Waypoint> getClosestWaypoint(ENG_Vector4D position, float radius) {
        return getClosestWaypoint(position, radius, false);
    }

    public ArrayList<Waypoint> getClosestWaypoint(ENG_Vector4D position, float radius, boolean entranceOrExit) {
        ArrayList<Waypoint> outputList = new ArrayList<>();
        getClosestWaypoint(position, radius, entranceOrExit, outputList);
        return outputList;
    }

    public void getClosestWaypoint(ENG_Vector4D position, float radius, boolean entranceOrExit, ArrayList<Waypoint> outputList) {
        for (Waypoint waypoint : waypoints) {
            if (entranceOrExit && !waypoint.isEntranceOrExit()) {
                continue;
            }
            if (waypoint.getPosition().distance(position) <= radius) {
                outputList.add(waypoint);
            }
        }
    }

    public Waypoint getClosestWaypoint(ENG_Vector4D position) {
        return getClosestWaypoint(position, false);
    }

    public Waypoint getClosestWaypoint(ENG_Vector4D position, boolean entranceOrExit) {
        float minDistance = Float.POSITIVE_INFINITY;
        Waypoint closestWaypoint = null;
        for (Waypoint waypoint : waypoints) {
            if (entranceOrExit && !waypoint.isEntranceOrExit()) {
                continue;
            }
            float distanceToWaypoint = waypoint.getDistanceToWaypoint(position);
            if (minDistance > distanceToWaypoint) {
                minDistance = distanceToWaypoint;
                closestWaypoint = waypoint;
            }
        }
        return closestWaypoint;
    }

    public float getTotalDistanceForWaypointChain(int startWaypointId, ArrayList<ENG_Integer> nextWaypointIds) {
        float distance = 0.0f;
        for (ENG_Integer nextWaypointId : nextWaypointIds) {
            Waypoint currentWaypoint = getWaypoint(startWaypointId);
            int indexOfNextWaypointId = currentWaypoint.getIndexOfNextWaypointId(nextWaypointId);
            if (indexOfNextWaypointId == -1) {
                throw new IllegalArgumentException("nextWaypointId: " + nextWaypointId +
                        " is not available for current waypoint id: " + startWaypointId);
            }
            distance += currentWaypoint.getNextWaypointDistances().get(indexOfNextWaypointId).getValue();
            startWaypointId = nextWaypointId.getValue();
        }
        if (WaypointSystem.DEBUG) {
            StringBuilder str = new StringBuilder();
            str.append(startWaypointId);
            for (ENG_Integer nextWaypointId : nextWaypointIds) {
                str.append(" ").append(nextWaypointId);
            }
            System.out.println("waypoint chain: " + str);
        }
        return distance;
    }

    protected ENG_AxisAlignedBox getBox() {
        return box;
    }

    public void setAxisAlignedBox(ENG_AxisAlignedBox box) {
        this.box.set(box);
    }

    public Waypoint getWaypoint(int id) {
        return getWaypoint(new ENG_Integer(id));
    }

    public Waypoint getWaypoint(ENG_Integer id) {
        return idToWaypoints.get(id);
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addNextWaypointSectorId(int nextId) {
        addNextWaypointSectorId(new ENG_Integer(nextId));
    }

    public void addNextWaypointSectorId(ENG_Integer nextId) {
        nextWaypointSectorIds.add(nextId);
    }

    public void removeNextWaypointSectorId(int nextId) {
        removeNextWaypointSectorId(new ENG_Integer(nextId));
    }

    public void removeNextWaypointSectorId(ENG_Integer nextId) {
        boolean remove = nextWaypointSectorIds.remove(nextId);
        if (!remove) {
            throw new IllegalArgumentException("NextId: " + nextId + " not found!");
        }
    }

    public void removeAllNextWaypointSectorIds() {
        nextWaypointSectorIds.clear();
    }

    public ArrayList<ENG_Integer> getNextWaypointSectorIds() {
        return nextWaypointSectorIds;
    }

    public int getTotalWaypointUserCount() {
        int totalWaypointUserCount = 0;
        for (Waypoint waypoint : waypoints) {
            totalWaypointUserCount += waypoint.getWaypointUsersCount();
        }
        return totalWaypointUserCount;
    }

    public boolean canAddUserToWaypoint() {
        int totalWaypointUserCount = getTotalWaypointUserCount();
        if (totalWaypointUserCount > maxUserCount) {
            if (WaypointSystem.DEBUG) {
                System.out.println("totalWaypointUserCount : " + totalWaypointUserCount + " maxUserCount: " + maxUserCount);
            }
            return false;
        }
        return true;
    }

    public boolean checkPositionInsideSector(ENG_Vector4D position) {
        return box.contains(position);
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

    public void setWaypointTable(int[][] table) {
        waypointTable.setTable(table);
    }

    public WaypointTable getWaypointTable() {
        return waypointTable;
    }
}
