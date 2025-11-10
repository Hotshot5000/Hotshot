/*
 * Created by Sebastian Bugiu on 16/02/2025, 11:35
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 11:35
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.levelresource.LevelWaypoint;
import headwayent.blackholedarksun.levelresource.LevelWaypointSector;
import headwayent.blackholedarksun.world.LevelEntity;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;

public class WaypointSystem {

    public static final boolean DEBUG = true;
    private static final WaypointSystem waypointSystem = new WaypointSystem();
    private final HashMap<ENG_Integer, Waypoint> idToWaypointMap = new HashMap<>();
    private final HashMap<ENG_Integer, WaypointSector> idToWaypointSectorMap = new HashMap<>();
    private final ArrayList<WaypointSector> waypointSectors = new ArrayList<>();
    private boolean visible; // Use beacon mesh to show the waypoint physically in the world.
    private boolean initialized;

    private WaypointSystem() {

    }

    public void addWaypointSector(WaypointSector waypointSector) {
        if (initialized) {
            throw new IllegalStateException("WaypointSystem has been initialized. Cannot add more sectors");
        }
        WaypointSector put = idToWaypointSectorMap.put(new ENG_Integer(waypointSector.getId()), waypointSector);
        if (put != null) {
            throw new IllegalArgumentException("WaypointSector: " + waypointSector.getId() + " already added");
        }
        waypointSectors.add(waypointSector);
    }

    public void removeWaypointSector(int id) {
        if (initialized) {
            throw new IllegalStateException("WaypointSystem has been initialized. Cannot remove sectors");
        }
        WaypointSector remove = idToWaypointSectorMap.remove(new ENG_Integer(id));
        if (remove == null) {
            throw new IllegalArgumentException("WaypointSector: " + id + " does not exist");
        }
        waypointSectors.remove(remove);
    }

    public void removeAllWaypointSectors() {
        idToWaypointSectorMap.clear();
        waypointSectors.clear();
        initialized = false;
    }

    public WaypointSector getWaypointSector(int id) {
        return getWaypointSector(new ENG_Integer(id));
    }

    public WaypointSector getWaypointSector(ENG_Integer id) {
        return idToWaypointSectorMap.get(id);
    }

    public Waypoint getWaypointById(int sectorId, int waypointId) {
        return getWaypointSector(sectorId).getWaypoint(waypointId);
    }

    public Waypoint getWaypointById(ENG_Integer sectorId, ENG_Integer waypointId) {
        return getWaypointSector(sectorId).getWaypoint(waypointId);
    }

    public WaypointSector getClosestWaypointSector(ENG_Vector4D position) {
        float minDistance = Float.POSITIVE_INFINITY;
        WaypointSector closestSector = null;
        for (WaypointSector waypointSector : waypointSectors) {
            float distanceToSector = waypointSector.getDistanceToSector(position);
            if (minDistance > distanceToSector) {
                minDistance = distanceToSector;
                closestSector = waypointSector;
            }
        }
        return closestSector;
    }

    public Waypoint getClosestWaypoint(ENG_Vector4D position) {
        return getClosestWaypoint(position,false);
    }

    public Waypoint getClosestWaypoint(ENG_Vector4D position, boolean entranceOrExit) {
        return getClosestWaypointSector(position).getClosestWaypoint(position, entranceOrExit);
    }

    public ENG_Integer getClosestWaypointId(ENG_Vector4D position) {
        return new ENG_Integer(0);
    }

    public int getClosestWaypointIdAsInt(ENG_Vector4D position) {
        return 0;
    }

    public static void createWaypoints(Level level) {
        int sectorId = 1;
        boolean generateSectorIds = false;
        boolean useProvidedSectorIds = false;
        for (LevelWaypointSector waypointSector : level.levelStart.waypointSectors) {
            WaypointSector sector = new WaypointSector(waypointSector.id != -1 ? waypointSector.id : (sectorId++));
            if (generateSectorIds && waypointSector.id != -1) {
                throw new IllegalStateException("WaypointSector id is being generated as one WaypointSector is missing an id");
            }
            if (waypointSector.id == -1) {
                generateSectorIds = true;
            }
            if (useProvidedSectorIds && waypointSector.id == -1) {
                throw new IllegalStateException("WaypointSector id is being used but one WaypointSector is missing an id");
            }
            if (waypointSector.id != -1) {
                useProvidedSectorIds = true;
            }
            sector.setAxisAlignedBox(waypointSector.box);
            sector.setMaxUserCount(waypointSector.maxTotalWaypointAttachmentCount);
            sector.setWaypointTable(waypointSector.waypointTable);
            for (Integer nextSectorId : waypointSector.nextSectorIds) {
                // TODO if it makes sense to connect sectors or just keep them completely separated.
                sector.addNextWaypointSectorId(nextSectorId);
            }

            int waypointId = 1;
            boolean generateWaypointIds = false;
            boolean useProvidedWaypointIds = false;
            for (LevelWaypoint levelWaypoint : waypointSector.waypoints) {
                Waypoint waypoint = new Waypoint(levelWaypoint.id != -1 ? levelWaypoint.id : (waypointId++));
                if (generateWaypointIds && levelWaypoint.id != -1) {
                    throw new IllegalStateException("Waypoint id is being generated as one Waypoint is missing an id");
                }
                if (levelWaypoint.id == -1) {
                    generateWaypointIds = true;
                }
                if (useProvidedWaypointIds && levelWaypoint.id == -1) {
                    throw new IllegalStateException("Waypoint id is being used but one Waypoint is missing an id");
                }
                if (levelWaypoint.id != -1) {
                    useProvidedWaypointIds = true;
                }
                waypoint.setPosition(levelWaypoint.position);
                for (Integer nextId : levelWaypoint.nextIds) {
                    waypoint.addNextWaypointId(nextId);
                }
                waypoint.setRadius(levelWaypoint.radius);
                waypoint.setWeight(levelWaypoint.weight);
                waypoint.setMaxUserCount(levelWaypoint.maxWaypointAttachmentCount);
                if (levelWaypoint.entranceOrExitActive) {
                    waypoint.setEntranceOrExit(levelWaypoint.entranceOrExitDirection,
                            levelWaypoint.entranceOrExitAngle * ENG_Math.DEGREES_TO_RADIANS,
                            levelWaypoint.entranceOrExitMinDistance);
                }
                waypoint.setActive(levelWaypoint.active);

                sector.addWaypoint(waypoint);
            }
            WaypointSystem.getSingleton().addWaypointSector(sector);
        }
        WaypointSystem.getSingleton().initialize();
    }

    private void initialize() {
        for (int i = 0, waypointSectorsSize = waypointSectors.size(); i < waypointSectorsSize - 1; i++) {
            WaypointSector waypointSector = waypointSectors.get(i);
            for (int j = i + 1, sectorsSize = waypointSectors.size(); j < sectorsSize; j++) {
                WaypointSector sector = waypointSectors.get(j);
                if (waypointSector.getBox().intersects(sector.getBox())) {
                    throw new IllegalStateException("sector: " + waypointSector.getId() +
                            " with AABB: " + waypointSector.getBox() + " is intersecting sector: " +
                            sector.getId() + " with AABB: " + sector.getBox());
                }
            }
        }

        for (WaypointSector waypointSector : waypointSectors) {
            waypointSector.initialize();
        }
        initialized = true;
    }

    private void addWaypointBeacons() {
        ArrayList<LevelObject> waypointBeacons = new ArrayList<>();
        for (WaypointSector waypointSector : waypointSectors) {
            for (Waypoint waypoint : waypointSector.getWaypoints()) {
                LevelObject levelObject = new LevelObject();
                levelObject.meshName = "flag_red.mesh";
                levelObject.name = "waypoint_" + waypointSector.getId() + "_" + waypoint.getId();
                levelObject.type = LevelObject.LevelObjectType.WAYPOINT;
                levelObject.position.set(waypoint.getPosition());
                levelObject.waypointSectorId = waypointSector.getId();
                levelObject.waypointId = waypoint.getId();
                waypointBeacons.add(levelObject);
            }

        }
        WorldManagerBase.getSingleton().createEntities(waypointBeacons);
    }

    private void removeWaypointBeacons() {
        // Remove objects from scene.
        WorldManager worldManager = WorldManager.getSingleton();
        for (Long waypointId : worldManager.getWaypointIds()) {
            LevelEntity levelEntity = worldManager.getLevelEntityFromEntityId(waypointId);
            if (levelEntity == null) {
//                throw new NullPointerException(waypointId + " entityId cannot be found!");
                // If we exit to main menu all objects get automatically destroyed.
                continue;
            }
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(levelEntity.getEntity());
            entityProperties.setDestroyed(true);
        }

        worldManager.removeAllWaypointIds();
    }

    /**
     *
     * @param position
     * @return waypoint sector id or -1 if none could be found.
     */
    public int checkPositionInWaypointSector(ENG_Vector4D position) {
        for (WaypointSector waypointSector : waypointSectors) {
            if (waypointSector.checkPositionInsideSector(position)) {
                return waypointSector.getId();
            }
        }
        return -1;
    }

    public void update() {
        if (DEBUG) {
            for (WaypointSector waypointSector : waypointSectors) {
                if (waypointSector.getTotalWaypointUserCount() > waypointSector.getMaxUserCount()) {
                    throw new IllegalStateException("Too many added users to the sector, total users: " +
                            waypointSector.getTotalWaypointUserCount() + " max user count: " +
                            waypointSector.getMaxUserCount());
                }
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                addWaypointBeacons();
            } else {
                removeWaypointBeacons();
            }
        }
        this.visible = visible;
    }

    public boolean isInitialized() {
        return initialized && !waypointSectors.isEmpty();
    }

    public static WaypointSystem getSingleton() {
        return waypointSystem;
    }
}
