/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/20/21, 9:49 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.audio.LevelPlayable;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.levelresource.*;
import headwayent.blackholedarksun.levelresource.LevelEndCond.EndCondType;
import headwayent.blackholedarksun.levelresource.LevelEndCond.EndCond;
import headwayent.blackholedarksun.levelresource.LevelEvent.DelayType;
import headwayent.blackholedarksun.levelresource.LevelObject.LevelObjectBehavior;
import headwayent.blackholedarksun.levelresource.LevelObject.LevelObjectType;
import headwayent.blackholedarksun.levelresource.levelmesh.LevelMesh;
import headwayent.blackholedarksun.levelresource.levelmesh.LevelPortal;
import headwayent.blackholedarksun.levelresource.levelmesh.LevelZone;
import headwayent.blackholedarksun.systems.helper.ai.WaypointTable;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Aabb;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.exception.ENG_ParsingException;
import headwayent.hotshotengine.renderer.ENG_Light;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collection;

public class LevelCompiler extends ENG_AbstractCompiler<LevelBase> {

    private static final String LEVEL = "level";
    private static final String LEVEL_START = "start";
    private static final String LEVEL_END = "end";
    private static final String LEVEL_EVENT = "event";

    private static final String OBJECT = "obj";
    private static final String OBJECT_MESH = "mesh";
    private static final String OBJECT_TYPE = "type";
    private static final String OBJECT_TYPE_PLAYER_SHIP = "player_ship";
    private static final String OBJECT_TYPE_FIGHTER_SHIP = "fighter_ship";
    private static final String OBJECT_TYPE_CARGO_SHIP = "cargo_ship";
    private static final String OBJECT_TYPE_FLAG_RED = "flag_red";
    private static final String OBJECT_TYPE_FLAG_BLUE = "flag_blue";
    private static final String OBJECT_TYPE_ASTEROID = "asteroid";
    private static final String OBJECT_TYPE_CARGO = "cargo";
    private static final String OBJECT_TYPE_STATIC = "static";
    private static final String OBJECT_POSITION = "position";
    private static final String OBJECT_ORIENTATION = "orientation";
    private static final String OBJECT_SPEED = "speed";
    private static final String OBJECT_AI = "ai";
    private static final String OBJECT_FRIENDLY = "friendly";
    private static final String OBJECT_HEALTH = "health";
    private static final String OBJECT_BEHAVIOR = "behavior";
    private static final String OBJECT_BEHAVIOR_NEUTRAL = "neutral";
    private static final String OBJECT_BEHAVIOR_AGGRESIVE = "aggresive";
    private static final String OBJECT_BEHAVIOR_AGGRESSIVE = "aggressive";
    private static final String OBJECT_BEHAVIOR_DEFENSIVE = "defensive";
    private static final String OBJECT_ATTACK = "attack";
    private static final String OBJECT_PRIORITIZE = "prioritize";
    private static final String OBJECT_SCAN_RADIUS = "scan_radius";
    private static final String OBJECT_RADIUS = "radius";
    private static final String OBJECT_INVINCIBLE = "invincible";
    private static final String OBJECT_DESTINATION = "destination";
    private static final String OBJECT_REACH_DESTINATION = "reach_destination";

    private static final String OBJECT_SQUAD = "squad";
    private static final String OBJECT_SQUAD_LEADER = "squad_leader";
    private static final String OBJECT_SQUAD_MIN_DISTANCE = "squad_leader_min_distance";
    private static final String OBJECT_SQUAD_MAX_DISTANCE = "squad_leader_max_distance";
    private static final String OBJECT_SQUAD_NAME = "squad_name";

    private static final String START_SKYBOX = "skybox";
    private static final String START_LIGHT_DIR = "light_dir";
    private static final String START_LIGHT_TYPE = "light_type";
    private static final String START_LIGHT_POWER_SCALE = "light_power_scale";
    private static final String START_LIGHT_DIFFUSE_COLOR = "light_diffuse_color";
    private static final String START_LIGHT_SPECULAR_COLOR = "light_specular_color";
    private static final String START_LIGHT_POSITION = "light_position";
    private static final String START_AMBIENT_LIGHT = "ambient_light_upperhemi_lowerhemi_dir";
    private static final String START_RELOADER_ALLOWED = "reloader";
    private static final String START_SPAWN_POINTS = "spawn_points";
    private static final String START_PLAYER_SHIP_SELECTION = "player_ship_selection";
    private static final String START_WAYPOINT_SECTOR = "waypoint_sector";
    private static final String START_WAYPOINT_SECTOR_EXTENTS_MIN_MAX = "extents_min_max";
    private static final String START_WAYPOINT_SECTOR_ID = "id";
    private static final String START_WAYPOINT_SECTOR_NEXT_SECTOR_ID = "next_sector_id";
    private static final String START_WAYPOINT_SECTOR_MAX_ATTACHMENT_COUNT = "max_attachment_count";
    private static final String START_WAYPOINT_TABLE = "waypoint_table";
    private static final String START_WAYPOINT_TABLE_SIZE = "size";
    private static final String START_WAYPOINT_TABLE_TABLE = "table";
    private static final String START_WAYPOINT = "waypoint";
    private static final String START_WAYPOINT_POSITION = "position";
    private static final String START_WAYPOINT_ID = "id";
    private static final String START_WAYPOINT_RADIUS = "radius";
    private static final String START_WAYPOINT_WEIGHT = "weight";
    private static final String START_WAYPOINT_NEXT_ID = "next_id";
    private static final String START_WAYPOINT_MAX_ATTACHMENTS_COUNT = "max_attachment_count";
    private static final String START_WAYPOINT_ENTRANCE_OR_EXIT = "entrance_or_exit";
    private static final String START_WAYPOINT_ACTIVE = "active";
    private static final String START_PLAY_CUTSCENE = "play_cutscene";
    private static final String START_USE_SKYBOX_DATA_FROM_LEVEL = "use_skybox_data_from_level";
    private static final String START_PLAYER_SHIP_SELECTION_TEAM = "team";
    private static final String START_PLAYER_SHIP_SELECTION_FILE = "file";
    private static final String START_PLAYER_SHIP_SELECTION_NAME = "name";
    private static final String START_LEVEL_MESH = "level_mesh";
    private static final String START_LEVEL_MESH_MESH_NAME = "mesh_name";
    private static final String START_LEVEL_MESH_NODE_NAME = "node_name";
    private static final String START_LEVEL_ZONE = "zone";
    private static final String START_LEVEL_ZONE_TYPE_NAME = "zone_type";
    private static final String START_LEVEL_ZONE_MESH_NAME = "mesh_name";
    private static final String START_LEVEL_ZONE_NODE_NAME = "node_name";
    private static final String START_LEVEL_ZONE_NODE_POSITION = "node_pos";
    private static final String START_LEVEL_PORTAL = "portal";
    private static final String START_LEVEL_PORTAL_TYPE = "type";
    private static final String START_LEVEL_PORTAL_CORNERS = "corners";
    private static final String START_AMBIENT_SOUNDS = "ambient_sounds";
    private static final String START_LEVEL_SOUNDS = "level_sounds";

    private static final String SPAWN_POINT = "spawn_point";
    private static final String SPAWN_POINT_TYPE = "type";
    private static final String SPAWN_POINT_POSITION = "position";
    private static final String SPAWN_POINT_ORIENTATION = "orientation";

    private static final String AMBIENT_SOUND = "ambient_sound";
    private static final String AMBIENT_SOUND_NAME = "sound_name";
    private static final String AMBIENT_PLAYABLE_NAME = "playable_name";
    private static final String AMBIENT_BOX_CENTER = "box_center";
    private static final String AMBIENT_BOX_HALF_SIZE = "box_half_size";
    private static final String AMBIENT_SOUND_POSITION = "sound_position";

    private static final String LEVEL_SOUND = "level_sound";
    private static final String LEVEL_PLAYABLE_NAME = "playable_name";
    private static final String LEVEL_SOUND_POSITION = "sound_position";
    private static final String LEVEL_DOPPLER_FACTOR = "doppler_factor";
    private static final String LEVEL_MAX_SOUND_SPEED = "max_sound_speed";
    private static final String LEVEL_FRONT_VEC = "front_vec";

    private static final String EVENT_PREVIOUS_END_COND = "previous_end_cond";
    private static final String EVENT_PREV_CONDS = "prev_conds";
    private static final String EVENT_PREV_CONDS_NONE = "none";
    private static final String EVENT_DELAY = "delay";
    private static final String EVENT_DELAY_SECS = "secs";
    private static final String EVENT_DELAY_MSECS = "msecs";
    private static final String EVENT_SPAWN = "spawn";
    private static final String EVENT_TEXT = "text";
    private static final String EVENT_EXIT = "exit";
    private static final String EVENT_PLAY_CUTSCENE = "play_cutscene";

    private static final String TEXT_TEXT = "text";
    private static final String TEXT_DURATION = "duration";

    private static final String EVENT_END_COND = "end_cond";
    private static final String EVENT_END_COND_TYPE = "type";
    private static final String EVENT_END_COND_TYPE_WIN = "win";
    private static final String EVENT_END_COND_TYPE_LOSS = "loss";
    private static final String EVENT_END_COND_COND = "cond";
    private static final String EVENT_END_COND_COND_SECS = "secs";
    private static final String EVENT_END_COND_COND_MSECS = "msecs";
    private static final String EVENT_END_COND_COND_DELAY = "delay";
    private static final String EVENT_END_COND_COND_OBJS = "objs";
    private static final String EVENT_END_COND_COND_TYPE = "type";
    private static final String EVENT_END_COND_COND_CUSTOM_EVENT = "custom_event";
    private static final String EVENT_END_COND_COND_TYPE_DESTROYED = "destroyed";
    private static final String EVENT_END_COND_COND_TYPE_TIME_ELAPSED = "time_elapsed";
    private static final String EVENT_END_COND_COND_TYPE_PLAYER_SHIP_DESTINATION_REACHED
            = "player_ship_destination_reached";
    private static final String EVENT_END_COND_COND_TYPE_EXITED = "exited";
    private static final String EVENT_END_COND_COND_TYPE_EXITED_OR_DESTROYED = "exited_or_destroyed";
    private static final String EVENT_END_COND_COND_TYPE_CARGO_SCANNED = "cargo_scanned";
    private static final String EVENT_END_COND_COND_TYPE_SHIP_DESTINATION_REACHED =
            "ship_destination_reached";
    private static final String EVENT_END_COND_COND_TYPE_TEXT_SHOWN = "text_shown";

    private static final String COMPARATOR = "comparator";
    private static final String COMPARATOR_PARAN_OPEN = "(";
    private static final String COMPARATOR_PARAN_CLOSE = ")";
    private static final String COMPARATOR_AND = "and";
    private static final String COMPARATOR_OR = "or";
    private static final String COMPARATOR_XOR = "xor";
    private static final String COMPARATOR_NOT = "not";

    private static final String LEVEL_END_EVENTS = "events";
    private static final String LEVEL_END_IGNORE_LOSS_EVENTS = "ignore_loss_events";
    private static final String OBJECTS = "objs";
    private final boolean multiplayer;

    public LevelCompiler(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    private LevelPlayable parseLevelSound(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelPlayable levelPlayable = new LevelPlayable();
        levelPlayable.setName(s);
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        boolean playableNameSet = false;
        ENG_Vector4D soundPosition = null;
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(LEVEL_PLAYABLE_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelPlayable.setSoundName(s);
                playableNameSet = true;
            } else if (s.equalsIgnoreCase(LEVEL_SOUND_POSITION)) {
                soundPosition = getVector3D(fp0);
                soundPosition.w = 1.0f;
            } else if (s.equalsIgnoreCase(LEVEL_DOPPLER_FACTOR)) {

            } else if (s.equalsIgnoreCase(LEVEL_MAX_SOUND_SPEED)) {

            } else if (s.equalsIgnoreCase(LEVEL_FRONT_VEC)) {

            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (playableNameSet && soundPosition != null) {
            levelPlayable.setPosition(soundPosition);
            return levelPlayable;
        }
        throw new ENG_ParsingException("Level sound does not contain all elements");
    }

    private ArrayList<LevelPlayable> parseLevelSounds(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("Bracket mandatory after " +
                    "level sounds");
        }
        ArrayList<LevelPlayable> sounds = new ArrayList<>();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(LEVEL_SOUND)) {
                sounds.add(parseLevelSound(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return sounds;
    }

    private WorldManagerBase.AmbientPlayable parseAmbientSound(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        WorldManagerBase.AmbientPlayable ambientPlayable = new WorldManagerBase.AmbientPlayable();
        ambientPlayable.setName(s);
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        boolean playableNameSet = false;
        ENG_Vector4D boxCenter = null;
        ENG_Vector4D boxHalfSize = null;
        ENG_Vector4D soundPosition = null;
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(AMBIENT_PLAYABLE_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                ambientPlayable.setSoundName(s);
                playableNameSet = true;
            } else if (s.equalsIgnoreCase(AMBIENT_BOX_CENTER)) {
                boxCenter = getVector3D(fp0);
            } else if (s.equalsIgnoreCase(AMBIENT_BOX_HALF_SIZE)) {
                boxHalfSize = getVector3D(fp0);
            } else if (s.equalsIgnoreCase(AMBIENT_SOUND_POSITION)) {
                soundPosition = getVector3D(fp0);
                soundPosition.w = 1.0f;
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (playableNameSet && boxCenter != null && boxHalfSize != null && soundPosition != null) {
            ambientPlayable.setBox(new ENG_Aabb(new ENG_Vector3D(boxCenter), new ENG_Vector3D(boxHalfSize)));
            ambientPlayable.setPosition(soundPosition);
            return ambientPlayable;
        }
        throw new ENG_ParsingException("Ambient sound does not contain all elements");
    }

    private ArrayList<WorldManagerBase.AmbientPlayable> parseAmbientSounds(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("Bracket mandatory after " +
                    "ambient sounds");
        }
        ArrayList<WorldManagerBase.AmbientPlayable> ambientSounds = new ArrayList<>();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(AMBIENT_SOUND)) {
                ambientSounds.add(parseAmbientSound(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return ambientSounds;
    }

    private LevelSpawnPoint parseSpawnPoint(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelSpawnPoint spawnPoint = new LevelSpawnPoint();
        spawnPoint.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(SPAWN_POINT_TYPE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                spawnPoint.team = ShipData.ShipTeam.getValueOf(s);
            } else if (s.equalsIgnoreCase(SPAWN_POINT_POSITION)) {
                getVector3D(fp0, spawnPoint.position);
            } else if (s.equalsIgnoreCase(SPAWN_POINT_ORIENTATION)) {
                getQuaternionDeg(fp0, spawnPoint.orientation);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return spawnPoint;
    }

    private ArrayList<LevelSpawnPoint> parseSpawnPoints(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("Bracket mandatory after " +
                    "spawn points");
        }
        ArrayList<LevelSpawnPoint> spawnPoints = new ArrayList<>();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(SPAWN_POINT)) {
                spawnPoints.add(parseSpawnPoint(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return spawnPoints;
    }

    private LevelPortal parseLevelPortal(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelPortal levelPortal = new LevelPortal();
        levelPortal.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_LEVEL_PORTAL_CORNERS)) {
                getVector3D(fp0, levelPortal.corners[0]);
                getVector3D(fp0, levelPortal.corners[1]);
                // We assume that the type is always set before setting the corners or else we crash.
                if (levelPortal.portalType == LevelPortal.PortalType.PORTAL_TYPE_QUAD) {
                    getVector3D(fp0, levelPortal.corners[2]);
                    getVector3D(fp0, levelPortal.corners[3]);
                }
            } else if (s.equalsIgnoreCase(START_LEVEL_PORTAL_TYPE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelPortal.portalType = LevelPortal.PortalType.getType(s);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelPortal;
    }

    private LevelZone parseLevelZone(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelZone levelZone = new LevelZone();
        levelZone.zoneName = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_LEVEL_PORTAL)) {
                levelZone.levelPortals.add(parseLevelPortal(fp0));
            } else if (s.equalsIgnoreCase(START_LEVEL_ZONE_MESH_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelZone.meshName = s;
            } else if (s.equalsIgnoreCase(START_LEVEL_ZONE_NODE_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelZone.nodeName = s;
            } else if (s.equalsIgnoreCase(START_LEVEL_ZONE_TYPE_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelZone.zoneTypeName = s;
            } else if (s.equalsIgnoreCase(START_LEVEL_ZONE_NODE_POSITION)) {
                getVector3D(fp0, levelZone.nodePosition);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelZone;
    }

    private LevelMesh parseLevelMesh(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelMesh levelMesh = new LevelMesh();
        levelMesh.levelMeshName = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_LEVEL_ZONE)) {
                levelMesh.levelZones.add(parseLevelZone(fp0));
            } else if (s.equalsIgnoreCase(START_LEVEL_MESH_MESH_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelMesh.meshName = s;
            } else if (s.equalsIgnoreCase(START_LEVEL_MESH_NODE_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelMesh.baseNodeName = s;
            } else if (s.equalsIgnoreCase(START_LEVEL_PORTAL)) {
                levelMesh.levelPortals.add(parseLevelPortal(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelMesh;
    }

    private LevelObject parseLevelObject(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelObject levelObject = new LevelObject();
        levelObject.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        boolean destinationSet = false;
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(OBJECT_MESH)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelObject.meshName = s;
            } else if (s.equalsIgnoreCase(OBJECT_TYPE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(OBJECT_TYPE_PLAYER_SHIP)) {
                    levelObject.type = LevelObjectType.PLAYER_SHIP;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_FIGHTER_SHIP)) {
                    levelObject.type = LevelObjectType.FIGHTER_SHIP;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_CARGO_SHIP)) {
                    levelObject.type = LevelObjectType.CARGO_SHIP;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_FLAG_RED)) {
                    levelObject.type = LevelObjectType.FLAG_RED;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_FLAG_BLUE)) {
                    levelObject.type = LevelObjectType.FLAG_BLUE;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_ASTEROID)) {
                    levelObject.type = LevelObjectType.ASTEROID;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_CARGO)) {
                    levelObject.type = LevelObjectType.CARGO;
                } else if (s.equalsIgnoreCase(OBJECT_TYPE_STATIC)) {
                    levelObject.type = LevelObjectType.STATIC;
                } else {
                    throw new ENG_InvalidFormatParsingException(s + " is an invalid " +
                            "type for a level object");
                }
            } else if (s.equalsIgnoreCase(OBJECT_POSITION)) {
                getVector3D(fp0, levelObject.position);
            } else if (s.equalsIgnoreCase(OBJECT_ORIENTATION)) {
                getQuaternionDeg(fp0, levelObject.orientation);
            } else if (s.equalsIgnoreCase(OBJECT_SPEED)) {
                getVector3D(fp0, levelObject.velocity);
            } else if (s.equalsIgnoreCase(OBJECT_AI)) {
                levelObject.ai = getBoolean(fp0);//(getInt(fp0) == 1);
            } else if (s.equalsIgnoreCase(OBJECT_FRIENDLY)) {
                levelObject.friendly = (getInt(fp0) == 1) ? ShipData.ShipTeam.HUMAN : ShipData.ShipTeam.ALIEN;
            } else if (s.equalsIgnoreCase(OBJECT_HEALTH)) {
                levelObject.health = getInt(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_BEHAVIOR)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(OBJECT_BEHAVIOR_NEUTRAL)) {
                    levelObject.behavior = LevelObjectBehavior.NEUTRAL;
                } else if (s.equalsIgnoreCase(OBJECT_BEHAVIOR_AGGRESIVE) || s.equalsIgnoreCase(OBJECT_BEHAVIOR_AGGRESSIVE)) {
                    levelObject.behavior = LevelObjectBehavior.AGGRESSIVE;
                } else if (s.equalsIgnoreCase(OBJECT_BEHAVIOR_DEFENSIVE)) {
                    levelObject.behavior = LevelObjectBehavior.DEFENSIVE;
                } else {
                    throw new ENG_InvalidFormatParsingException(s + " not a valid " +
                            "behavior type");
                }
            } else if (s.equalsIgnoreCase(OBJECT_ATTACK)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelObject.attackName = s;
            } else if (s.equalsIgnoreCase(OBJECT_PRIORITIZE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelObject.prioritizeList.add(s);
            } else if (s.equalsIgnoreCase(OBJECT_SCAN_RADIUS)) {
                levelObject.scanRadius = getFloat(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_RADIUS)) {
                levelObject.radius = getFloat(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_INVINCIBLE)) {
                levelObject.invincible = getFloat(fp0) == 1;
            } else if (s.equalsIgnoreCase(OBJECT_DESTINATION)) {
                levelObject.destination.set(getVector3D(fp0));
                destinationSet = true;
            } else if (s.equalsIgnoreCase(OBJECT_REACH_DESTINATION)) {
                levelObject.reachDestination = getFloat(fp0) == 1;
            } else if (s.equalsIgnoreCase(OBJECT_SQUAD)) {
                levelObject.squadNum = getInt(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_SQUAD_LEADER)) {
                levelObject.squadLeader = getBoolean(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_SQUAD_MIN_DISTANCE)) {
                levelObject.squadMinDistance = getFloat(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_SQUAD_MAX_DISTANCE)) {
                levelObject.squadMaxDistance = getFloat(fp0);
            } else if (s.equalsIgnoreCase(OBJECT_SQUAD_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelObject.squadName = s;
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (levelObject.reachDestination && !destinationSet) {
            throw new ENG_InvalidFormatParsingException("Object told to reach destination "
                    + "without setting the destination");
        }
        return levelObject;
    }

    /** @noinspection deprecation*/
    private LevelStart parseLevelStart(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after start mandatory");
        }
        LevelStart levelStart = multiplayer ? new MultiplayerClientLevelStart() : new LevelStart();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_SKYBOX)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                levelStart.skyboxName = s;
            } else if (s.equalsIgnoreCase(START_LIGHT_DIR)) {
                getVector3D(fp0, levelStart.lightDir);
            } else if (s.equalsIgnoreCase(START_LIGHT_TYPE)) {
                levelStart.lightType = ENG_Light.LightTypes.getType(ENG_CompilerUtil.getNextWord(fp0));
            } else if (s.equalsIgnoreCase(START_LIGHT_POWER_SCALE)) {
                levelStart.lightPowerScale = getFloat(fp0);
            } else if (s.equalsIgnoreCase(START_LIGHT_DIFFUSE_COLOR)) {
                getColourValue(fp0, levelStart.lightDiffuseColor);
            } else if (s.equalsIgnoreCase(START_LIGHT_SPECULAR_COLOR)) {
                getColourValue(fp0, levelStart.lightSpecularColor);
            } else if (s.equalsIgnoreCase(START_LIGHT_POSITION)) {
                getVector3D(fp0, levelStart.lightPos);
            } else if (s.equalsIgnoreCase(START_AMBIENT_LIGHT)) {
                getColourValue(fp0, levelStart.ambientLightUpperHemisphere);
                getColourValue(fp0, levelStart.ambientLightLowerHemisphere);
                getVector3D(fp0, levelStart.ambientLighthemisphereDir);
            } else if (s.equalsIgnoreCase(START_RELOADER_ALLOWED)) {
                levelStart.reloaderAllowed = getBoolean(fp0);//getInt(fp0) != 0;
            } else if (s.equalsIgnoreCase(START_PLAYER_SHIP_SELECTION)) {
                ArrayList<LevelPlayerShipSelection> playerShipSelectionList = parsePlayerShipSelection(fp0);
                for (LevelPlayerShipSelection playerShipSelection : playerShipSelectionList) {
                    levelStart.playerShipSelectionMap.put(playerShipSelection.team, playerShipSelection);
                    for (String shipName : playerShipSelection.shipNameList) {
                        LevelObject levelObject = new LevelObject();
                        levelObject.name = shipName;
                        // This type has been added just so we can differentiate in the load resources in WorldManagerBase.
                        levelObject.type = LevelObjectType.PLAYER_SHIP_SELECTION;
                        levelStart.playerShipSelectionObjects.add(levelObject);
                    }
                }
            } else if (s.equalsIgnoreCase(START_WAYPOINT_SECTOR)) {
                levelStart.waypointSectors.add(parseWaypointSector(fp0));
            } else if (s.equalsIgnoreCase(START_PLAY_CUTSCENE)) {
                levelStart.cutsceneName = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(levelStart.cutsceneName);
            } else if (s.equalsIgnoreCase(START_USE_SKYBOX_DATA_FROM_LEVEL)) {
                levelStart.useSkyboxDataFromLevel = getInt(fp0) == 1;
            } else if (s.equalsIgnoreCase(OBJECT)) {
                levelStart.startObjects.add(parseLevelObject(fp0));
            } else if (s.equalsIgnoreCase(START_SPAWN_POINTS)) {
                levelStart.spawnPoints.addAll(parseSpawnPoints(fp0));
            } else if (s.equalsIgnoreCase(START_LEVEL_MESH)) {
                levelStart.levelMesh = parseLevelMesh(fp0);
            } else if (s.equalsIgnoreCase(START_AMBIENT_SOUNDS)) {
                levelStart.ambientSounds.addAll(parseAmbientSounds(fp0));
            } else if (s.equalsIgnoreCase(START_LEVEL_SOUNDS)) {
                levelStart.sounds.addAll(parseLevelSounds(fp0));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (multiplayer) {
            ((MultiplayerClientLevelStart) levelStart).initializeMaps();
        }
        return levelStart;
    }

    private LevelWaypoint parseWaypoint(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_WAYPOINT + " mandatory");
        }
        LevelWaypoint levelWaypoint = new LevelWaypoint();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_WAYPOINT_POSITION)) {
                levelWaypoint.position.set(getVector3D(fp0));
            } else if (s.equalsIgnoreCase(START_WAYPOINT_ID)) {
                levelWaypoint.id = getInt(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_RADIUS)) {
                levelWaypoint.radius = getFloat(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_WEIGHT)) {
                levelWaypoint.weight = getFloat(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_NEXT_ID)) {
                levelWaypoint.nextIds.add(getInt(fp0));
            } else if (s.equalsIgnoreCase(START_WAYPOINT_MAX_ATTACHMENTS_COUNT)) {
                levelWaypoint.maxWaypointAttachmentCount = getInt(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_ENTRANCE_OR_EXIT)) {
                levelWaypoint.entranceOrExitDirection = getVector3D(fp0);
                levelWaypoint.entranceOrExitAngle = getFloat(fp0);
                levelWaypoint.entranceOrExitMinDistance = getFloat(fp0);
                levelWaypoint.entranceOrExitActive = true;
            } else if (s.equalsIgnoreCase(START_WAYPOINT_ACTIVE)) {
                levelWaypoint.active = getBoolean(fp0);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelWaypoint;
    }

    private void parseWaypointTableValues(DataInputStream fp0, int[][] table) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_WAYPOINT_TABLE_TABLE + " mandatory");
        }
        int size = table.length;
        for (int i = 0; i < size; ++i) {
            checkWaypointTableStartOrEnd(fp0, "[", " should be [");
            for (int j = 0; j < size; ++j) {
                table[i][j] = getInt(fp0);
            }
            checkWaypointTableStartOrEnd(fp0, "]", " should be ]");
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
            decrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket at the end of " + START_WAYPOINT_TABLE_TABLE + " mandatory");
        }
    }

    private static void checkWaypointTableStartOrEnd(DataInputStream fp0, String anotherString, String x) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (!s.equalsIgnoreCase(anotherString)) {
            throw new ENG_InvalidFormatParsingException(s + x);
        }
    }

    private int[][] parseWaypointTable(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_WAYPOINT_TABLE + " mandatory");
        }
        int[][] table = null;
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_WAYPOINT_TABLE_SIZE)) {
                int size = getInt(fp0);
                table = WaypointTable.allocate(size);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_TABLE_TABLE)) {
                if (table == null) {
                    throw new NullPointerException("size must be first use to allocate the table size");
                }
                parseWaypointTableValues(fp0, table);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return table;
    }

    private LevelWaypointSector parseWaypointSector(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_WAYPOINT_SECTOR + " mandatory");
        }
        LevelWaypointSector levelWaypointSector = new LevelWaypointSector();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(START_WAYPOINT_SECTOR_EXTENTS_MIN_MAX)) {
                levelWaypointSector.box.setMin(getVector3D(fp0));
                levelWaypointSector.box.setMax(getVector3D(fp0));
            } else if (s.equalsIgnoreCase(START_WAYPOINT_SECTOR_ID)) {
                levelWaypointSector.id = getInt(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT_SECTOR_NEXT_SECTOR_ID)) {
                levelWaypointSector.nextSectorIds.add(getInt(fp0));
            } else if (s.equalsIgnoreCase(START_WAYPOINT_SECTOR_MAX_ATTACHMENT_COUNT)) {
                levelWaypointSector.maxTotalWaypointAttachmentCount = getInt(fp0);
            } else if (s.equalsIgnoreCase(START_WAYPOINT)) {
                levelWaypointSector.waypoints.add(parseWaypoint(fp0));
            } else if (s.equalsIgnoreCase(START_WAYPOINT_TABLE)) {
                levelWaypointSector.waypointTable = parseWaypointTable(fp0);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelWaypointSector;
    }

    private ArrayList<LevelPlayerShipSelection> parsePlayerShipSelection(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_PLAYER_SHIP_SELECTION + " mandatory");
        }
        String dir;
        String currentDir = "";
        ArrayList<LevelPlayerShipSelection> playerShipSelections = new ArrayList<>();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            dir = ENG_CompilerUtil.checkDirChange(s, fp0);
            if (dir != null) {
                currentDir = dir;
            }
            if (s.equalsIgnoreCase(START_PLAYER_SHIP_SELECTION_TEAM)) {
                playerShipSelections.add(parseShipSelectionTeam(fp0, currentDir));
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return playerShipSelections;
    }

    private LevelPlayerShipSelection parseShipSelectionTeam(DataInputStream fp0, String currentDir) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        String dir;
        LevelPlayerShipSelection playerShipSelection = new LevelPlayerShipSelection();
        playerShipSelection.team = ShipData.ShipTeam.getValueOf(s);
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after " + START_PLAYER_SHIP_SELECTION_TEAM + " mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            dir = ENG_CompilerUtil.checkDirChange(s, fp0);
            if (dir != null) {
                currentDir = dir;
            }
            if (s.equalsIgnoreCase(START_PLAYER_SHIP_SELECTION_FILE)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                playerShipSelection.shipLoaderFilenameList.add(currentDir + s);
            } else if (s.equalsIgnoreCase(START_PLAYER_SHIP_SELECTION_NAME)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                playerShipSelection.shipNameList.add(s);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return playerShipSelection;
    }

    private LevelEnd parseLevelEnd(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after end mandatory");
        }
        LevelEnd levelEnd = new LevelEnd();
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(LEVEL_END_EVENTS)) {
                int eventNum = getInt(fp0);
                for (int i = 0; i < eventNum; ++i) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    // Should these go anywhere??
                    String event = s;
                    levelEnd.endEventList.add(event);
                }
            } else if (s.equalsIgnoreCase(LEVEL_END_IGNORE_LOSS_EVENTS)) {
                int eventNum = getInt(fp0);
                for (int i = 0; i < eventNum; ++i) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    // Should these go anywhere??
                    String event = s;
                    levelEnd.endEventIgnoreLossList.add(event);
                }
            } else if (s.equalsIgnoreCase(COMPARATOR)) {
                parseComparator(fp0, levelEnd.endEvents);
                checkParanLevel();
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        return levelEnd;
    }

    private LevelEvent parseLevelEvent(DataInputStream fp0) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelEvent levelEvent = multiplayer ? new MultiplayerClientLevelEvent() : new LevelEvent();
        levelEvent.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("bracket after event mandatory");
        }
        while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
            if (s.equalsIgnoreCase(EVENT_PREVIOUS_END_COND)) {
                parsePrevEndCond(fp0, levelEvent);
            } else if (s.equalsIgnoreCase(EVENT_DELAY)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(EVENT_DELAY_SECS)) {
                    levelEvent.delayType = DelayType.SECS;
                } else if (s.equalsIgnoreCase(EVENT_DELAY_MSECS)) {
                    levelEvent.delayType = DelayType.MSECS;
                } else {
                    throw new ENG_InvalidFormatParsingException("Expected secs or" +
                            " msecs as a delay type");
                }
                levelEvent.delay = getInt(fp0);
            } else if (s.equalsIgnoreCase(EVENT_SPAWN)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(BRACKET_OPEN)) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException("bracket expected " +
                            "after opening spawn");
                }
                while (true) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(OBJECT)) {
                        levelEvent.spawn.add(parseLevelObject(fp0));
                    } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                        decrementBracketLevel();
                        break;
                    } else {
                        throw new ENG_InvalidFormatParsingException(s + " invalid. " +
                                "Only objs and } allowed");
                    }
                }
            } else if (s.equalsIgnoreCase(EVENT_TEXT)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(BRACKET_OPEN)) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException("bracket expected " +
                            "after opening text");
                }
                while (true) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(TEXT_TEXT)) {
                        levelEvent.textShown = ENG_CompilerUtil.getNextWord(fp0);
                    } else if (s.equalsIgnoreCase(TEXT_DURATION)) {
                        levelEvent.textShownDuration = getLong(fp0);
                    } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                        decrementBracketLevel();
                        break;
                    } else {
                        throw new ENG_InvalidFormatParsingException(s + " invalid. " +
                                "Only text, duration or } allowed");
                    }
                }
            } else if (s.equalsIgnoreCase(EVENT_EXIT)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(BRACKET_OPEN)) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException("bracket expected " +
                            "after selecting exit event");
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(OBJECTS)) {
                    int objs = getInt(fp0);
                    for (int i = 0; i < objs; ++i) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        levelEvent.exitObjects.add(s);
                    }
                } else {
                    throw new ENG_InvalidFormatParsingException("only objs allowed " +
                            "int exit brackets");
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException("} mandatory after " +
                            "exiting an event exit block");
                }
            } else if (s.equalsIgnoreCase(EVENT_END_COND)) {
                parseLevelEndCond(fp0, levelEvent);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
        if (multiplayer) {
            ((MultiplayerClientLevelEvent) levelEvent).initializeMaps();
        }
        return levelEvent;
    }

    private void parsePrevEndCond(DataInputStream fp0,
                                  LevelEvent levelEvent) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("{ expected");
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(EVENT_PREV_CONDS_NONE)) {

            } else if (s.equalsIgnoreCase(EVENT_PREV_CONDS)) {
                int paramNum = getInt(fp0);
                for (int i = 0; i < paramNum; ++i) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    // Put them somewhere??
                    String condition = s;
                    levelEvent.prevCondList.add(condition);
                }
            } else if (s.equalsIgnoreCase(COMPARATOR)) {
                parseComparator(fp0, levelEvent.prevCondEndRoot);
                checkParanLevel();
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
    }

    private enum EndType {
        WIN, LOSS
    }

    private void parseLevelEndCond(DataInputStream fp0,
                                   LevelEvent levelEvent) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        LevelEndCond endCond = new LevelEndCond();
        levelEvent.endCond = endCond;
        endCond.name = s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException("{ expected not " +
                    s);
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(EVENT_END_COND_TYPE)) {
                parseCond(fp0, endCond, levelEvent);
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }
        }
    }

    private void parseCond(DataInputStream fp0, LevelEndCond endCond, LevelEvent levelEvent) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
//	EndCond cond = new EndCond();
        EndType endType;
        if (s.equalsIgnoreCase(EVENT_END_COND_TYPE_WIN)) {
            //	endCond.winList.add(cond);
            endType = EndType.WIN;
        } else if (s.equalsIgnoreCase(EVENT_END_COND_TYPE_LOSS)) {
            //	endCond.lossList.add(cond);
            endType = EndType.LOSS;
        } else {
            throw new ENG_InvalidFormatParsingException("Only " +
                    "allowed win and loss types.");
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(BRACKET_OPEN)) {
            incrementBracketLevel();
        } else {
            throw new ENG_InvalidFormatParsingException(s + " is invalid." +
                    " { expected");
        }
        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(EVENT_END_COND_COND)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                EndCond cond = new EndCond();
                cond.name = s;
                switch (endType) {
                    case WIN:
                        endCond.winList.add(cond);
                        break;
                    case LOSS:
                        endCond.lossList.add(cond);
                        break;

                    default:
                        // Should never get here
                        throw new IllegalArgumentException();
                }
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                if (s.equalsIgnoreCase(BRACKET_OPEN)) {
                    incrementBracketLevel();
                } else {
                    throw new ENG_InvalidFormatParsingException("{ expected" +
                            " after defining an cond in end type");
                }
                while (true) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_DESTROYED)) {
                            cond.type = EndCondType.DESTROYED;
                        } else if (s.equalsIgnoreCase(
                                EVENT_END_COND_COND_TYPE_TIME_ELAPSED)) {
                            cond.type = EndCondType.TIME_ELAPSED;
                        } else if (s.equalsIgnoreCase(
                                EVENT_END_COND_COND_TYPE_PLAYER_SHIP_DESTINATION_REACHED)) {
                            cond.type = EndCondType.PLAYER_SHIP_DESTINATION_REACHED;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_EXITED)) {
                            cond.type = EndCondType.EXITED;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_CARGO_SCANNED)) {
                            cond.type = EndCondType.CARGO_SCANNED;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_SHIP_DESTINATION_REACHED)) {
                            cond.type = EndCondType.SHIP_DESTINATION_REACHED;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_EXITED_OR_DESTROYED)) {
                            cond.type = EndCondType.EXITED_OR_DESTROYED;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_TYPE_TEXT_SHOWN)) {
                            cond.type = EndCondType.TEXT_SHOWN;
                        } else {
                            throw new ENG_InvalidFormatParsingException(s + " is an " +
                                    "invalid end condition type");
                        }
                    } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_OBJS)) {
                        if (cond.objects == null) {
                            cond.objects = new ArrayList<>();
                        }
                        int numObjs = getInt(fp0);
                        for (int i = 0; i < numObjs; ++i) {
                            s = ENG_CompilerUtil.getNextWord(fp0);
                            checkNull(s);
                            cond.objects.add(s);
                        }
                    } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_SECS)) {
                        cond.delayType = DelayType.SECS;
                        cond.secs = getInt(fp0);
                    } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_MSECS)) {
                        cond.delayType = DelayType.MSECS;
                        cond.secs = getInt(fp0);
                    } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_DELAY)) {

                        s = ENG_CompilerUtil.getNextWord(fp0);
                        checkNull(s);
                        if (s.equalsIgnoreCase(EVENT_END_COND_COND_SECS)) {
                            cond.objectiveAchievedDelayType = DelayType.SECS;
                        } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_MSECS)) {
                            cond.objectiveAchievedDelayType = DelayType.MSECS;
                        } else {
                            throw new ENG_InvalidFormatParsingException(
                                    "After delay you must either have secs or msecs");
                        }
                        cond.objectiveAchievedDelaySecs = getLong(fp0);
                    } else if (s.equalsIgnoreCase(EVENT_END_COND_COND_CUSTOM_EVENT)) {
                        s = ENG_CompilerUtil.getNextWord(fp0);
                        LevelEventValidatorFactory levelEventValidatorFactory =
                                LevelEvent.levelEventValidatorFactoryMap.get(s);
                        if (levelEventValidatorFactory == null) {
                            throw new NullPointerException(s + " is not a valid level event validator factory!");
                        }
                        ArrayList<String> paramList = new ArrayList<>();
                        if (levelEventValidatorFactory.readAhead() > 0) {
                            for (int i = 0; i < levelEventValidatorFactory.readAhead(); ++i) {
                                paramList.add(ENG_CompilerUtil.getNextWord(fp0));
                            }
                        }
                        LevelEventValidator levelEventValidator = levelEventValidatorFactory.createLevelEventValidator(paramList);
                        levelEvent.levelEventValidatorList.add(levelEventValidator);
                    } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                        decrementBracketLevel();
                        break;
                    }
                }

            } else if (s.equalsIgnoreCase(COMPARATOR)) {
                switch (endType) {
                    case WIN:
                        parseComparator(fp0, endCond.winNode);
                        break;
                    case LOSS:
                        parseComparator(fp0, endCond.lossNode);
                        break;
                    default:
                        // Should never get here
                        throw new IllegalArgumentException();
                }
                checkParanLevel();
            } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                decrementBracketLevel();
                break;
            }

        }
    }

    private void checkParanLevel() {
        if (paranLevel != 0) {
            throw new ENG_InvalidFormatParsingException("paranLevel should be 0. " +
                    "Current value is " + paranLevel);
        }
    }

    private int paranLevel;

    private void parseComparator(DataInputStream fp0, ComparatorNode node) {
        String s;
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(COMPARATOR_PARAN_OPEN)) {
            ++paranLevel;
        } else {
            throw new ENG_InvalidFormatParsingException(s + " is invalid. " +
                    "Parantheses expected");
        }
        s = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(s);
        if (s.equalsIgnoreCase(COMPARATOR_AND)) {
            node.op = ComparatorOperator.AND;
        } else if (s.equalsIgnoreCase(COMPARATOR_OR)) {
            node.op = ComparatorOperator.OR;
        } else if (s.equalsIgnoreCase(COMPARATOR_XOR)) {
            node.op = ComparatorOperator.XOR;
        } else if (s.equalsIgnoreCase(COMPARATOR_NOT)) {
            node.op = ComparatorOperator.NOT;
        } else {
            throw new ENG_InvalidFormatParsingException(s + " is invalid. " +
                    "Logic operator expected");
        }

        while (true) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            // Now we either have an operand or a new node
            if (s.equalsIgnoreCase(COMPARATOR_PARAN_OPEN)) {
                ENG_CompilerUtil.setLookAhead(s);
                ComparatorNode newNode = new ComparatorNode();
                parseComparator(fp0, newNode);
                initLeaves(node);
                node.leaves.add(newNode);
            } else if (s.equalsIgnoreCase(COMPARATOR_PARAN_CLOSE)) {
                if ((--paranLevel) < 0) {
                    throw new ENG_InvalidFormatParsingException("Parantheses do not" +
                            " match");
                }
                break;
            } else {
                ComparatorNode newNode = new ComparatorNode();
                newNode.s = s;
                initLeaves(node);
                node.leaves.add(newNode);
            }
        }
    }

    private void initLeaves(ComparatorNode node) {
        if (node.leaves == null) {
            node.leaves = new ArrayList<>();
        }
    }

    public LevelBase compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            LevelBase level = multiplayer ? new MultiplayerClientLevel() : new Level();
            if (s.equalsIgnoreCase(LEVEL)) {
                s = ENG_CompilerUtil.getNextWord(fp0);
                checkNull(s);
                level.name = s;
            } else {
                throw new ENG_InvalidFormatParsingException("No level found");
            }
            s = ENG_CompilerUtil.getNextWord(fp0);
            checkNull(s);
            if (s.equalsIgnoreCase(BRACKET_OPEN)) {
                incrementBracketLevel();
            } else {
                throw new ENG_InvalidFormatParsingException("Bracket mandatory after " +
                        "level name");
            }
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(LEVEL_START)) {
                    level.setLevelStart(parseLevelStart(fp0));
                } else if (s.equalsIgnoreCase(LEVEL_END)) {
                    level.levelEnd = parseLevelEnd(fp0);
                } else if (s.equalsIgnoreCase(LEVEL_EVENT)) {
                    level.addLevelEvent(parseLevelEvent(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return level;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }
}
