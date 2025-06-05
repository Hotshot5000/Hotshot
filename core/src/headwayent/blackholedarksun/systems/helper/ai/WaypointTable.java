/*
 * Created by Sebastian Bugiu on 16/02/2025, 15:07
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 15:07
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;

public class WaypointTable {

    private int[][] table;

    public WaypointTable() {

    }

    public void allocateTable(int size) {
        table = allocate(size);
    }

    public static int[][] allocate(int size) {
        int[][] table = new int[size][];
        for (int i = 0; i < size; ++i) {
            table[i] = new int[size];
        }
        return table;
    }

    private void copyTable(int[][] source, int[][] destination) {
        if (source.length != destination.length) {
            throw new IllegalArgumentException("source.length: " + source.length + " destination.length: " + destination.length);
        }
        for (int i = 0, sourceLength = source.length; i < sourceLength; i++) {
            int[] src = source[i];
            int[] dest = destination[i];
            if (src.length != dest.length) {
                throw new IllegalArgumentException("i: " + i + " src.length: " + src.length + " dest.length: " + dest.length);
            }
            System.arraycopy(src, 0, dest, 0, src.length);
        }

    }

    public void createTable(ArrayList<Waypoint> waypoints) {
        if (waypoints.size() != table.length) {
            throw new IllegalArgumentException("table allocated size: " + table.length + " waypoints list size: " + waypoints.size());
        }
        updateTable(waypoints);
    }

    private static class WaypointRouteStep {
        public int previousWaypointId;
        public int currentWaypointId;
        public float currentDistance;

        public WaypointRouteStep(int previousWaypointId, int currentWaypointId, float currentDistance) {
            this.previousWaypointId = previousWaypointId;
            this.currentWaypointId = currentWaypointId;
            this.currentDistance = currentDistance;
        }

        @Override
        public String toString() {
            return "WaypointRouteStep{" +
                    "previousWaypointId=" + previousWaypointId +
                    ", currentWaypointId=" + currentWaypointId +
                    ", currentDistance=" + currentDistance +
                    '}';
        }
    }

    // This is no longer needed as the paths are updated directly in the depth first solution.
//    private static class WaypointIdAndPosition {
//        public int currentWaypointId;
//        public int lastPosition;
//        public int currentDepth;
//
//        public WaypointIdAndPosition(int currentWaypointId, int lastPosition, int currentDepth) {
//            this.currentWaypointId = currentWaypointId;
//            this.lastPosition = lastPosition;
//            this.currentDepth = currentDepth;
//        }
//
//        @Override
//        public String toString() {
//            return "WaypointIdAndPosition{" +
//                    "currentWaypointId=" + currentWaypointId +
//                    ", lastPosition=" + lastPosition +
//                    ", currentDepth=" + currentDepth +
//                    '}';
//        }
//    }
//
//    private WaypointIdAndPosition getNextWaypointId(int waypointId, int startPos, int currentDepth) {
//        int[] column = table[waypointId - 1];
//        for (int i = startPos; i <= column.length; ++i) {
//            int currentWaypointId = column[i - 1];
//            // We are only going on to the right half of the table.
//            if (currentWaypointId > waypointId) {
//                return new WaypointIdAndPosition(currentWaypointId, i, currentDepth);
//            }
//        }
//        return new WaypointIdAndPosition(0, column.length, currentDepth);
//    }
//
//    private WaypointIdAndPosition getPreviousWaypointId(int waypointId, int endPos, int currentDepth) {
//        int[] column = table[waypointId - 1];
//        for (int i = 1; i <= endPos; ++i) {
//            int currentWaypointId = column[i - 1];
//            // We are only going on to the left half of the table.
//            if (currentWaypointId > 0 && currentWaypointId < waypointId) {
//                return new WaypointIdAndPosition(currentWaypointId, i, currentDepth);
//            }
//        }
//        return new WaypointIdAndPosition(0, column.length, currentDepth);
//    }

    private static boolean findDestination(ArrayList<Waypoint> waypoints,
                                           ArrayList<LinkedList<WaypointRouteStep>> routes,
                                           int baseWaypointId, int startWaypointId, int targetWaypointId, float distance) {
        Waypoint startWaypoint = waypoints.get(startWaypointId - 1);
        ArrayList<ENG_Integer> nextWaypointIds = startWaypoint.getNextWaypointIds();
        ArrayList<ENG_Float> nextWaypointDistances = startWaypoint.getNextWaypointDistances();
        boolean foundTargetWaypoint = false;
        for (int i = 0, nextWaypointIdsSize = nextWaypointIds.size(); i < nextWaypointIdsSize; i++) {
            LinkedList<WaypointRouteStep> waypointRouteSteps = routes.get(routes.size() - 1);
            ENG_Integer nextWaypointId = nextWaypointIds.get(i);
            if (nextWaypointId.getValue() < 1) {
                throw new IllegalStateException(nextWaypointId.getValue() + " waypoint id < 1!");
            }
            if (nextWaypointId.getValue() == startWaypoint.getId()) {
                throw new IllegalStateException(nextWaypointId.getValue() + " pointing to the same start waypoint id!");
            }
            // Make sure that we are not pointing back to any of the previous startWaypointId from where we came from.
            boolean foundPreviousWaypoint = false;
            for (WaypointRouteStep waypointRouteStep : waypointRouteSteps) {
                if (waypointRouteStep.previousWaypointId == nextWaypointId.getValue()) {
                    foundPreviousWaypoint = true;
                    break;
                }
            }

            if (waypointRouteSteps.isEmpty() || !foundPreviousWaypoint) {
                float accumulatedDistance = distance + nextWaypointDistances.get(i).getValue();
                waypointRouteSteps.push(new WaypointRouteStep(startWaypointId, nextWaypointId.getValue(), accumulatedDistance));
                if (nextWaypointId.getValue() != targetWaypointId) {
                    foundTargetWaypoint = findDestination(waypoints, routes, baseWaypointId, nextWaypointId.getValue(),
                            targetWaypointId, accumulatedDistance);
                    // We need to go back on the stack to the starting waypoint id.
                    // TODO don't go to the starting waypoint id just move back and try again on a different path. Read below.
                    if (foundTargetWaypoint && baseWaypointId != startWaypointId) {
                        break;
                    }
                } else {
                    foundTargetWaypoint = true;
                    routes.add(new LinkedList<>());
                    // We need to go back on the stack to the starting waypoint id.
                    // TODO don't go to the starting waypoint id just move back and try again on a different path.
                    // Just that we need to make sure that we keep the previous waypoints saved in order to make sure
                    // we are not looping around towards the target.
                    break;
                }
            }
        }
        if (!foundTargetWaypoint) {
            LinkedList<WaypointRouteStep> waypointRouteSteps = routes.get(routes.size() - 1);
            if (!waypointRouteSteps.isEmpty()) {
                waypointRouteSteps.pop();
            }
        }

        return foundTargetWaypoint;
    }

    public void updateTable(ArrayList<Waypoint> waypoints) {
        if (waypoints.isEmpty()) {
            return;
        }
        // This is no longer needed as the paths are updated directly in the depth first solution.
        // Create the table for the direct next waypoints from each waypoint id.
//        for (Waypoint waypoint : waypoints) {
//            ArrayList<ENG_Integer> nextWaypointIds = waypoint.getNextWaypointIds();
//            for (ENG_Integer nextWaypointId : nextWaypointIds) {
//                // Ids should always start from 1.
//                // The positions are offset in the table with -1 but the values in the table are the original +1 ones.
//                table[waypoint.getId() - 1][nextWaypointId.getValue() - 1] = nextWaypointId.getValue();
//            }
//        }
        // Find all paths from every point to every point.
//        int[][] tempTable = allocate(table.length);
//        copyTable(table, tempTable);

        // Another better idea is to go depth first from each waypoint to each possible waypoint.
        // If there are multiple paths find the one with smallest distance.
        Collections.sort(waypoints);
        if (waypoints.get(0).getId() != 1) {
            throw new IllegalArgumentException(waypoints.get(0).getId() + " first waypoint should be 1");
        }
        ArrayList<LinkedList<WaypointRouteStep>> routes = new ArrayList<>();
        int waypointCount = table.length;
        int startWaypointId = 1;
        do {
            int targetWaypointId = startWaypointId + 1;
            if (targetWaypointId > waypointCount) {
                targetWaypointId = 1;
            }
            int itCount = 1;
            do {
                routes.clear();
                routes.add(new LinkedList<>());
                int targetWaypointIdCopy = targetWaypointId;
                // TODO find a non-recursive solution.
                findDestination(waypoints, routes, startWaypointId, startWaypointId, targetWaypointId++, 0.0f);
                if (targetWaypointId > waypointCount) {
                    targetWaypointId = 1;
                }
                LinkedList<WaypointRouteStep> shortestRoute = getWaypointRouteSteps(routes);
                if (shortestRoute == null) {
                    continue;
                }
                System.out.println("shortest route between start id: " + startWaypointId + " target id: " + targetWaypointIdCopy);
                for (int i = shortestRoute.size() - 1; i >= 0; i--) {
                    WaypointRouteStep waypointRouteStep = shortestRoute.get(i);
                    System.out.println(waypointRouteStep);
                }
                int startPos = startWaypointId;
                for (int i = shortestRoute.size() - 1; i >= 0; i--) {
                    WaypointRouteStep waypointRouteStep = shortestRoute.get(i);
                    table[startPos - 1][targetWaypointIdCopy - 1] = waypointRouteStep.currentWaypointId;
                    startPos = waypointRouteStep.currentWaypointId;
                }
            } while ((++itCount) < waypointCount); // If we have 7 waypointCount we only iterate 6 times as we don't need to have base as target.
        } while ((++startWaypointId) <= waypointCount);

        // This kind of works, but the problem is that it doesn't give us the shortest routes, just whatever routes it can find.
        /*LinkedList<WaypointIdAndPosition> waypointIdAndPositions = new LinkedList<>();
        for (int depth = 1; depth < table.length; ++depth) {
            for (int waypointId = 1; waypointId <= table.length; ++waypointId) {
                // Check if each waypoint can jump to the asked depth.
                int currentWaypointId = waypointId;
//                for (int currentDepth = 0; currentDepth < depth; ++currentDepth) {
                // Only do the right side of the diagonal 0 columns.
                // The "stack" for searching for paths goes down.
                for (int column = waypointId + 1; column <= table.length; ++column) {
//                    if (waypointId == column) {
//                        continue;
//                    }
                    int targetWaypointId = table[waypointId - 1][column - 1];
                    if (targetWaypointId > 0) {
                        WaypointIdAndPosition pathWaypointId = new WaypointIdAndPosition(targetWaypointId, column, 0);
                        waypointIdAndPositions.clear();
                        waypointIdAndPositions.push(pathWaypointId);
                        int currentDepth = 1;
                        do {
                            pathWaypointId = getNextWaypointId(pathWaypointId.currentWaypointId, pathWaypointId.lastPosition, currentDepth);
                            if (pathWaypointId.currentWaypointId == 0) {
                                // We couldn't find a path until the end of depth.
                                pathWaypointId = waypointIdAndPositions.pop();
                                pathWaypointId.lastPosition += 1;
                                break;
                            } else {
                                waypointIdAndPositions.push(pathWaypointId);
                            }
                        } while ((++currentDepth) <= depth);
                        if (currentDepth - 1 == depth) {
                            WaypointIdAndPosition waypointIdAndPosition = waypointIdAndPositions.peekFirst();
                            WaypointIdAndPosition current = null;
                            WaypointIdAndPosition previous = null;
                            while ((current = waypointIdAndPositions.peekFirst()) != null) {
                                if (previous != null) {
                                    tempTable[waypointId - 1 + current.currentDepth][waypointIdAndPosition.currentWaypointId - 1] = current.currentWaypointId;
                                }
                                waypointIdAndPositions.pop();
                                previous = current;
                            }
//                            table[waypointId - 1][waypointIdAndPosition.currentWaypointId - 1] = waypointIdAndPosition.currentWaypointId;
                        }
                    }
//                    WaypointIdAndPosition pathWaypointId = new WaypointIdAndPosition(targetWaypointId, column);
//                    do {
//                        int currentDepth = depth;
//                        if (targetWaypointId > 0) {
//                            while ((currentDepth--) >= 0) {
//                                pathWaypointId = getNextWaypointId(pathWaypointId.currentWaypointId, pathWaypointId.lastPosition);
//                                if (pathWaypointId.currentWaypointId == 0) {
//                                    pathWaypointId = waypointIdAndPositions.pop();
//                                    pathWaypointId.lastPosition += 1;
//                                    break;
//                                } else if (pathWaypointId.currentWaypointId == waypointId) {
//                                    // We are going in circles not going in depth.
//                                    // Continue from the next position.
//                                    pathWaypointId.lastPosition += 1;
//                                } else {
//                                    waypointIdAndPositions.push(pathWaypointId);
//                                    pathWaypointId = new WaypointIdAndPosition(pathWaypointId.currentWaypointId, 1);
//                                }
//                            }
//                            if (currentDepth == 0) {
//                                // We reached the required depth.
//                                table[waypointId - 1][pathWaypointId.currentWaypointId - 1] = pathWaypointId.currentWaypointId;
//                            } else {
//                                System.out.println("Bailing from depth: " + currentDepth);
//                            }
//                            waypointIdAndPositions.clear();
//                        }
//                    } while (pathWaypointId.lastPosition != table.length);
//                        if (currentDepth != 0) {
//
//                        } else {
//                            // We reached the required depth.
//                            table[waypointId - 1][pathWaypointId.currentWaypointId - 1] = pathWaypointId.currentWaypointId;
//                        }
                }

//                }
            }
            for (int waypointId = table.length; waypointId >= 1; --waypointId) {
                // Only do the left side of the diagonal 0 columns.
                // The "stack" for searching for paths goes up.
                for (int column = 1; column <= waypointId - 1; ++column) {
                    int targetWaypointId = table[waypointId - 1][column - 1];
                    if (targetWaypointId > 0) {
                        WaypointIdAndPosition pathWaypointId = new WaypointIdAndPosition(targetWaypointId, waypointId - 1, 0);
                        waypointIdAndPositions.clear();
                        waypointIdAndPositions.push(pathWaypointId);
                        int currentDepth = 1;
                        do {
                            pathWaypointId = getPreviousWaypointId(pathWaypointId.currentWaypointId, pathWaypointId.lastPosition, currentDepth);
                            if (pathWaypointId.currentWaypointId == 0) {
                                // We couldn't find a path until the end of depth.
                                pathWaypointId = waypointIdAndPositions.pop();
                                pathWaypointId.lastPosition += 1;
//                                ++currentDepth;
                                break;
                            } else {
                                waypointIdAndPositions.push(pathWaypointId);
                            }
                        } while ((++currentDepth) <= depth);
                        if (currentDepth - 1 == depth) {
                            WaypointIdAndPosition waypointIdAndPosition = waypointIdAndPositions.peekFirst();
                            WaypointIdAndPosition current = null;
                            WaypointIdAndPosition previous = null;
                            while ((current = waypointIdAndPositions.peekFirst()) != null) {
                                if (previous != null) {
                                    tempTable[waypointId - 1 - current.currentDepth][waypointIdAndPosition.currentWaypointId - 1] = current.currentWaypointId;
                                }
                                waypointIdAndPositions.pop();
                                previous = current;
                            }
//                            table[waypointId - 1][waypointIdAndPosition.currentWaypointId - 1] = waypointIdAndPosition.currentWaypointId;
                        }
                    }
                }
            }
        }*/
//        table = tempTable;
        //Fuck it this is too complicated and slow. Ignore what is below.
        // Update the table for the indirect next waypoints from each waypoint id.
        // waypoint 2 can point to 3 which can point to 4. But indirectly that means that waypoint 2 also points to 4 through 3.
//        for (int i = 0; i < table.length - 1; ++i) {
//            for (int j = 0; j < table.length; ++j) {
//                if (i == j) {
//                    continue;
//                }
//                int currentWaypointId = i + 1;
//                int immediateNextWaypoint = table[i][j];
//                if (immediateNextWaypoint > 0) {
//                    for (int k = 0; k < table.length; ++k) {
//                        int forwardOrBackwardWaypoint = table[currentWaypointId][k];
//                        if (forwardOrBackwardWaypoint > 0) {
//                            if (forwardOrBackwardWaypoint != currentWaypointId) {
//                                // We have something going forward so we must also update the previous path.
//                                table[i][/*forwardOrBackwardWaypoint - 1*/immediateNextWaypoint] = immediateNextWaypoint;
//                            } else {
//                                if (i != forwardOrBackwardWaypoint - 1) {
//                                    table[currentWaypointId][k - 1] = currentWaypointId;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        if (WaypointSystem.DEBUG) {
            System.out.println(this);
        }
    }

    private static LinkedList<WaypointRouteStep> getWaypointRouteSteps(ArrayList<LinkedList<WaypointRouteStep>> routes) {
        float minDistance = Float.MAX_VALUE;
        LinkedList<WaypointRouteStep> shortestRoute = null;
        for (LinkedList<WaypointRouteStep> route : routes) {
            if (route.isEmpty()) {
                continue;
            }
            float currentDistance = Objects.requireNonNull(route.peekFirst()).currentDistance;
            if (minDistance > currentDistance) {
                minDistance = currentDistance;
                shortestRoute = route;
            }
        }
        return shortestRoute;
    }

    public int getNextWaypointToDestination(int currentId, int destinationId) {
        if (WaypointSystem.DEBUG) {
            System.out.println("getNextWaypointToDestination currentId: " + currentId + " destinationId: " + destinationId);
            try {
                return table[currentId - 1][destinationId - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return table[currentId - 1][destinationId - 1];
    }

    public ArrayList<ENG_Integer> getWaypointChainToDestination(int currentId, int destinationId) {
        ArrayList<ENG_Integer> ret = new ArrayList<>();
        getWaypointChainToDestination(currentId, destinationId, ret);
        return ret;
    }

    public void getWaypointChainToDestination(int currentId, int destinationId, ArrayList<ENG_Integer> outputList) {
        while (currentId != destinationId) {
            int nextWaypointToDestination = getNextWaypointToDestination(currentId, destinationId);
            if (nextWaypointToDestination != 0) {
                outputList.add(new ENG_Integer(nextWaypointToDestination));
            } else {
                return;
            }
            currentId = nextWaypointToDestination;
        }
    }

    public int[][] getTable() {
        return table;
    }

    public void setTable(int[][] table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "WaypointTable{" +
                "table=" + Arrays.deepToString(table) +
                '}';
    }
}
