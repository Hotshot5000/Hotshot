/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/5/21, 8:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.gamestatedebugger;

import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameUDP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.net.registeredclasses.JoinServerConnectionRequest;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionRequest;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionResponse;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

import java.util.ArrayList;

/**
 * Created by sebas on 22.09.2015.
 */
public class FrameInterval extends ENG_FrameInterval {

    public static final String ENEMY_SEEK_RAND = "enemy_seek_rand ";
    public static final String NO_ENEMY_DIRECTION_CHANGE_CHANCE = "no_enemy_direction_change_chance ";
    public static final String NO_ENEMY_DIRECTION_CHANGE_CHANCE_DIR = "no_enemy_direction_change_chance_dir ";
    public static final String PARTICLE_LAUNCH_RAND = "particle_launch_rand ";
    public static final String TRACKING_MISSILE_MINIMUM_DISTANCE_RAND = "tracking_missile_minimum_distance_rand ";
    public static final String FOLLOW_PLAYER_TO_SEEK_PLAYER_RAND = "follow_player_to_seek_player_rand ";
    public static final String CHECK_TARGETED_CHANCE = "check_targeted_chance ";
    public static final String SPECIAL_WEAPON_CHANCE = "special_weapon_chance ";
    public static final String MIN_EVASION_DISTANCE = "min_evasion_distance ";
    public static final String MIN_EVASION_DISTANCE_INVERT = "min_evasion_distance_invert ";
    public static final String COLLISION_RESPONSE_DIR = "collision_response ";
    public static final String EVADE_HIT_DIR = "evade_hit_dir ";
    public static final String DEMO_SHIP_SPAWN_CHANCE = "demo_ship_spawn_chance";
    public static final String PATROLING_ROTATION_TIME = "patroling_rotation_time ";
    public static final String CHASING_MISSILE_SOUND_DELAY = "chasing_missile_sound_delay";
    public static final String AFTERBURNER_TIME = "afterburner_time";
    public static final String COUNTERMEASURE_TIME = "countermeasure_time";
    public static final String FIRE_WAITING_TIME = "fire_waiting_time";
    public static final String RESUME_GAME_KEY_CODE = "resume_game_key_code";
    public static final String RELOADER_TIME_BETWEEN_RELOADING_UNITS = "reloader_time_between_reloading_units";
    public static final String LEVEL_EVENT_START_TIME = "level_event_start_time ";
    public static final String CUTSCENE_OBJECT_EVENT_START_TIME = "level_object_event_start_time ";
    public static final String CUTSCENE_CAMERA_EVENT_START_TIME = "level_camera_event_start_time ";
    public static final String CUTSCENE_CAMERA_ATTACH_EVENT_START_TIME = "level_camera_attach_event_start_time ";
    public static final String CUTSCENE_CAMERA_DETACH_EVENT_START_TIME = "level_camera_detach_event_start_time ";
    public static final String CUTSCENE_PARALLEL_EVENT_START_TIME = "level_parallel_event_start_time ";
    public static final String CUTSCENE_OBJECT_EVENT_END_TIME = "level_object_event_end_time ";
    public static final String CUTSCENE_OBJECT_EVENT_COMPLETION_TIME = "level_object_event_completion_time ";
    public static final String CUTSCENE_CAMERA_EVENT_END_TIME = "level_camera_event_end_time ";
    public static final String CUTSCENE_CAMERA_EVENT_LOOK_AT_TIME = "level_camera_event_look_at_time ";
    public static final String CUTSCENE_CAMERA_ATTACH_EVENT_END_TIME = "level_camera_attach_event_end_time ";
    public static final String CUTSCENE_CAMERA_DETACH_EVENT_END_TIME = "level_camera_detach_event_end_time ";
    public static final String CUTSCENE_PARALLEL_EVENT_END_TIME = "level_parallel_event_end_time ";
    public static final String LEVEL_END_TIME_LAPSED = "level_end_time_lapsed ";
    public static final String LEVEL_END_TEXT_SHOWN = "level_end_text_shown ";
    public static final String SHIP_AFTERBURNER_TIME = "ship_afterburner_time ";
    public static final String OBJECTIVE_ACHIEVED_DELAY = "textfield_key_code_delay ";
    public static final String UPDATE_COLLISION_TIMED_DAMAGE = "update_collision_timed_damage ";
    public static final String DEBRIS_TRACKING_DELAY = "debris_tracking_delay ";
    public static final String DEBRIS_RADIUS_RANDOM_X = "debris_radius_random_x ";
    public static final String DEBRIS_RADIUS_RANDOM_Y = "debris_radius_random_y ";
    public static final String DEBRIS_RADIUS_RANDOM_Z = "debris_radius_random_z ";
    public static final String DEBRIS_RADIUS_RANDOM_IMPULSE_X = "debris_radius_random_impulse_x ";
    public static final String DEBRIS_RADIUS_RANDOM_IMPULSE_Y = "debris_radius_random_impulse_y ";
    public static final String DEBRIS_RADIUS_RANDOM_IMPULSE_Z = "debris_radius_random_impulse_z ";
    public static final String DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_X = "debris_radius_random_torque_impulse_x ";
    public static final String DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_Y = "debris_radius_random_torque_impulse_y ";
    public static final String DEBRIS_RADIUS_RANDOM_TORQUE_IMPULSE_Z = "debris_radius_random_torque_impulse_z ";
    public static final String DEBRIS_RANDOM_SELECTION = "debris_random_selection ";
    public static final String PROJECTILE_TRACKING_DELAY = "projectile_tracking_delay ";
    public static final String EVADE_COLLISION_TIME = "evade_collision_time ";
    public static final String MAX_PROJECTILES_LAUNCHED_DELAY = "max_projectiles_launched_delay ";
    public static final String WEAPON_ENEMY_SELECTION_TIME = "weapon_enemy_selection_time ";
    public static final String WEAPON_COOLDOWN_TIME = "weapon_cooldown_time ";
    public static final String AI_COUNTERMEASURE_TIME = "ai_countermeasure_time ";
    public static final String COLLISION_RESPONSE_ACCELERATION_TIME = "collision_response_acceleration_time ";
    public static final String EVASION_HIT_TIME = "evasion_hit_time ";
    public static final String CURRENT_ENEMY_SELECTION_TIME = "current_enemy_selection_time ";
    public static final String CARGO_SCAN_TIME = "password_char_time ";
    public static final String SET_AFTERBURNER_ACTIVE = "set_afterburner_active ";
    public static final String UPDATE_COLLISION_PLAYER_SHIP_HIT_ANIMATION = "update_collision_player_ship_hit_animation";
    public static final String SOUND_SHOULD_PLAY_SOUND = "should_play_sound ";
    public static final String COUNTER_MEASURE_EXPIRATION_TIME = "countermeasure_expiration_time ";
    public static final String HUD_VIBRATION_TIME = "hud_vibration_time ";


    public static class GameFrameIntervalFactory extends FrameIntervalFactory {

        @Override
        public ENG_FrameInterval createFrameInterval(long intervalNum) {
            return new FrameInterval(intervalNum);
        }
    }

    public enum ShipType {
        PLAYER, AI, NETWORK
    }

    public static final String SKYBOX_RAND = "skyboxCreationRand";
    public static final String ASTEROID_CREATE_ENTITY = "asteroid_createEntity ";
    public static final String DEBRIS_HIT_SOUND = "debris_hit_sound_onContactStarted ";
    public static final String DESTRUCTION_SOUND_CREATE_ENTITY = "destruction_sound_createEntity ";
    public static final String CARGO_DESTRUCTION_SOUND_CREATE_ENTITY = "cargoShip_destruction_sound_createEntity ";
    public static final String PIRANHA_CREATE_PROJ_RAND_X_POS = "piranha_create_projectile_rand_x_pos ";
    public static final String PIRANHA_CREATE_PROJ_RAND_Y_POS = "piranha_create_projectile_rand_y_pos ";
    public static final String PIRANHA_CREATE_PROJ_RAND_X_ROT = "piranha_create_projectile_rand_x_rot ";
    public static final String PIRANHA_CREATE_PROJ_RAND_Y_ROT = "piranha_create_projectile_rand_y_rot ";
    public static final String GENERATE_RANDOM_SHIPS = "generateRandomShips";
    public static final String GENERATE_RANDOM_SHIPS_AXIS = "generateRandomShipsAxis";
    public static final String GENERATE_RANDOM_SHIPS_AXIS_RAD = "generateRandomShipsAxisRad";
    public static final String GENERATE_RANDOM_SHIP_X_AXIS = "generate_random_ship_x_axis ";
    public static final String GENERATE_RANDOM_SHIP_Y_AXIS = "generate_random_ship_y_axis ";
    public static final String GENERATE_RANDOM_SHIP_Z_AXIS = "generate_random_ship_z_axis ";
    public static final String COUNTERMEASURE_ANIMATION_PLAY_SOUND = "CountermeasureAnimation_playSound ";
    public static final String COLLISION_RESPONSE_AXIS = "collision_response_axis ";
    public static final String EVADE_HIT_AXIS = "evade_hit_axis ";
    public static final String PATROL_AXIS = "patrol_axis ";
    public static final String PATROL_DIRECTION = "patrol_direction ";
    public static final String UPDATE_AFTERBURNER_SOUND_NUM = "update_afterburner_sound_num ";
    public static final String CREATE_RELOADER_ENTITY = "create_reloader_entity";
    public static final String CARGO_SHIP_EXPLOSION_SMALL = "cargo_ship_explosion_small ";
    public static final String GENERATE_RANDOM_MULTIPLAYER_PLAYER_SHIP_POSITION = "generateRandomMultiplayerPlayerShipPosition";
//    public static final String PLAYER_SHIP_DESTRUCTION_X = "PlayerShipDestruction_x_";
//    public static final String PLAYER_SHIP_DESTRUCTION_Y = "PlayerShipDestruction_y_";
//    public static final String PLAYER_SHIP_DESTRUCTION_Z = "PlayerShipDestruction_z_";
    
    public static final String SPHERE_AROUND_POINT_X = "sphere_around_point_x";
    public static final String SPHERE_AROUND_POINT_Y = "sphere_around_point_y";
    public static final String SPHERE_AROUND_POINT_Z = "sphere_around_point_z";
    public static final String SPHERE_AROUND_POINT_SIGNUM_X = "sphere_around_point_signum_x";
    public static final String SPHERE_AROUND_POINT_SIGNUM_Y = "sphere_around_point_signum_y";
    public static final String SPHERE_AROUND_POINT_SIGNUM_Z = "sphere_around_point_signum_z";
    public static final String CARGO_SHIP_SELECT_CLOSEST = "cargo_ship_select_closest ";

//    private HashMap<String, AIEntity> aiEntityMap = new HashMap<>();
//    private HashMap<String, TrackerEntity> trackerEntityMap = new HashMap<>();
//    private Timers timers = new Timers();

    private final ArrayList<MultiplayerServerFrameTCP> tcpServerFrames = new ArrayList<>();
    private final ArrayList<MultiplayerServerFrameUDP> udpServerFrames = new ArrayList<>();
    private final ArrayList<MultiplayerClientFrameTCP> tcpClientFrames = new ArrayList<>();
    private final ArrayList<MultiplayerClientFrameUDP> udpClientFrames = new ArrayList<>();
    private final ArrayList<ServerConnectionRequest> serverConnectionRequests = new ArrayList<>();
    private final ArrayList<JoinServerConnectionRequest> joinServerConnectionRequests = new ArrayList<>();
    private final ArrayList<WorldManagerServerSide.AddedPlayer> addedPlayers = new ArrayList<>();
    private final ArrayList<ServerConnectionResponse> serverConnectionResponses = new ArrayList<>();

    public FrameInterval(long intervalNum) {
        super(intervalNum);
    }

    public ArrayList<MultiplayerServerFrameTCP> getTcpServerFrames() {
        return tcpServerFrames;
    }

    public ArrayList<MultiplayerClientFrameTCP> getTcpClientFrames() {
        return tcpClientFrames;
    }

    public ArrayList<MultiplayerClientFrameUDP> getUdpClientFrames() {
        return udpClientFrames;
    }

    public ArrayList<MultiplayerServerFrameUDP> getUdpServerFrames() {
        return udpServerFrames;
    }

    public ArrayList<ServerConnectionRequest> getServerConnectionRequests() {
        return serverConnectionRequests;
    }

    public ArrayList<JoinServerConnectionRequest> getJoinServerConnectionRequests() {
        return joinServerConnectionRequests;
    }

    public ArrayList<WorldManagerServerSide.AddedPlayer> getAddedPlayers() {
        return addedPlayers;
    }

    public ArrayList<ServerConnectionResponse> getServerConnectionResponses() {
        return serverConnectionResponses;
    }

    //    public Timers getTimers() {
//        return timers;
//    }
}
