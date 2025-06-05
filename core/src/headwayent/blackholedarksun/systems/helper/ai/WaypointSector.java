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
import headwayent.hotshotengine.basictypes.ENG_Float;
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
                float distance = waypoint.getPosition().distance(nextWaypoint.getPosition());
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

    private boolean getWaypointChainInFront(ENG_Integer currentWaypoint, ENG_Vector4D position, ENG_Vector4D frontVec,
                                         int maxCount, ENG_Integer currentCount, ENG_Float currentTotalDistance,
                                         float maxAngle, ArrayList<ENG_Integer> outputList) {
        boolean reachedEnd = false;
        Waypoint waypoint = getWaypoint(currentWaypoint);
        ArrayList<ENG_Integer> nextWaypointIds = waypoint.getNextWaypointIds();
        for (int i = 0, nextWaypointIdsSize = nextWaypointIds.size(); i < nextWaypointIdsSize; i++) {
            ENG_Integer nextWaypointId = nextWaypointIds.get(i);
            // Make sure we are not going back from where we came from in the chain.
            if (outputList.contains(nextWaypointId)) continue;
            Waypoint nextWaypoint = getWaypoint(nextWaypointId);
            ENG_Vector4D waypointDiff = nextWaypoint.getPosition().subAsVec(position);
            waypointDiff.normalize();
            float angleBetween = frontVec.angleBetween(waypointDiff);
            if (angleBetween < maxAngle) {
                currentTotalDistance.addInPlace(waypoint.getNextWaypointDistances().get(i));
                currentCount.addInPlace(1);
                outputList.add(nextWaypointId);
                if (currentCount.getValue() < maxCount) {
                    reachedEnd = getWaypointChainInFront(nextWaypointId, position, frontVec, maxCount,
                            currentCount, currentTotalDistance, maxAngle, outputList);
                    if (!reachedEnd) {
                        outputList.remove(outputList.size() - 1);
                        currentTotalDistance.subInPlace(waypoint.getNextWaypointDistances().get(i));
                        currentCount.subInPlace(1);
                    } else {
                        // Bail out completely.
                        return true;
                    }
                } else {
                    // We found all the waypoints that we need.
                    return true;
                }
            }
        }
        return reachedEnd;
    }

    public ArrayList<ENG_Integer> getWaypointChainInFront(int currentWaypoint, ENG_Vector4D position, ENG_Vector4D frontVec,
                                        int maxCount, float maxAngle) {
        ArrayList<ENG_Integer> outputList = new ArrayList<>();
        getWaypointChainInFront(new ENG_Integer(currentWaypoint), position, frontVec, maxCount, maxAngle, outputList);
        return outputList;
    }

    public float getWaypointChainInFront(int currentWaypoint, ENG_Vector4D position, ENG_Vector4D frontVec,
                                         int maxCount, float maxAngle, ArrayList<ENG_Integer> outputList) {
        return getWaypointChainInFront(new ENG_Integer(currentWaypoint), position, frontVec, maxCount, maxAngle, outputList);
    }

    public float getWaypointChainInFront(ENG_Integer currentWaypoint, ENG_Vector4D position, ENG_Vector4D frontVec,
                                         int maxCount, float maxAngle, ArrayList<ENG_Integer> outputList) {
        ENG_Float totalDistance = new ENG_Float();
        ENG_Integer currentDepthCount = new ENG_Integer();
        // Add the current waypoint id for comparison so we don't start from waypoint 4 go to 5 and back to 4 since 5
        // also has a next_id of the previous 4.
        outputList.add(currentWaypoint);
        boolean maxCountReached = getWaypointChainInFront(currentWaypoint, position, frontVec, maxCount,
                currentDepthCount, totalDistance, maxAngle, outputList);
        outputList.remove(0);
        return totalDistance.getValue();
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
        return getClosestWaypoint(position, radius, false, null);
    }

    /**
     *
     * @param position
     * @param radius
     * @param ignoreIds If you have a waypoint that is already the target it makes no sense to include it in the same list.
     *                 Use 0 in order to ignore this parameter as all waypoint ids >= 1.
     * @return
     */
    public ArrayList<Waypoint> getClosestWaypoint(ENG_Vector4D position, float radius, int[] ignoreIds) {
        return getClosestWaypoint(position, radius, false, ignoreIds);
    }

    public ArrayList<Waypoint> getClosestWaypoint(ENG_Vector4D position, float radius, boolean entranceOrExit, int[] ignoreIds) {
        ArrayList<Waypoint> outputList = new ArrayList<>();
        getClosestWaypoint(position, radius, entranceOrExit, ignoreIds, outputList);
        return outputList;
    }

    public void getClosestWaypoint(ENG_Vector4D position, float radius, boolean entranceOrExit, int[] ignoreIds, ArrayList<Waypoint> outputList) {
        goTo:
        for (Waypoint waypoint : waypoints) {
            if (entranceOrExit && !waypoint.isEntranceOrExit()) {
                continue;
            }
            if (waypoint.getPosition().distance(position) <= radius) {
                if (ignoreIds != null) {
                    for (int j = 0, ignoreIdLength = ignoreIds.length; j < ignoreIdLength; j++) {
                        int idToIgnore = ignoreIds[j];
                        if (waypoint.getId() == idToIgnore) {
                            continue goTo;
                        }
                    }
                }
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

    public boolean isUserLimitReached() {
        return getTotalWaypointUserCount() >= getMaxUserCount();
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
        if (totalWaypointUserCount >= maxUserCount) {
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
}
