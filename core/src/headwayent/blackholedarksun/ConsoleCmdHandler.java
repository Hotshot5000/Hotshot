/*
 * Created by Sebastian Bugiu on 13/04/2025, 13:27
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 13/04/2025, 13:27
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.artemis.ComponentMapper;
import com.artemis.Entity;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.world.LevelEntity;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

/**
 * Only use from MainThread.
 */
public class ConsoleCmdHandler {

    public interface ConsoleCmdHandlerListener {
        void notifyConsoleTextUpdated();
    }

    private static final ConsoleCmdHandler singleton = new ConsoleCmdHandler();
    private StringBuilder consoleText = new StringBuilder();
    private StringBuilder lastResult = new StringBuilder();
    private LinkedList<String> lastCommands = new LinkedList<>();
    private int currentLastCommand = -1;
    private LinkedList<String> autoCompletes = new LinkedList<>();
    private String currentWordRootAutoCompleteText = "";
    private int currentAutoComplete = -1;
    private ArrayList<ConsoleCmdHandlerListener> listeners = new ArrayList<>();
    private boolean cheating;

    private enum ConsoleCommand {
        SET_HEALTH,
        GET_HEALTH,
        SET_INVINCIBLE,
        GET_INVINCIBLE,
        SET_AI_ENABLED,
        GET_AI_ENABLED,
        GET_AI_STATE,
        GET_ALL_AI_STATE,
        GET_AI_WAYPOINT_STATE,
        GET_ALL_AI_WAYPOINT_STATE,
        GET_FOLLOWED_SHIP,
        GET_ALL_FOLLOWED_SHIP,
        GET_ALL_PROJECTILE_PARENT,
        GET_POSITION_ORIENTATION_VELOCITY,
        GET_ALL_POSITION_ORIENTATION_VELOCITY,
        GET_AI_TRACE,
        GET_ALL_AI_TRACE,
        GIVE_AMMO,
        EXIT,
        QUIT,
        CLEAR,
        INVALID_COMMAND;

        public static ConsoleCommand getCommand(String str) {
            if (str.equalsIgnoreCase(SET_HEALTH.toString().replace("_", ""))) {
                return SET_HEALTH;
            } else if (str.equalsIgnoreCase(GET_HEALTH.toString().replace("_", ""))) {
                return GET_HEALTH;
            } else if (str.equalsIgnoreCase(SET_INVINCIBLE.toString().replace("_", ""))) {
                return SET_INVINCIBLE;
            } else if (str.equalsIgnoreCase(GET_INVINCIBLE.toString().replace("_", ""))) {
                return GET_INVINCIBLE;
            } else if (str.equalsIgnoreCase(SET_AI_ENABLED.toString().replace("_", ""))) {
                return SET_AI_ENABLED;
            } else if (str.equalsIgnoreCase(GET_AI_ENABLED.toString().replace("_", ""))) {
                return GET_AI_ENABLED;
            } else if (str.equalsIgnoreCase(GET_AI_STATE.toString().replace("_", ""))) {
                return GET_AI_STATE;
            } else if (str.equalsIgnoreCase(GET_ALL_AI_STATE.toString().replace("_", ""))) {
                return GET_ALL_AI_STATE;
            } else if (str.equalsIgnoreCase(GET_AI_WAYPOINT_STATE.toString().replace("_", ""))) {
                return GET_AI_WAYPOINT_STATE;
            } else if (str.equalsIgnoreCase(GET_ALL_AI_WAYPOINT_STATE.toString().replace("_", ""))) {
                return GET_ALL_AI_WAYPOINT_STATE;
            } else if (str.equalsIgnoreCase(GET_FOLLOWED_SHIP.toString().replace("_", ""))) {
                return GET_FOLLOWED_SHIP;
            } else if (str.equalsIgnoreCase(GET_ALL_FOLLOWED_SHIP.toString().replace("_", ""))) {
                return GET_ALL_FOLLOWED_SHIP;
            } else if (str.equalsIgnoreCase(GET_ALL_PROJECTILE_PARENT.toString().replace("_", ""))) {
                return GET_ALL_PROJECTILE_PARENT;
            } else if (str.equalsIgnoreCase(GIVE_AMMO.toString().replace("_", ""))) {
                return GIVE_AMMO;
            } else if (str.equalsIgnoreCase(GET_POSITION_ORIENTATION_VELOCITY.toString().replace("_", ""))) {
                return GET_POSITION_ORIENTATION_VELOCITY;
            } else if (str.equalsIgnoreCase(GET_ALL_POSITION_ORIENTATION_VELOCITY.toString().replace("_", ""))) {
                return GET_ALL_POSITION_ORIENTATION_VELOCITY;
            } else if (str.equalsIgnoreCase(GET_AI_TRACE.toString().replace("_", ""))) {
                return GET_AI_TRACE;
            } else if (str.equalsIgnoreCase(GET_ALL_AI_TRACE.toString().replace("_", ""))) {
                return GET_ALL_AI_TRACE;
            } else if (str.equalsIgnoreCase(EXIT.toString())) {
                return EXIT;
            } else if (str.equalsIgnoreCase(QUIT.toString())) {
                return QUIT;
            } else if (str.equalsIgnoreCase(CLEAR.toString())) {
                return CLEAR;
            } else {
                return INVALID_COMMAND;
            }
        }
    }

    private ConsoleCmdHandler() {

    }

    public String handleCommand(String commandStr) {
        if (commandStr.isEmpty()) return "";
        lastResult.setLength(0);
        lastCommands.add(commandStr);
        DataInputStream fp0 = ENG_Resource.getStringAsStream(commandStr);
        String command = ENG_CompilerUtil.getNextWord(fp0);
        ConsoleCommand commandType = ConsoleCommand.getCommand(command);
        try {
            switch (commandType) {
                case SET_HEALTH: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    int health = ENG_AbstractCompiler.getInt(fp0);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    EntityProperties entityProperties = getEntityProperties(levelEntity);
                    entityProperties.setHealth(health);
                    writeOutput(entity + " health set: " + health);
                    cheating = true;
                }
                break;
                case GET_HEALTH: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    EntityProperties entityProperties = getEntityProperties(levelEntity);
                    writeOutput(entity + " health: " + entityProperties.getHealth());
                    cheating = true;
                }
                    break;
                case SET_INVINCIBLE: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    boolean invincible = ENG_AbstractCompiler.getBoolean(fp0);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    EntityProperties entityProperties = getEntityProperties(levelEntity);
                    entityProperties.setInvincible(invincible);
                    writeOutput(entity + " invincibility set: " + invincible);
                    cheating = true;
                }
                    break;
                case GET_INVINCIBLE: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    EntityProperties entityProperties = getEntityProperties(levelEntity);
                    writeOutput(entity + " invincibility: " + entityProperties.isInvincible());
                }
                    break;
                case SET_AI_ENABLED: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    boolean aiEnabled = ENG_AbstractCompiler.getBoolean(fp0);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    ShipProperties shipProperties = getShipProperties(levelEntity);
                    if (shipProperties == null) break;
                    shipProperties.setAiEnabled(aiEnabled);
                    writeOutput(entity + " AI enabled set: " + aiEnabled);
                    cheating = true;
                }
                    break;
                case GET_AI_ENABLED: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    ShipProperties shipProperties = getShipProperties(levelEntity);
                    if (shipProperties == null) break;
                    writeOutput(entity + " AI enabled: " + shipProperties.isAiEnabled());
                }
                    break;
                case GET_AI_STATE: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (writeAIState(levelEntity)) break;
                }
                    break;
                case GET_ALL_AI_STATE: {
                    for (LevelEntity levelEntity : WorldManagerBase.getSingleton().getLevelEntities()) {
                        writeAIState(levelEntity);
                    }
                }
                    break;
                case GET_AI_WAYPOINT_STATE: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (writeAIWaypointState(levelEntity)) break;
                }
                    break;
                case GET_ALL_AI_WAYPOINT_STATE: {
                    for (LevelEntity levelEntity : WorldManagerBase.getSingleton().getLevelEntities()) {
                        writeAIWaypointState(levelEntity);
                    }

                }
                    break;
                case GET_FOLLOWED_SHIP: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    AIProperties aiProperties = getAIProperties(levelEntity);
                    if (aiProperties == null) break;
                    if (aiProperties.getFollowedShip() == -1) {
                        writeOutput(entity + " no followed ship");
                        break;
                    }
                    if (getFollowedShip(aiProperties, entity)) break;
                }
                    break;
                case GET_ALL_FOLLOWED_SHIP: {
                    for (LevelEntity levelEntity : WorldManagerBase.getSingleton().getLevelEntities()) {
                        EntityProperties entityProperties = getEntityProperties(levelEntity);
                        AIProperties aiProperties = getAIProperties(levelEntity, false);
                        if (aiProperties == null) {
//                            writeOutput(entityProperties.getName() + " does not have AI");
                            continue;
                        }
                        getFollowedShip(aiProperties, entityProperties.getName());
                    }

                }
                    break;
                case GET_ALL_PROJECTILE_PARENT: {
                    for (Entity entity : WorldManagerBase.getSingleton().getAllEntities()) {
                        ProjectileProperties projectileProperties = getProjectileProperties(entity, false);
                        if (projectileProperties == null) {
                            continue;
                        }
                        EntityProperties entityProperties = getEntityProperties(entity);
                        writeOutput(entityProperties.getName() + " projectile with parent: " + projectileProperties.getParentName());
                    }

                }
                    break;
                case GET_POSITION_ORIENTATION_VELOCITY: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity, false);
                    if (levelEntity == null) break;
                    writePositionOrientationVelocity(levelEntity);
                    cheating = true;
                }
                    break;
                case GET_ALL_POSITION_ORIENTATION_VELOCITY: {
                    for (LevelEntity levelEntity : WorldManagerBase.getSingleton().getLevelEntities()) {
                        writePositionOrientationVelocity(levelEntity);
                    }
                    cheating = true;
                }
                    break;
                case GET_AI_TRACE: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity, false);
                    if (levelEntity == null) break;
                    if (writeAITrace(levelEntity)) break;
                    cheating = true;
                }
                    break;
                case GET_ALL_AI_TRACE: {
                    for (LevelEntity levelEntity : WorldManagerBase.getSingleton().getLevelEntities()) {
                        writeAITrace(levelEntity);
                    }
                    cheating = true;
                }
                    break;
                case GIVE_AMMO: {
                    String entity = ENG_CompilerUtil.getNextWord(fp0);
                    entity = checkPlayerShip(entity);
                    LevelEntity levelEntity = getLevelEntity(entity);
                    if (levelEntity == null) break;
                    ShipProperties shipProperties = getShipProperties(levelEntity);
                    if (shipProperties == null) break;
                    for (WeaponData.WeaponType wpn : shipProperties.getShipData().weaponTypeList) {
                        if (!WeaponData.WeaponType.hasInfiniteAmmo(wpn)) {
                            WeaponProperties weaponProperties = getWeaponProperties(levelEntity.getEntity());
                            if (weaponProperties == null) {
                                continue;
                            }
                            while (weaponProperties.getWeaponAmmo(wpn) < WeaponData.WeaponType.getDefaultMissileNumber(wpn)) {
                                weaponProperties.incrementWeaponAmmo(wpn, 1);
                            }
                        }
                    }
                    writeOutput(entity + " now has full ammo");
                    cheating = true;
                }
                    break;
                case EXIT:
                case QUIT:
                    SimpleViewGameMenuManager.exitGame();
                    break;
                case CLEAR:
                    consoleText.setLength(0);
                    writeOutput("");
                    break;
                case INVALID_COMMAND:
                    writeOutput(commandStr + " is an invalid command!");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
        } catch (Exception e) {
            writeOutput(command + " invalid params");
        }
        closeStream(fp0);
        return getLastResult();
    }

    private boolean writeAIState(LevelEntity levelEntity) {
        if (levelEntity == null) return true;
        AIProperties aiProperties = getAIProperties(levelEntity);
        if (aiProperties == null) return true;
        EntityProperties entityProperties = getEntityProperties(levelEntity);
        writeOutput(entityProperties.getName() + " aiState: " + aiProperties.getState());
        return false;
    }

    private boolean writeAIWaypointState(LevelEntity levelEntity) {
        if (levelEntity == null) return true;
        AIProperties aiProperties = getAIProperties(levelEntity, false);
        if (aiProperties == null) return true;
        EntityProperties entityProperties = getEntityProperties(levelEntity);
        writeOutput(entityProperties.getName() + " aiWaypointState: " + aiProperties.getWaypointState() +
                " waypointSectorId: " + aiProperties.getCurrentWaypointSectorId() +
                " waypointId: " + aiProperties.getCurrentWaypointId() +
                " targetWaypointId: " + aiProperties.getCurrentTargetWaypointId() +
                " entranceWaypointId: " + aiProperties.getEntranceWaypointId() +
                " exitWaypointId: " + aiProperties.getExitWaypointId());
        return false;
    }

    private boolean writeAITrace(LevelEntity levelEntity) {
        EntityProperties entityProperties = getEntityProperties(levelEntity);
        AIProperties aiProperties = getAIProperties(levelEntity, false);
        if (aiProperties == null) return true;
        writeOutput(entityProperties.getName() + " aiState: " + aiProperties.getState() +
                " aiWaypointState: " + aiProperties.getWaypointState() +
                " isCollided: " + aiProperties.isCollided() +
                " isCollidedWithStatic: " + aiProperties.isCollidedWithStaticObject() +
                " isEvadingCollision: " + aiProperties.isEvadingCollision() +
                " isEvadingHit: " + aiProperties.isEvadingHit() +
                " enemySelected: " + aiProperties.isEnemySelected() +
                " isLockedIn: " + aiProperties.isLockedIn() +
                " isPatrolling: " + aiProperties.isPatrolling() +
                " isReachDestination: " + aiProperties.isReachDestination() +
                " destination: " + aiProperties.getDestination() +
                " isDestinationReached: " + aiProperties.isDestinationReached() +
                " isWaypointShootPlayer: " + aiProperties.isWaypointShootPlayer() +
                " isReloaderShouldLeaveWorld: " + aiProperties.isReloaderShouldLeaveWorld() +
                " chasedByEnemyNum: " + aiProperties.getChasedByEnemyNum() +
                " getAttackEntityName: " + aiProperties.getAttackEntityName());
        return false;
    }

    private void writePositionOrientationVelocity(LevelEntity levelEntity) {
        EntityProperties entityProperties = getEntityProperties(levelEntity);
        ENG_Vector4D axis = new ENG_Vector4D();
        float angleDeg = entityProperties.getOrientation().toAngleAxisDeg(axis);
        writeOutput(entityProperties.getName() + " position: " + entityProperties.getPosition() +
                " orientation axis: " + axis + " angle: " + angleDeg +
                " velocity: " + entityProperties.getVelocity() +
                " health: " + entityProperties.getHealth());
    }

    private static String checkPlayerShip(String entity) {
        if (entity.equalsIgnoreCase("ps")) {
            entity = "PlayerShip";
        }
        return entity;
    }

    private boolean getFollowedShip(AIProperties aiProperties, String entity) {
        Entity entityByItemId = WorldManagerBase.getSingleton().getEntityByItemId(aiProperties.getFollowedShip());
        if (entityByItemId == null) {
            writeOutput(entity + " could not find followed ship with item id: " + aiProperties.getFollowedShip());
            return true;
        }
        ComponentMapper<EntityProperties> entityPropertiesComponentMapper =
                WorldManagerBase.getSingleton().getEntityPropertiesComponentMapper();
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(entityByItemId);
        writeOutput(entity + " following: " + entityProperties.getName());
        return false;
    }

    private static EntityProperties getEntityProperties(LevelEntity levelEntity) {
        return getEntityProperties(levelEntity.getEntity());
    }

    private static EntityProperties getEntityProperties(Entity entity) {
        ComponentMapper<EntityProperties> entityPropertiesComponentMapper =
                WorldManagerBase.getSingleton().getEntityPropertiesComponentMapper();
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
        return entityProperties;
    }

    private ProjectileProperties getProjectileProperties(Entity entity) {
        return getProjectileProperties(entity, false);
    }

    private ProjectileProperties getProjectileProperties(Entity entity, boolean printOut) {
        ComponentMapper<ProjectileProperties> projectilePropertiesComponentMapper =
                WorldManagerBase.getSingleton().getProjectilePropertiesComponentMapper();
        ProjectileProperties projectileProperties = projectilePropertiesComponentMapper.getSafe(entity);
        if (projectileProperties == null && printOut) {
            writeOutput("Not a projectile");
        }
        return projectileProperties;
    }

    private WeaponProperties getWeaponProperties(Entity entity) {
        return getWeaponProperties(entity, true);
    }

    private WeaponProperties getWeaponProperties(Entity entity, boolean printOut) {
        ComponentMapper<WeaponProperties> weaponPropertiesComponentMapper =
                WorldManagerBase.getSingleton().getWeaponPropertiesComponentMapper();
        WeaponProperties weaponProperties = weaponPropertiesComponentMapper.getSafe(entity);
        if (weaponProperties == null && printOut) {
            writeOutput("No weapon properties");
        }
        return weaponProperties;
    }

    private ShipProperties getShipProperties(LevelEntity levelEntity) {
        return getShipProperties(levelEntity, true);
    }

    private ShipProperties getShipProperties(LevelEntity levelEntity, boolean printOut) {
        ComponentMapper<ShipProperties> shipPropertiesComponentMapper =
                WorldManagerBase.getSingleton().getShipPropertiesComponentMapper();
        ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(levelEntity.getEntity());
        if (shipProperties == null && printOut) {
            writeOutput("Not a ship");
        }
        return shipProperties;
    }

    private AIProperties getAIProperties(LevelEntity levelEntity) {
        return getAIProperties(levelEntity, true);
    }

    private AIProperties getAIProperties(LevelEntity levelEntity, boolean printOut) {
        ComponentMapper<AIProperties> aiPropertiesComponentMapper =
                WorldManagerBase.getSingleton().getAiPropertiesComponentMapper();
        AIProperties aiProperties = aiPropertiesComponentMapper.getSafe(levelEntity.getEntity());
        if (aiProperties == null && printOut) {
            writeOutput("Not controlled by AI");
        }
        return aiProperties;
    }

    private LevelEntity getLevelEntity(String entity) {
        return getLevelEntity(entity, true);
    }

    private LevelEntity getLevelEntity(String entity, boolean printOut) {
        LevelEntity levelEntity = WorldManagerBase.getSingleton().getLevelEntity(entity);
        if (levelEntity == null && printOut) {
            writeOutput(entity + " invalid level entity");
            return null;
        }
        return levelEntity;
    }

    private static void closeStream(DataInputStream fp0) {
        try {
            fp0.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOutput(String output) {
        lastResult.append('\n').append(output);
        writeToConsole(output);
    }

    public void writeToConsole(String output) {
        consoleText.append('\n').append(output);
        for (ConsoleCmdHandlerListener listener : listeners) {
            listener.notifyConsoleTextUpdated();
        }

    }

    public String getOutput() {
        return consoleText.toString();
    }

    public String getLastResult() {
        return lastResult.toString();
    }

    public String getLastCommandBackward() {
        if (lastCommands.isEmpty()) {
            return null;
        }
        if (currentLastCommand == - 1) {
            currentLastCommand = lastCommands.size();
        }
        --currentLastCommand;
        if (currentLastCommand >= 0 && currentLastCommand < lastCommands.size()) {
            return lastCommands.get(currentLastCommand);
        } else {
            if (currentLastCommand == -1) {
                resetLastCommandPosition();
                return "";
            }
            resetLastCommandPosition();

        }
        return null;
    }

    public String getLastCommandForward() {
        if (lastCommands.isEmpty()) {
            return null;
        }
        if (currentLastCommand == -1) {
            return null;
        }
        ++currentLastCommand;
        if (currentLastCommand >= 0 && currentLastCommand < lastCommands.size()) {
            return lastCommands.get(currentLastCommand);
        } else {
            if (currentLastCommand == lastCommands.size()) {
                resetLastCommandPosition();
                return "";
            }
            resetLastCommandPosition();
        }
        return null;
    }

    public String getAutoCompleteNext(String beginText) {
        if (beginText == null || beginText.isEmpty()) {
            resetAutoComplete();
            return null;
        }
        if (!currentWordRootAutoCompleteText.isEmpty() &&
                beginText.toLowerCase(Locale.US).startsWith(currentWordRootAutoCompleteText)) {
            ++currentAutoComplete;
            if (currentAutoComplete >= autoCompletes.size()) {
                currentAutoComplete = 0;
            }
            return autoCompletes.get(currentAutoComplete);
        } else {
            for (ConsoleCommand value : ConsoleCommand.values()) {
                String command = value.toString().replace("_", "").toLowerCase(Locale.US);
                if (command.startsWith(beginText)) {
                    autoCompletes.add(command);
                }
            }
            if (!autoCompletes.isEmpty()) {
                currentWordRootAutoCompleteText = beginText;
                currentAutoComplete = 0;
                return autoCompletes.get(currentAutoComplete);
            }
        }
        return null;
    }

    public String getAutoCompletePrevious(String beginText) {
        if (beginText == null || beginText.isEmpty()) {
            resetAutoComplete();
            return null;
        }
        if (!currentWordRootAutoCompleteText.isEmpty() &&
                beginText.toLowerCase(Locale.US).startsWith(currentWordRootAutoCompleteText)) {
            --currentAutoComplete;
            if (currentAutoComplete < 0) {
                currentAutoComplete = autoCompletes.size() - 1;
            }
            return autoCompletes.get(currentAutoComplete);
        }
        return null;
    }

    public void resetAutoComplete() {
        currentAutoComplete = -1;
        autoCompletes.clear();
        currentWordRootAutoCompleteText = "";
    }

    public void resetLastCommandPosition() {
        currentLastCommand = -1;
    }

    public void addListener(ConsoleCmdHandlerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConsoleCmdHandlerListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public static ConsoleCmdHandler getInstance() {
        return singleton;
    }

    public boolean isCheating() {
        return cheating;
    }

    public void setCheating(boolean cheating) {
        this.cheating = cheating;
    }
}
