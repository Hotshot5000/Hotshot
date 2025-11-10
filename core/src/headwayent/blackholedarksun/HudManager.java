/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectConstArray;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.BeaconProperties;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.CargoProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.StaticEntityProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.effects.HitMarker;
import headwayent.blackholedarksun.effects.MovementFlareManager;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.eventtranslator.modernized.AfterburnerButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.AttackSelectedEnemyButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.BackButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.CountermeasuresButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.DefendPlayerShipButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.EnemySelectionButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.FireButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.MovementControlsListener;
import headwayent.blackholedarksun.eventtranslator.modernized.ReloaderButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.RotationButtonListener;
import headwayent.blackholedarksun.eventtranslator.modernized.SpeedScrollListener;
import headwayent.blackholedarksun.eventtranslator.modernized.WeaponSelectionButtonListener;
import headwayent.blackholedarksun.entitydata.ShipData.ShipTeam;
import headwayent.blackholedarksun.entitydata.WeaponData.WeaponType;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.physics.EntityContactListener;
import headwayent.blackholedarksun.physics.EntityRigidBody;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.physics.StaticEntityRigidBody;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.ENG_Math.QuadraticEquationResult;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.exception.ENG_DivisionByZeroException;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.gui.ENG_ButtonOverlayElement;
import headwayent.hotshotengine.gui.ENG_ControlsOverlayElement;
import headwayent.hotshotengine.gui.ENG_DynamicOverlayElement;
import headwayent.hotshotengine.gui.ENG_GUIOverlayManager;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer.ScrollType;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Frustum;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_Overlay;
import headwayent.hotshotengine.renderer.ENG_OverlayContainer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_OverlayElement.GuiMetricsMode;
import headwayent.hotshotengine.renderer.ENG_OverlayManager;
import headwayent.hotshotengine.renderer.ENG_RaySceneQuery;
import headwayent.hotshotengine.renderer.ENG_RaySceneQuery.RaySceneQueryResultEntry;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement;
import headwayent.hotshotengine.renderer.ENG_TextureNative;
import headwayent.hotshotengine.renderer.ENG_Viewport;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.util.objectpool.ENG_ObjectFactory;
import headwayent.hotshotengine.util.objectpool.ENG_ObjectPool;
import headwayent.hotshotengine.util.objectpool.ENG_PoolObject;

import static headwayent.blackholedarksun.physics.PhysicsUtility.toVector3;
import static headwayent.blackholedarksun.physics.PhysicsUtility.toVector4DAsPt;
import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class HudManager {

    private static final boolean DEBUG = true;
    private static final boolean HUD_VIBRATION_ENABLED = true;
    private static final boolean ENABLE_ATTACK_SELECTED_ENEMY = true;
    private static final boolean ENABLE_DEFEND_PLAYER_SHIP = true;
    private static final boolean USE_BULLET_RAY_TEST = true;
    private static final boolean USE_BULLET_COLLISION_SHAPE = true;
    // The following 2 settings are mutually exclusive.
    private static final boolean USE_CURRENT_POSITION = false;
    private static final boolean USE_BULLET_AABB_POSITION = true;
    private static final int BEEP_RATE = 2000;
    private static final float CARGO_SCAN_MIN_DISTANCE = 400;// 40000.0f;
    private static final int CARGO_SCAN_TIME = 3000;
    private static final float CARGO_SELECTION_ANGLE = 10.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final int MINIMUM_DELAY_BETWEEN_BEEPS = 500;
    public static final String BEEP_SND = "beep";
    private static final float MIN_BEEP_DISTANCE = 2000.0f;
    private static final int VIBRATION_HIT_TIME = 500;
    private static final int VIBRATION_AFTERBURNER_TIME = 600;
    private static final int VIBRATION_COUNTERMEASURES_TIME = 150;
    private static final int VIBRATIONS_PER_SECOND = 40;
    private static final float VIBRATION_DISTANCE_X = 0.01f;
    private static final float VIBRATION_DISTANCE_Y = 0.01f;
    private static final float VIBRATION_DISTANCE_X_MISSILE_HIT = 0.025f;
    private static final float VIBRATION_DISTANCE_Y_MISSILE_HIT = 0.025f;
    private static final float ANGLE_BETWEEN_CROSSHAIR_AND_TARGETED_ENEMY_FOR_AIM_ASSIST = 6.0f * ENG_Math.DEGREES_TO_RADIANS;
    private static final float SHIP_TORQUE_IMPULSE_FOR_AIM_ASSIST = 700.0f;
    public static final int AIM_ASSIST_ROTATION_STEPS = 30;
    public static final int MAX_FRIENDLY_SHIPS_TO_CHASE_ENEMY = 3;
    public static final float RAY_CAST_DISTANCE = 10000.0f;
    public static final int HEALTH_LOW = 20;
    private final ComponentMapper<EntityProperties> entityPropertiesMapper;
    private final ComponentMapper<ShipProperties> shipPropertiesMapper;
    private final ComponentMapper<AIProperties> aIPropertiesMapper;
    private final ENG_Camera camera;
    private ENG_TextAreaOverlayElement cargoCountOverlayElement;
    private ENG_TextAreaOverlayElement cargoScanOverlayElement;
    private ENG_TextAreaOverlayElement tutorialInfoOverlayElement;
    private ENG_TextAreaOverlayElement enemySelectedNoEnemyOverlayElement;
    private ENG_TextAreaOverlayElement enemySelectedDistanceOverlayElement;
    private ENG_TextAreaOverlayElement aboveCrosshairOverlayElement;
    private ENG_TextAreaOverlayElement belowCrosshairOverlayElement;
    private ENG_Overlay beaconDirOverlay;
    private ENG_OverlayContainer beaconDirLeftContainerElement;
    private ENG_OverlayContainer beaconDirUpContainerElement;
    private ENG_OverlayContainer beaconDirRightContainerElement;
    private ENG_OverlayContainer beaconDirDownContainerElement;
    private final ENG_Matrix4 playerShipFullTransform = new ENG_Matrix4();
    private final ENG_Vector4D beaconTransformedPosition = new ENG_Vector4D(true);
    private boolean weaponHudShown;
    private boolean recolorCrosshair;
    private final ENG_Matrix4 beaconFullTransform = new ENG_Matrix4();
    private final ENG_Matrix4 beaconProjectionSpace = new ENG_Matrix4();
    private final ENG_Frustum frustum;
    private final ENG_Matrix4 beaconWorldViewMatrix = new ENG_Matrix4();
    private boolean healthShown;
    private final ENG_Vector4D shipFrontVec = new ENG_Vector4D();
    private boolean pingCreated;
    private ENG_TextAreaOverlayElement pingIndicator;
    private ENG_TextAreaOverlayElement playerPosIndicator;
    private ENG_TextAreaOverlayElement gameResourcesCheckerIndicator;
    private long currentShipSelectionId = -1;
    private final ArrayList<RaySceneQueryPair> raySceneQueryPairList = new ArrayList<>();
    private int radarWidth;
    private HitMarker hitMarker;
    private final ArrayList<VibrationOverlayElement> vibrationOverlayElementList = new ArrayList<>();
    private long currentVibrationDuration;
    private long currentVibrationStartTime;
    private long currentVibrationMovementTime;
    private long currentVibrationsPerSecondWaitTime = 1000 / VIBRATIONS_PER_SECOND;
    private float currentVibrationDistanceX = VIBRATION_DISTANCE_X;
    private float currentVibrationDistanceY = VIBRATION_DISTANCE_Y;
    private boolean vibrationActive;
    private boolean aimAssisting;
    private boolean collisionPositionValid;

    public enum HudVibrationType {
        AFTERBURNER(VIBRATION_AFTERBURNER_TIME, VIBRATION_DISTANCE_X, VIBRATION_DISTANCE_Y, VIBRATIONS_PER_SECOND),
        COUNTERMEASURES(VIBRATION_COUNTERMEASURES_TIME, VIBRATION_DISTANCE_X, VIBRATION_DISTANCE_Y, VIBRATIONS_PER_SECOND),
        MISSILE_HIT(VIBRATION_HIT_TIME, VIBRATION_DISTANCE_X_MISSILE_HIT, VIBRATION_DISTANCE_Y_MISSILE_HIT, VIBRATIONS_PER_SECOND);

        private final int time;
        private final float vibrationDistanceX;
        private final float vibrationDistanceY;
        private final int vibrationsPerSecondWaitTime;

        HudVibrationType(int time,
                                 float vibrationDistanceX, float vibrationDistanceY,
                                 int vibrationsPerSecond) {
            this.time = time;
            this.vibrationDistanceX = vibrationDistanceX;
            this.vibrationDistanceY = vibrationDistanceY;
            this.vibrationsPerSecondWaitTime = 1000 / vibrationsPerSecond;
        }

        public int getTime() {
            return time;
        }

        public float getVibrationDistanceX() {
            return vibrationDistanceX;
        }

        public float getVibrationDistanceY() {
            return vibrationDistanceY;
        }

        public int getVibrationsPerSecondWaitTime() {
            return vibrationsPerSecondWaitTime;
        }
    }

    private static class VibrationOverlayElement {
        private final ENG_OverlayElement element;
        private float origLeft;
        private float origTop;
        private GuiMetricsMode metricsMode = GuiMetricsMode.GMM_RELATIVE;

        public VibrationOverlayElement(ENG_OverlayElement element) {
            this.element = element;
        }

        public VibrationOverlayElement(ENG_OverlayElement element, GuiMetricsMode metricsMode) {
            this.element = element;
            this.metricsMode = metricsMode;
        }

        public void savePosition() {
            origLeft = element.getLeft();
            origTop = element.getTop();
        }

        public void restorePosition() {
            element.setLeft(origLeft);
            element.setTop(origTop);
        }

        /**
         * Params always between 0.0f and 1.0f as GMM_RELATIVE is assumed.
         * @param x
         * @param y
         */
        public void addToOriginalPosition(float x, float y) {
            switch (metricsMode) {
                case GMM_RELATIVE:
                    break;
                case GMM_PIXELS: {
                    ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot()
                            .getCurrentRenderWindow();
                    float windowWidth = window.getWidth();
                    float windowHeight = window.getHeight();
                    x *= windowWidth;
                    y *= windowHeight;
                }
                    break;
                case GMM_RELATIVE_ASPECT_ADJUSTED:
                default:
                    // Should never get here.
                    throw new IllegalStateException(metricsMode + " not supported");
            }
            element.setLeft(origLeft + x);
            element.setTop(origTop + y);
        }
    }

    /**
     * This is an ugly hack.
     */
    public static class RaySceneQueryPair {
        public ENG_RaySceneQuery rayQuery;
        public ArrayList<RaySceneQueryResultEntry> rayQueryResultsList;
        public boolean rayQueryResultsArrived;
    }

    public enum CrosshairType {

        CROSSHAIR, SELECTION
    }

    public static final boolean SHOW_DEBUGGING_INDICATORS = true;
    private static final long SPAWN_INFO_TIME = 5000;
    private static final long PLAYER_SPAWN_INFO_TIME = 5000;
    private static final long TUTORIAL_INFO_TIME = 5000;
    private static final long BELOW_CROSSHAIR_INFO_TIME = 5000;
//    private static HudManager mgr;
    private ENG_Overlay hudOverlay;
    private boolean created;
    private boolean visible;
    private ENG_ButtonOverlayElement fireButtonOverlay;
    private ENG_ScrollOverlayContainer speedScrollOverlay;
    private ENG_ControlsOverlayElement controlsOverlayElement;
    private ENG_DynamicOverlayElement radarOverlayElement;
    private ENG_ButtonOverlayElement backButtonOverlay;
    private FireButtonListener fireButtonListener;
    private SpeedScrollListener speedScrollListener;
    private MovementControlsListener controlsListener;
    private long gameResourcesCheckerBeginTime;
    private long gameResourcesCheckerDuration;
    private boolean showMovementControls; // For the accelerometer
    private ENG_TextAreaOverlayElement fpsIndicator;
    private boolean fpsCreated;
    private boolean playerPosCreated;
    private boolean gameResourcesCheckerCreated;
    // Now we need some variables for setting properties for when the hud will
    // be created. Please note that this is not necessarily at the moment this
    // variables are changed. Usually the hud is created the next frame after
    // the level load so this is why we need this and can't set the containers
    // when we like it
    private int maxScrollPercentageChange = ENG_ScrollOverlayContainer.DEFAULT_MAX_PERCENTAGE_CHANGE;
    private int scrollStartingPercentage;
    private ENG_ButtonOverlayElement afterburnerButtonOverlay;
    private ENG_ButtonOverlayElement countermeasuresButtonOverlay;
    private ENG_ButtonOverlayElement reloaderButtonOverlay;
    private ENG_ButtonOverlayElement weaponSelectionPreviousButtonOverlay;
    private ENG_ButtonOverlayElement weaponSelectionNextButtonOverlay;
    private ENG_ButtonOverlayElement rotateLeftButtonOverlay;
    private ENG_ButtonOverlayElement rotateRightButtonOverlay;
    private ENG_ButtonOverlayElement enemySelectionLeftButtonOverlay;
    private ENG_ButtonOverlayElement enemySelectionRightButtonOverlay;
    private ENG_ButtonOverlayElement attackSelectedEnemyButtonOverlay;
    private ENG_ButtonOverlayElement defendPlayerShipButtonOverlay;
    private AfterburnerButtonListener afterburnerButtonListener;
    private CountermeasuresButtonListener countermeasuresButtonListener;
    private ReloaderButtonListener reloaderButtonListener;
    private RotationButtonListener rotationLeftButtonListener;
    private RotationButtonListener rotationRightButtonListener;
    private WeaponSelectionButtonListener weaponSelectionPreviousButtonListener;
    private WeaponSelectionButtonListener weaponSelectionNextButtonListener;
    private EnemySelectionButtonListener enemySelectionPreviousButtonListener;
    private EnemySelectionButtonListener enemySelectionNextButtonListener;
    private AttackSelectedEnemyButtonListener attackSelectedEnemyButtonListener;
    private DefendPlayerShipButtonListener defendPlayerShipButtonListener;
    private BackButtonListener backButtonListener;
    private ENG_TextAreaOverlayElement weaponOverlayElement;
    private WeaponType currentWeaponType;
    private int currentWeaponAmmo;
    private final MovementFlareManager movementFlareManager;
    private final int windowWidth;
    private final int windowHeight;

    public HudManager() {
        entityPropertiesMapper = MainApp.getGame().getWorldManager().getEntityPropertiesComponentMapper();
        shipPropertiesMapper = MainApp.getGame().getWorldManager().getShipPropertiesComponentMapper();
        aIPropertiesMapper = MainApp.getGame().getWorldManager().getAiPropertiesComponentMapper();
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        createDebuggingIndicators();
        frustum = new ENG_Frustum("HudManagerFrustum");
        frustum.setNearClipDistance(10.0f);
        // More correct since we are in a box it should be 2 * sqrt(boxlen);
        frustum.setFarClipDistance(GameWorld.MAX_DISTANCE * 6.0f);
        ENG_Viewport viewport = ENG_RenderRoot.getRenderRoot()
                .getCurrentRenderWindow().getViewport(0);
        frustum.setAspectRatio(((float) viewport.getActualWidth())
                / ((float) viewport.getActualHeight()));
        movementFlareManager = new MovementFlareManager();

        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        camera = sceneManager.getCamera(APP_Game.MAIN_CAM);
        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
        windowWidth = window.getWidth();// renderSurface.getWidth();
        windowHeight = window.getHeight();// renderSurface.getHeight();
    }

    private void addVibrationElement(ENG_OverlayElement element) {
        addVibrationElement(element, GuiMetricsMode.GMM_RELATIVE);
    }

    private void addVibrationElement(ENG_OverlayElement element, GuiMetricsMode metricsMode) {
        if (!HUD_VIBRATION_ENABLED) {
            return;
        }
        VibrationOverlayElement foundElement = null;
        for (VibrationOverlayElement vibrationOverlayElement : vibrationOverlayElementList) {
            if (vibrationOverlayElement.element == element) {
                foundElement = vibrationOverlayElement;
                break;
            }
        }

        if (foundElement != null) {
            throw new IllegalArgumentException(element.getName() + " already exists in vibration list");
        }
        vibrationOverlayElementList.add(new VibrationOverlayElement(element, metricsMode));
    }

    private void removeVibrationElement(ENG_OverlayElement element) {
        if (!HUD_VIBRATION_ENABLED) {
            return;
        }
        VibrationOverlayElement foundElement = null;
        for (VibrationOverlayElement vibrationOverlayElement : vibrationOverlayElementList) {
            if (vibrationOverlayElement.element == element) {
                foundElement = vibrationOverlayElement;
                break;
            }
        }
        if (foundElement == null) {
            throw new IllegalArgumentException(element.getName() + " doesn't exist in the vibration list");
        }
        vibrationOverlayElementList.remove(foundElement);
    }

    private void removeAllVibrationElements() {
        vibrationOverlayElementList.clear();
    }

    public void lockRadarData() {
        if (visible) {
            radarOverlayElement.lock();
            matricesConcatenated = false;
//            System.out.println("New Frame");
        }
    }

    public void unlockRadarData() {
        if (visible) {
            radarOverlayElement.unlock();
        }
    }

    public void resetRadar() {
        if (visible) {
            radarOverlayElement.resetToInitialTexture();
//            System.out.println("radar data reset");
        }
    }

    public void updateRadarFinalState() {
        if (visible) {
            radarOverlayElement.updateFinalTexture();
//            System.out.println("radar data final state update");
        }
    }

    public void resetCurrentFrameRadarVisibilityIds() {
        currentFrameRadarVisibilityMap.clear();
//        System.out.println("resetCurrentFrameRadarVisibilityIds");
    }

//    private final ENG_Vector4D centre = new ENG_Vector4D(true);
    private final HashMap<Long, ArrayList<RadarVisibilityData>> radarVisibilityDataMap = new HashMap<>();
    private final HashMap<Long, RadarVisibilityData> currentFrameRadarVisibilityMap = new HashMap<>();
    private static final int DEFAULT_RADAR_VISIBILITY_DATA_QUEUE_LENGTH = 256;
    private final ENG_ObjectPool<RadarVisibilityData> radarVisibilityDataQueue = new ENG_ObjectPool<>(new ENG_ObjectFactory<RadarVisibilityData>() {
        @Override
        public RadarVisibilityData create() {
            return new RadarVisibilityData();
        }

        @Override
        public void destroy(RadarVisibilityData obj) {

        }
    }, DEFAULT_RADAR_VISIBILITY_DATA_QUEUE_LENGTH, true, "radarVisibilityDataQueue");
    private final ENG_Vector4D screenPos = new ENG_Vector4D(true);
    private final ENG_Matrix4 fullTrans = new ENG_Matrix4();
    private final ENG_Matrix4 viewMatrix = new ENG_Matrix4();
    private final ENG_Matrix4 invViewMatrix = new ENG_Matrix4();
    private final ENG_Matrix4 projMatrix = new ENG_Matrix4();
    private final ENG_Matrix4 viewProjMatrix = new ENG_Matrix4();
    private final ENG_Matrix4 worldViewProjMatrix = new ENG_Matrix4();
    private final ENG_ColorValue alienColour = new ENG_ColorValue(ENG_ColorValue.RED);
    private final ENG_ColorValue humanColour = new ENG_ColorValue(ENG_ColorValue.BLUE);
    private boolean matricesConcatenated;

    private static class RadarVisibilityData implements ENG_PoolObject {
        public final ENG_Vector4D centre = new ENG_Vector4D(true);
        public ENG_Boolean visibility = new ENG_Boolean();
        public ENG_Boolean visibilityDataSet = new ENG_Boolean();
        public ShipTeam team;
        public Entity entity;

        // Just for debugging.
        public String entityName;
        public int writeableBuffer;
        public long frameNum;

        public void reset() {
            // We cannot just set the values of the fields as we wish since they might have been
            // already passed to ENG_NativeCalls functions. Read the following comment from
            // radarVisibilityDataQueue.add() below:
            //
            // The problem with this is that we might add to the pool an entity for which the
            // visibilityDataSet has already been passed as a pointer to a ENG_NativeCalls function.
            // So we could end up as getting an object from the queue whose pointer is shared with
            // an ENG_NativeCalls function that then overrides the visibilityDataSet when it returns.
            // In this case we are dead since visibilityDataSet will become true with no
            // actual data behind it.
            visibility = new ENG_Boolean();
            visibilityDataSet = new ENG_Boolean();
        }
    }

    public void updateRadarData(Entity e, ENG_SceneNode node, EntityProperties entityProperties, ShipProperties shipProperties) {
        if (!visible) {
            return;
        }
        createViewProjectionMatrix(camera);
        if (!matricesConcatenated) {
            return;
        }

        if (!entityProperties.getItem().getWorldAABB().isFinite()) {
            if (MainActivity.isDebugmode()) {
                Entity ship = WorldManager.getSingleton().getShipByGameEntityId(entityProperties.getEntityId());
                if (ship != null) {
                    System.out.println("ship destroyed: " + entityProperties.isDestroyed());
                }
                System.out.println("node name: " + node.getName() + " ship pos: " + node.getPosition());
            }
            return;
        }
//        System.out.println("updateRadarData: " + entityProperties.getName());
        // TODO replace with rayTest from Bullet.
        RadarVisibilityData radarVisibilityData = radarVisibilityDataQueue.get();
        radarVisibilityData.entity = e;
        radarVisibilityData.entityName = entityProperties.getName();
        radarVisibilityData.writeableBuffer = ENG_RenderingThread.getCurrentWriteableBuffer();
        radarVisibilityData.frameNum = MainApp.getMainThread().getFrameNum();
        entityProperties.getItem().getWorldAABB().getCenter(radarVisibilityData.centre);
        camera.isVisibleNative(radarVisibilityData.centre, radarVisibilityData.visibility, radarVisibilityData.visibilityDataSet);
        radarVisibilityData.team = shipProperties.getShipData().team;
        currentFrameRadarVisibilityMap.put(entityProperties.getItemId(), radarVisibilityData);
//        System.out.println("updateRadarData for: " + entityProperties.getName() + " writeableBuffer: " +
//                radarVisibilityData.writeableBuffer + " frameNum: " +
//                radarVisibilityData.frameNum);
    }

    public void sendRadarVisibilityDataToNative() {
//        System.out.println("sendRadarVisibilityDataToNative()");
        // Check that all the visibility data not belonging to the current frame is ignored.
        for (Iterator<Map.Entry<Long, ArrayList<RadarVisibilityData>>> it = radarVisibilityDataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, ArrayList<RadarVisibilityData>> next = it.next();
            RadarVisibilityData radarVisibilityData = currentFrameRadarVisibilityMap.get(next.getKey());
            if (radarVisibilityData == null) {
                // We no longer care about what the native side returns since the object is no longer in front of us.
                // The problem with this is that we might add to the pool an entity for which the
                // visibilityDataSet has already been passed as a pointer to a ENG_NativeCalls function.
                // So we could end up as getting an object from the queue whose pointer is shared with
                // an ENG_NativeCalls function that then overrides the visibilityDataSet when it returns.
                // In this case we are dead since visibilityDataSet will become true with no
                // actual data behind it.
                for (RadarVisibilityData visibilityData : next.getValue()) {
//                    System.out.println("Getting rid of visibilityData: " + visibilityData.entityName);
                    radarVisibilityDataQueue.add(visibilityData);
                }

                it.remove();
            }
        }

        // Add the current frame data to the radar visibility map.
        for (Map.Entry<Long, RadarVisibilityData> entry : currentFrameRadarVisibilityMap.entrySet()) {
            Long itemId = entry.getKey();
            RadarVisibilityData radarVisibilityData = entry.getValue();
            ArrayList<RadarVisibilityData> radarVisibilityDataList = radarVisibilityDataMap.get(itemId);
            if (radarVisibilityDataList == null) {
                radarVisibilityDataList = new ArrayList<>();
                radarVisibilityDataMap.put(itemId, radarVisibilityDataList);
            }
            radarVisibilityDataList.add(radarVisibilityData);

        }

//        ArrayList<RadarVisibilityData> radarVisibilityDataToRemove = new ArrayList<>();
        // Check if any data has been updated from the native side, and if yes, send it back to native to
        // draw the updated radar.
        for (Iterator<Map.Entry<Long, ArrayList<RadarVisibilityData>>> mapIt = radarVisibilityDataMap.entrySet().iterator(); mapIt.hasNext(); ) {
            Map.Entry<Long, ArrayList<RadarVisibilityData>> next = mapIt.next();
            ArrayList<RadarVisibilityData> visibilityDataList = next.getValue();

            // We only care about the latest updated visibility data. Once we have that we can send it to native
            // and delete the previous data from the list. We no longer care about the responses from native
            // for the old data.
            boolean visibilityDataUpdated = false;
            for (ListIterator<RadarVisibilityData> it = visibilityDataList.listIterator(visibilityDataList.size()); it.hasPrevious(); ) {
                RadarVisibilityData visibilityData = it.previous();
                if (visibilityDataUpdated) {
                    radarVisibilityDataQueue.add(visibilityData);
                    it.remove();
                    continue;
                }
                if (visibilityData.visibilityDataSet.getValue()) {
                    if (visibilityData.visibility.getValue()) {
                        // Wait until we have confirmation from native side that we are indeed inside the visual range.
                        if (USE_CURRENT_POSITION) {
                            EntityProperties entityProperties = entityPropertiesMapper.get(visibilityData.entity);
                            ENG_Vector4D position = entityProperties.getNode().getPosition();
                            transformByProjectionMat(viewProjMatrix, position, screenPos);
                        } else if (USE_BULLET_AABB_POSITION) {
                            EntityProperties entityProperties = entityPropertiesMapper.get(visibilityData.entity);
                            ENG_AxisAlignedBox aabb = PhysicsUtility.getAxisAlignedBox(entityProperties.getRigidBody());
                            ENG_Vector4D position = aabb.getCenter();
                            transformByProjectionMat(viewProjMatrix, position, screenPos);
                        } else {
                            transformByProjectionMat(viewProjMatrix, visibilityData.centre, screenPos);
                        }

                        screenPos.x = (screenPos.x + 1.0f) * 0.5f;
                        screenPos.y = 1.0f - (screenPos.y + 1.0f) * 0.5f;
                        if (screenPos.x < 0.0f || screenPos.x > 1.0f ||
                                screenPos.y < 0.0f || screenPos.y > 1.0f) {
//                            System.out.println("radarVisibility out of bounds entityName: " + visibilityData.entityName +
//                            " writeableBuffer: " + visibilityData.writeableBuffer +
//                                    " frameNum: " + visibilityData.frameNum + " currentFrameNum: " + MainApp.getMainThread().getFrameNum());
//                            System.out.println("radarOverlayElement screenPos: " + screenPos + " winv: " + winv +
//                                    " team: " + visibilityData.team + " visibilityData.centre: " + visibilityData.centre +
//                                    " entityName: " + visibilityData.entityName +
//                                    "\nviewMatrix: " + viewMatrix + "\nprojMatrix: " + projMatrix);
                            visibilityDataUpdated = true;
                            continue;
                        }
//                        System.out.println("radarVisibility entityName: " + visibilityData.entityName);
//                        System.out.println("radarOverlayElement screenPos.x: " + screenPos.x + " screenPos.y: " + screenPos.y +
//                                        " team: " + visibilityData.team + " visibilityData.centre: " + visibilityData.centre +
//                                        " matricesConcatenated: " + matricesConcatenated);
                        screenPos.x = ENG_Math.clamp(screenPos.x, 0.0f, 1.0f);
                        screenPos.y = ENG_Math.clamp(screenPos.y, 0.0f, 1.0f);
                        // Since the radar is now 1024*1024 we also need to scale the num of pixels.
                        int pixelLen = radarWidth / 64 * 2; // 2 pixels per unit of adjusted width.
                        visibilityDataUpdated = radarOverlayElement.setPointScreenSpace(screenPos.x, screenPos.y, pixelLen,
                                visibilityData.team == ShipTeam.HUMAN ? humanColour : alienColour, false);
//                        System.out.println("radarOverlayElement screenPox.x: " + screenPos.x + " screenPos.y: " + screenPos.y +
//                        " team: " + visibilityData.team + " visiblityDataUpdated: " + visibilityDataUpdated);
//                        if (visibilityDataUpdated) {
//                            it.remove();
//                        }
//                        System.out.println("updated radar with point for id: " + next.getKey());
//                    radarVisibilityDataToRemove.add(visibilityData);
                    } else {
                        visibilityDataUpdated = true;
//                        System.out.println("centre not visible for " + visibilityData.entityName +
//                                " writeableBuffer: " + visibilityData.writeableBuffer +
//                                " frameNum: " + visibilityData.frameNum + " currentFrameNum: " + MainApp.getMainThread().getFrameNum());
                    }

                }
            }

            if (visibilityDataList.isEmpty()) {
                mapIt.remove();
            }
        }
    }

    private void createViewProjectionMatrix(ENG_Camera camera) {
        if (!matricesConcatenated) {
            boolean matricesSet = camera.getProjectionMatrixNative(projMatrix, ENG_RenderingThread.getCurrentWriteableBuffer());
            matricesSet &= camera.getViewMatrixNative(viewMatrix, ENG_RenderingThread.getCurrentWriteableBuffer());
            if (matricesSet) {
                viewMatrix.invert(invViewMatrix);
                projMatrix.concatenate(viewMatrix, viewProjMatrix);
//                System.out.println("currentWriteableBuffer: " + ENG_RenderingThread.getCurrentWriteableBuffer());
            }
            matricesConcatenated = matricesSet;
        }
    }

    private final ENG_Vector4D rayOrigin = new ENG_Vector4D(true);
    private final ENG_Vector4D rayDir = new ENG_Vector4D();
    private final ENG_Vector4D[] corners = new ENG_Vector4D[8];
    private final ENG_Vector4D[] transCorners = new ENG_Vector4D[8];
    private final ENG_Vector4D[] cameraSpaceTransCorners = new ENG_Vector4D[8];
    private boolean cornersVectorsInitialized;
    private final ENG_Vector2D leftTop = new ENG_Vector2D();
    /*
     * private ENG_Vector4D leftBottom = new ENG_Vector4D(true); private
     * ENG_Vector4D rightTop = new ENG_Vector4D(true);
     */
    private final ENG_Vector2D rightBottom = new ENG_Vector2D();
    private ENG_OverlayContainer enemySelection;
    private ENG_Overlay enemySelectionOverlay;
    private ENG_Overlay crosshairOuterOverlay;
    private ENG_Overlay crosshairInnerOverlay;
    private ENG_Overlay crosshairCrossOverlay;
    private ENG_OverlayContainer crosshairOuter;
    private ENG_OverlayContainer crosshairInner;
    private ENG_OverlayContainer crosshairCross;
    private float crossHalfWidth;
    private float crossHalfHeight;
    private ENG_OverlayContainer cross;
    private ENG_Overlay crossOverlay;
    private ENG_OverlayContainer enemySelectionOutsideScreen;
    private ENG_Overlay enemySelectionOutsideScreenOverlay;
    private float enemySelectionOutsideScreenHalfWidth;
    private float enemySelectionOutsideScreenHalfHeight;
    private long enemySelectionTime;
    private long currentEnemySelectionTime;
    private WeaponType previousWeaponType;
    private final ENG_ColorValue enemySelectedColor = ENG_ColorValue.createAsRGBA(0xc70000ff);
    private final ENG_ColorValue defaultCrosshairColor = ENG_ColorValue.createAsRGBA(0x00c700ff);
    private ENG_Item currentSelectedEnemy;
    // The enemy that is currently under selection but the time hasn't passed to
    // redden the crosshair and set it as tracking by the missiles.
    // As long as it stays in the crosshair selection square it will be promoted
    // to currentSelectedEnemy.
    private ENG_Item currentPotentialSelectedEnemy;
    private boolean currentSelectedEnemyFollowable;
    private ENG_TextAreaOverlayElement enemySelectionText;
    private final ENG_RealRect crossRect = new ENG_RealRect();
    private final ENG_RealRect crosshairCrossRect = new ENG_RealRect();
    private final ENG_RealRect crosshairIntersectionRect = new ENG_RealRect();
    private final ENG_Vector4D shipToBeacon = new ENG_Vector4D();

    public void nextEnemySelection() {
        ++currentEnemySelection;
        if (currentEnemySelection >= enemySelectionOrderedByDistance.size()) {
            resetCurrentEnemySelection();
        } else if (currentEnemySelection != -1) {
            updateCurrentEnemySelectionUserId();
        }
    }

    public void previousEnemySelection() {
        --currentEnemySelection;
        if (currentEnemySelection == -1) {
            resetCurrentEnemySelection();
        }
        if (currentEnemySelection < -1) {
            resetCurrentEnemySelection();
            if (!enemySelectionOrderedByDistance.isEmpty()) {
                currentEnemySelection = enemySelectionOrderedByDistance.size() - 1;
                updateCurrentEnemySelectionUserId();
            }
        } else if (currentEnemySelection != -1) {
            updateCurrentEnemySelectionUserId();
        }
    }

    private void updateCurrentEnemySelectionUserId() {
        Entity currentSelectedEnemy = enemySelectionOrderedByDistance.get(currentEnemySelection);
        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(currentSelectedEnemy);
        currentEnemySelectionItemId = entityProperties.getItemId();
    }

    private void resetCurrentEnemySelection() {
        currentEnemySelection = -1;
        currentEnemySelectionItemId = -1;
    }

    private final ENG_Vector4D currentPos = new ENG_Vector4D(true);
    private final ENG_Vector4D otherPos = new ENG_Vector4D(true);
    private final ENG_Vector4D distVec = new ENG_Vector4D(true);
    private final ArrayList<Entity> tempFriendlyShipsList = new ArrayList<>();
    private final ArrayList<Entity> selectedFriendlyShipsList = new ArrayList<>();
    private static final long ABOVE_CROSSHAIR_CAPTION_TIME = 2000;
    private long aboveCrosshairCaptionCurrentTime;

    public void attackCurrentEnemySelection() {
        if (currentEnemySelectionItemId != -1) {
            Entity playerShip = MainApp.getGame().getWorldManager().getPlayerShip();
            if (playerShip == null) {
                return;
            }
            Entity enemyEntity = MainApp.getGame().getWorldManager().getEntityByItemId(currentEnemySelectionItemId);
            if (enemyEntity == null) {
                return;
            }
            EntityProperties playerShipEntityProperties = entityPropertiesMapper.get(playerShip);
            long playerShipItemId = playerShipEntityProperties.getItemId();
            ShipProperties shipProperties = shipPropertiesMapper.getSafe(playerShip);
            if (shipProperties == null) {
                // This should never be possible. Just to be safe because we used this pattern when it probably made sense.
                return;
            }
            ImmutableBag<Entity> friendlyEntities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(
                    ShipTeam.getAsString(shipProperties.getShipData().team));
            if (friendlyEntities.isEmpty()) {
                return;
            }
            // Select closest ships to enemy.
            tempFriendlyShipsList.clear();
            selectedFriendlyShipsList.clear();
            for (Entity e : friendlyEntities) {
                tempFriendlyShipsList.add(e);
            }
            int friendlyNumToSearchFor = Math.min(friendlyEntities.size(), MAX_FRIENDLY_SHIPS_TO_CHASE_ENEMY);
            EntityProperties enemyEntityProperties = entityPropertiesMapper.get(enemyEntity);
            enemyEntityProperties.getNode().getPosition(otherPos);

            for (int friendly = 0; friendly < friendlyNumToSearchFor; ++friendly) {
                // Get closest friendly ship to enemy.
                // Find smallest distance.
                Entity closestFriendly = null;
                float currentMinLen = Float.MAX_VALUE;
                int i = 0;
                for (Entity friendlyEntity : tempFriendlyShipsList) {
                    EntityProperties friendlyEntityProperties = entityPropertiesMapper.get(friendlyEntity);
                    if (friendlyEntityProperties.getItemId() == playerShipItemId) {
                        continue;
                    }
                    AIProperties friendlyAIProperties = aIPropertiesMapper.getSafe(friendlyEntity);
                    if (friendlyAIProperties == null) {
                        // Should  this be possible? Yes if you are a player ship in the same team.
                        System.out.println("friendlyEntity: " + friendlyEntityProperties.getName() +
                                " id: " + enemyEntityProperties.getItemId() + " has no friendlyAIProperties");
                        continue;
                    }
                    // Check that we select only friendlies that are ready for combat.
                    if (friendlyAIProperties.getState() != AIProperties.AIState.SEEK_CLOSEST_PLAYER &&
                            friendlyAIProperties.getState() != AIProperties.AIState.FOLLOW_PLAYER) {
                        continue;
                    }
                    friendlyEntityProperties.getNode().getPosition(currentPos);
                    otherPos.sub(currentPos, distVec);
                    if (i == 0) {
                        currentMinLen = distVec.squaredLength();
                        closestFriendly = friendlyEntity;
                    } else {
                        if (distVec.squaredLength() < currentMinLen) {
                            currentMinLen = distVec.squaredLength();
                            closestFriendly = friendlyEntity;
                        }
                    }
                    ++i;
                }
                if (closestFriendly != null) {
                    selectedFriendlyShipsList.add(closestFriendly);
                    tempFriendlyShipsList.remove(closestFriendly); // Don't search for the same friendly again.
                } else {
                    // How did we get here? Maybe no friendly ships have the ready state to help the player ship.
                    // Might as well not continue searching for another friendly.
                    break; // for (int friendly = 0; friendly < friendlyNumToSearchFor; ++friendly)
//                    System.out.println("closestFriendly == null");
//                    if (DEBUG) {
//                        throw new IllegalStateException("closestFriendly == null");
//                    }
                }
            }
            for (Entity entity : selectedFriendlyShipsList) {
//                EntityProperties entityProperties = entityPropertiesMapper.get(entity);
                AIProperties aiProperties = aIPropertiesMapper.getSafe(entity);
                // By this time we shouldn't have to check for aiProperties == null because we checked previously.
                if (aiProperties == null) {
                    continue;
                }
                if (DEBUG) {
                    EntityProperties entityProperties = entityPropertiesMapper.get(entity);
                    System.out.println("HudManager: attackSelectedEnemy by ship: "
                            + entityProperties.getName() + " itemId: " + entityProperties.getItemId()
                            + " current state: " + aiProperties.getState()
                            + " attacking enemy: " + enemyEntityProperties.getName() + " itemId: " + enemyEntityProperties.getItemId());
                }
                setShipToFollowTargetState(aiProperties, currentEnemySelectionItemId);
            }
            if (!selectedFriendlyShipsList.isEmpty()) {
                aboveCrosshairOverlayElement.setCaption("Attacking selected enemy");
                aboveCrosshairCaptionCurrentTime = currentTimeMillis();
            }

        } else {
            aboveCrosshairOverlayElement.setCaption("No enemy selected");
            aboveCrosshairCaptionCurrentTime = currentTimeMillis();
        }
    }

    private final ArrayList<Entity> enemyShipList = new ArrayList<>();
    private final ArrayList<Entity> friendlyShipList = new ArrayList<>();

    public void defendPlayerShip() {
        Entity playerShip = WorldManager.getSingleton().getPlayerShip();
        if (playerShip == null) {
            return;
        }
        EntityProperties playerShipEntityProperties = entityPropertiesMapper.get(playerShip);
        ShipProperties playerShipShipProperties = shipPropertiesMapper.getSafe(playerShip);
        if (playerShipShipProperties == null) {
            System.out.println("playerShipShipProperties is null"); // TODO Should we crash?
            return;
        }
        long playerShipItemId = playerShipEntityProperties.getItemId();
        // Find all the enemies that are following the player ship to follow them.
        ImmutableBag<Entity> friendlyEntities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(
                ShipTeam.getAsString(playerShipShipProperties.getShipData().team));
        if (friendlyEntities.isEmpty()) {
            return;
        }
//        friendlyShipsList.clear();
//        for (Entity friendlyEntity : friendlyEntities) {
//            friendlyShipsList.add(friendlyEntity);
//        }

        ImmutableBag<Entity> enemyEntities = GameWorld.getWorld().getManager(GroupManager.class).getEntities(
                ShipTeam.getOtherTeamAsString(playerShipShipProperties.getShipData().team));
        if (enemyEntities.isEmpty()) {
            return;
        }
        enemyShipList.clear();
        for (Entity enemyEntity : enemyEntities) {
            AIProperties aiProperties = aIPropertiesMapper.getSafe(enemyEntity);
            if (aiProperties == null) {
                continue;
            }
            if (aiProperties.getState() == AIProperties.AIState.FOLLOW_PLAYER && aiProperties.getFollowedShip() == playerShipItemId) {
                enemyShipList.add(enemyEntity);
            }
        }
        if (enemyShipList.isEmpty()) {
            return;
        }
        if (friendlyEntities.size() - 1 <= enemyShipList.size()) { // Take into account that the playerShip is also among friendlies.
            // One to one. Try to map based on distance.
            // If we have less friendly ships then simply go after the closest enemy ships.
            // TODO maybe also classify the threat level based on enemy type.
            boolean classifyBasedOnThreat = friendlyEntities.size() < enemyShipList.size();
            for (int i = 0; i < friendlyEntities.size(); ++i) {
                Entity friendlyEntity = friendlyEntities.get(i);
                EntityProperties friendlyEntityProperties = entityPropertiesMapper.get(friendlyEntity);
                if (friendlyEntityProperties.getItemId() == playerShipItemId) {
                    continue;
                }
                friendlyEntityProperties.getNode().getPosition(currentPos);

                Entity closestEnemy = getClosestEntity(enemyShipList, currentPos, otherPos, distVec);
                if (closestEnemy == null) {
                    // Should this be possible?
                    continue;
                }
                AIProperties friendlyAIProperties = aIPropertiesMapper.getSafe(friendlyEntity);
                // By this time we shouldn't have to check for friendlyAIProperties == null because we checked previously.
                if (friendlyAIProperties == null) {
                    continue;
                }
                EntityProperties enemyEntityProperties = entityPropertiesMapper.get(closestEnemy);
                setShipToFollowTargetState(friendlyAIProperties, enemyEntityProperties.getItemId());
                enemyShipList.remove(closestEnemy);
                aboveCrosshairOverlayElement.setCaption("Defender One protected!");
                aboveCrosshairCaptionCurrentTime = currentTimeMillis();
            }
        } else {
            // If we have more friendlies then distribute, ideally based on health level.
            friendlyShipList.clear();
            for (Entity friendlyEntity : friendlyEntities) {
                friendlyShipList.add(friendlyEntity);
            }
            while (!friendlyShipList.isEmpty()) {
                Entity friendlyEntity = friendlyShipList.get(friendlyShipList.size() - 1);
                EntityProperties friendlyEntityProperties = entityPropertiesMapper.get(friendlyEntity);
                if (friendlyEntityProperties.getItemId() == playerShipItemId) {
                    friendlyShipList.remove(friendlyShipList.size() - 1);
                    continue;
                }
                friendlyEntityProperties.getNode().getPosition(currentPos);
                Entity closestEnemy = getClosestEntity(enemyShipList, currentPos, otherPos, distVec);
                if (closestEnemy == null) {
                    // Should this be possible?
                    friendlyShipList.remove(friendlyShipList.size() - 1);
                    continue;
                }
                AIProperties friendlyAIProperties = aIPropertiesMapper.getSafe(friendlyEntity);
                // By this time we shouldn't have to check for friendlyAIProperties == null because we checked previously.
                if (friendlyAIProperties == null) {
                    friendlyShipList.remove(friendlyShipList.size() - 1);
                    continue;
                }
                EntityProperties enemyEntityProperties = entityPropertiesMapper.get(closestEnemy);
                setShipToFollowTargetState(friendlyAIProperties, enemyEntityProperties.getItemId());
                friendlyShipList.remove(friendlyShipList.size() - 1);
                aboveCrosshairOverlayElement.setCaption("Defender One protected!");
                aboveCrosshairCaptionCurrentTime = currentTimeMillis();
            }
        }
    }

    private static void setShipToFollowTargetState(AIProperties friendlyAIProperties, long enemyEntityProperties) {
        if (friendlyAIProperties.getState() == AIProperties.AIState.SEEK_CLOSEST_PLAYER) {
            friendlyAIProperties.setPatrolling(false);
            friendlyAIProperties.setPatrollingRotationStarted(false);
            friendlyAIProperties.setFollowedShip(enemyEntityProperties);
            friendlyAIProperties.setState(AIProperties.AIState.FOLLOW_PLAYER);
        } else if (friendlyAIProperties.getState() == AIProperties.AIState.FOLLOW_PLAYER) {
            friendlyAIProperties.setFollowedShip(enemyEntityProperties);
        }
    }

    private Entity getClosestEntity(ArrayList<Entity> shipList, ENG_Vector4D currentPos, ENG_Vector4D otherPos, ENG_Vector4D distVec) {
        Entity closestEnemy = null;
        float currentMinLen = Float.MAX_VALUE;
        // Find closest enemy.
        for (int j = 0; j < shipList.size(); ++j) {
            Entity enemyEntity = shipList.get(j);
            EntityProperties enemyEntityProperties = entityPropertiesMapper.get(enemyEntity);
            enemyEntityProperties.getNode().getPosition(otherPos);
            otherPos.sub(currentPos, distVec);
            if (j == 0) {
                currentMinLen = distVec.squaredLength();
                closestEnemy = enemyEntity;
            } else {
                if (distVec.squaredLength() < currentMinLen) {
                    currentMinLen = distVec.squaredLength();
                    closestEnemy = enemyEntity;
                }
            }
        }
        return closestEnemy;
    }

    public void updateThirdPersonCameraPosition() {
        if (!MainApp.getGame().isThirdPersonCamera()) return;

        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip == null) return;
        ComponentMapper<CameraProperties> cameraPropertiesComponentMapper =
                worldManager.getCameraPropertiesComponentMapper();
        ComponentMapper<EntityProperties> entityPropertiesComponentMapper =
                worldManager.getEntityPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesComponentMapper.get(playerShip);
        CameraProperties cameraProperties = cameraPropertiesComponentMapper.get(playerShip);

        ENG_Vector4D cameraPosition = cameraProperties.getUnadjustedCameraPosition();//cameraProperties.getNode().getPosition();

        ENG_AxisAlignedBox aabb = PhysicsUtility.getAxisAlignedBox(entityProperties.getRigidBody());
        ENG_Vector4D playerShipCenter = aabb.getCenter();
        long playerShipItemId = entityProperties.getItemId();

        AllHitsRayResultCallback callback = new AllHitsRayResultCallback(toVector3(playerShipCenter), toVector3(cameraPosition));
        worldManager.getDynamicWorld().rayTest(toVector3(playerShipCenter), toVector3(cameraPosition), callback);

        if (callback.hasHit()) {
            ENG_Vector4D diff = cameraPosition.subAsVec(playerShipCenter);
            float distance = diff.length();
            btCollisionObjectConstArray collisionObjects = callback.getCollisionObjects();
            int collisionObjectsLen = collisionObjects.size();
            ArrayList<RaySceneQueryResultEntry> rayQueryResultsList = new ArrayList<>();
            for (int i = 0; i < collisionObjectsLen; ++i) {
                btCollisionObject btCollisionObject = collisionObjects.atConst(i);
                ENG_Item item = null;
                if (EntityContactListener.isEntityRigidBody(btCollisionObject.getUserPointer())) {
                    EntityRigidBody eEntityRigidBody = (EntityRigidBody) btCollisionObject;
                    Entity e = eEntityRigidBody.getEntity();
                    EntityProperties collidedEntityProperties = entityPropertiesMapper.get(e);
                    item = collidedEntityProperties.getItem();
                } else if (EntityContactListener.isStaticEntityRigidBody(btCollisionObject.getUserPointer())) {
                    StaticEntityRigidBody eEntityRigidBody = (StaticEntityRigidBody) btCollisionObject;
                    Entity e = eEntityRigidBody.getEntity();
                    StaticEntityProperties collidedEntityProperties = worldManager.getStaticEntityPropertiesComponentMapper().get(e);
                    item = collidedEntityProperties.getItem();
                } else if (EntityContactListener.isInvisibleWall(btCollisionObject.getUserPointer())) {
                    // TODO should we collide with invisible walls or ignore them. For now ignore them.
                }
                if (item != null) {
                    RaySceneQueryResultEntry entry = new RaySceneQueryResultEntry();
                    entry.distance = distance * callback.getHitFractions().atConst(i);
//                        System.out.println(i + " hit distance: " + entry.distance + " fraction: " + callback.getHitFractions().atConst(i));
                    entry.movable = item;
                    rayQueryResultsList.add(entry);
                }
            }
            // AllHitsRayResultCallback does not guarantee that the results are sorted by distance.
            Collections.sort(rayQueryResultsList);

            // Find where the player ship comes in the ordered by distance list.
            float distanceFromShip = getDistanceFromShip(rayQueryResultsList, playerShipItemId);
            // Below code assumes the ray casting starts from camera position to player ship.
//            if (playerShipPositionInDistanceList == -1 || playerShipPositionInDistanceList == 0) {
//                cameraProperties.resetCollision();
//                return;
//            }

            // TODO don't just check for the closest hit to the player position. Should also check for AABB size.
//            int index = playerShipPositionInDistanceList - 1;
//            Entity closestEntity = null;
//            RaySceneQueryResultEntry raySceneQueryResultEntry = null;
//            do {
//                raySceneQueryResultEntry = rayQueryResultsList.get(index);
//                closestEntity = worldManager.getEntityByItemId(raySceneQueryResultEntry.movable.getId());
//                if (closestEntity != null) {
//                    break;
//                }
//            } while ((--index) >= 0);
//            if (closestEntity == null) {
//                cameraProperties.resetCollision();
//                return;
//            }
//            float distanceFromShip = distance - raySceneQueryResultEntry.distance;
            cameraProperties.setCollisionDistance(Math.max(distanceFromShip - 10.0f, 0));
            /*EntityProperties closestEntityProperties = entityPropertiesComponentMapper.get(closestEntity);
            Vector3 min = new Vector3();
            Vector3 max = new Vector3();
            closestEntityProperties.getRigidBody().getAabb(min, max);
            ENG_AxisAlignedBox box = new ENG_AxisAlignedBox(toVector4DAsPt(min), toVector4DAsPt(max));
            // Get closest corner to player ship in order to move the camera in front of it.
            box.getAllCorners(corners);
            int closestCorner = -1;
            float closestDistance = Float.MAX_VALUE;
            for (int i = 0; i < corners.length; ++i) {
                float dist = playerShipCenter.squaredDistance(corners[i]);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestCorner = i;
                }
            }
            ENG_Vector4D closestCornerPosition = corners[closestCorner];
            cameraProperties.setCollisionClosestPoint(closestCornerPosition);
            cameraProperties.setCollisionDistance(Math.max(closestDistance - 10.0f, 0));*/
        } else {
            cameraProperties.resetCollision();
        }

    }

    private static float getDistanceFromShip(ArrayList<RaySceneQueryResultEntry> rayQueryResultsList, long playerShipItemId) {
        int playerShipPositionInDistanceList = -1;
        for (int i = 0, rayQueryResultsListSize = rayQueryResultsList.size(); i < rayQueryResultsListSize; i++) {
            RaySceneQueryResultEntry raySceneQueryResultEntry = rayQueryResultsList.get(i);
            if (playerShipItemId == raySceneQueryResultEntry.movable.getId()) {
                playerShipPositionInDistanceList = i;
                break;
            }
        }
        RaySceneQueryResultEntry collisionEntry = rayQueryResultsList.get(playerShipPositionInDistanceList + 1);
        float distanceFromShip = collisionEntry.distance;
        return distanceFromShip;
    }

    public void updateEnemySelection(ENG_SceneNode node, ShipProperties playerShipProperties) {

        if (!visible) {
            return;
        }
//        System.out.println("updateEnemySelection called");
        initializeCornersVectors();

        node.getPosition(rayOrigin);
        node.getOrientation().mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, rayDir);
//        ENG_RaySceneQuery rayQuery = sceneManager.createRayQuery(new ENG_Ray(rayOrigin, rayDir));


        ArrayList<RaySceneQueryResultEntry> rayQueryResultsList = null;
        RaySceneQueryResultEntry raySceneQueryResultEntry = null;
        WorldManager worldManager = WorldManager.getSingleton();
        if (USE_BULLET_RAY_TEST) {
            rayDir.mul(RAY_CAST_DISTANCE);
            rayDir.addInPlace(rayOrigin);
            AllHitsRayResultCallback callback = new AllHitsRayResultCallback(toVector3(rayOrigin), toVector3(rayDir));
            PhysicsUtility.rayTest(WorldManagerBase.getSingleton().getDynamicWorld(), rayOrigin, rayDir, callback);
            if (callback.hasHit()) {
                rayQueryResultsList = new ArrayList<>();
                ENG_Vector4D diff = rayDir.subAsVec(rayOrigin);
                float distance = diff.length();
                btCollisionObjectConstArray collisionObjects = callback.getCollisionObjects();
                int collisionObjectsLen = collisionObjects.size();
                for (int i = 0; i < collisionObjectsLen; ++i) {
                    btCollisionObject btCollisionObject = collisionObjects.atConst(i);
                    ENG_Item item = null;
                    if (EntityContactListener.isEntityRigidBody(btCollisionObject.getUserPointer())) {
                        EntityRigidBody eEntityRigidBody = (EntityRigidBody) btCollisionObject;
                        Entity e = eEntityRigidBody.getEntity();
                        EntityProperties entityProperties = entityPropertiesMapper.get(e);
                        item = entityProperties.getItem();
                    }
                    if (item != null) {
                        RaySceneQueryResultEntry entry = new RaySceneQueryResultEntry();
                        entry.distance = distance * callback.getHitFractions().atConst(i);
//                        System.out.println(i + " hit distance: " + entry.distance + " fraction: " + callback.getHitFractions().atConst(i));
                        entry.movable = item;
                        entry.collisionObject = btCollisionObject;
                        rayQueryResultsList.add(entry);
                    }
                }
                // AllHitsRayResultCallback does not guarantee that the results are sorted by distance.
                Collections.sort(rayQueryResultsList);

                // Get closest object that is a ship to set for camera orientation.
                if (MainApp.getGame().isThirdPersonCamera()) {
                    float closestShip = Float.MAX_VALUE;
                    for (RaySceneQueryResultEntry entry : rayQueryResultsList) {
                        if (EntityContactListener.isEntityRigidBody(entry.collisionObject.getUserPointer())) {
                            EntityRigidBody eEntityRigidBody = (EntityRigidBody) entry.collisionObject;
                            Entity e = eEntityRigidBody.getEntity();
                            EntityProperties entityProperties = entityPropertiesMapper.get(e);
                            if (entityProperties.getObjectType() == LevelObject.LevelObjectType.FIGHTER_SHIP &&
                                    entry.movable != null &&
                                    worldManager.getEntityByItemId(entry.movable.getId()) != worldManager.getPlayerShip()) {
                                closestShip = entry.distance;
                                break;
                            }
                        }
                    }
                    ComponentMapper<CameraProperties> cameraPropertiesComponentMapper = worldManager.getCameraPropertiesComponentMapper();
                    CameraProperties cameraProperties = cameraPropertiesComponentMapper.getSafe(worldManager.getPlayerShip());
                    if (cameraProperties != null) {
                        if (closestShip < Float.MAX_VALUE) {
                            cameraProperties.setClosestTargetDistance(closestShip);
                        } else {
                            cameraProperties.resetClosestTargetDistance();
                        }
                    }
                }
            } else {
                if (MainApp.getGame().isThirdPersonCamera()) {
                    // Also reset if we are hitting nothing ahead.
                    ComponentMapper<CameraProperties> cameraPropertiesComponentMapper = worldManager.getCameraPropertiesComponentMapper();
                    CameraProperties cameraProperties = cameraPropertiesComponentMapper.getSafe(worldManager.getPlayerShip());
                    if (cameraProperties != null) {
                        cameraProperties.resetClosestTargetDistance();
                    }
                }
            }
        } else {
            // Check if the results have arrived and process them, while creating a new command.
            // Here we must avoid a possible bug that could happen if the rendering thread is 2
            // frames behind the main thread. You would never get to see ray scene query results because
            // they would always be overwritten just as they were coming in and rayQueryResultsList would
            // always be empty.
//        long createRaySceneQueryBeginTime = System.nanoTime();
            createRaySceneQueryPair();
//        System.out.println("createRaySceneQueryPair time: " + ((System.nanoTime() - createRaySceneQueryBeginTime) / 1000000.0f));
            /*
             * if (list.isEmpty()) { throw new
             * IllegalArgumentException("There must be at least one " +
             * "intersection with the skybox."); }
             */
            // Get the first ship that is not us
//        long raySceneQueryResultEntryBeginTime = System.nanoTime();
//        System.out.println("raySceneQueryResultEntry size: " + rayQueryResultsList.size());
            RaySceneQueryPair raySceneQueryPair = raySceneQueryPairList.get(0);
            rayQueryResultsList = raySceneQueryPair.rayQueryResultsList;
            if (raySceneQueryPair.rayQueryResultsArrived) {
                raySceneQueryPairList.remove(0);
            }
        }
        if (rayQueryResultsList != null) {
            for (RaySceneQueryResultEntry entry : rayQueryResultsList) {
                // We can have a result with a null movable if the ray query execution that came
                // back from the native side gave us a pointer to a deleted object on the java
                // side.
                // Ignore if it's the player ship or a drawn waypoint for debugging or a static object.
                if ((entry.movable != null) &&
                        (worldManager.getEntityByItemId(entry.movable.getId()) != worldManager.getPlayerShip()) &&
                        (!worldManager.getWaypointId(entry.movable.getId())) &&
                        (!worldManager.getStaticObjectId(entry.movable.getId()))) {
                    raySceneQueryResultEntry = entry;
                    break;
                }
            }
        }
//        System.out.println("raySceneQueryResultEntry time: " + (System.nanoTime() - raySceneQueryResultEntryBeginTime) / 1000000.0f);
//        System.out.println("raySceneQueryPairList size: " + raySceneQueryPairList.size());

        // Must be here before checking the raySceneQueryResultEntry
        // We want to avoid the case in which the player moves the crosshair out
        // of an object just as we switch an weapon and then switches to
        // previous
        // weapon just at the moment when it reenters an object's space.
//        long entityCheckBeginTime = System.nanoTime();
        checkWeaponChanged();

        createViewProjectionMatrix(camera);
        if (!matricesConcatenated) {
            return;
//                throw new IllegalStateException(
//                        "The view matrix and "
//                                + "projection matrix must be concatenated before calling "
//                                + "this method. Make sure that updateRadarData() is "
//                                + "called first to setup the viewprojection matrix. "
//                                + "This is a hack for optimization so we don't setup "
//                                + "the matrices for every method call");
        }

        boolean insideCrosshair = false;
        if (raySceneQueryResultEntry == null) {
            // throw new
            // NullPointerException("We should have found at least the skybox");
            // This is the skybox we have hit so make sure no enemy selection
            // active

            // NEW AND IMPROVED SELECTION METHOD!
            // If we have a ray query returning a result with a collided ship, just because we no
            // longer have that ship being hit by the ray query in the following frames
            // doesn't mean that we should no longer consider the current ship the selected enemy.
            // Maybe the ship is still in the square formed by the selection reticle and as long
            // as that is the case we should consider it the current selected enemy.
            // If another ship gets hit by the ray query then the timer resets and we consider
            // that ship the new current selected enemy.

            if (WeaponType.getWeaponCrosshairType(currentWeaponType) == CrosshairType.SELECTION) {
                if (currentPotentialSelectedEnemy != null && !currentPotentialSelectedEnemy.isDestroyed()) {
                    ENG_Vector4D center = currentPotentialSelectedEnemy.getWorldAABB().getCenter();
                    ENG_Vector4D screenSpaceCenter = new ENG_Vector4D(true);
                    transformByProjectionMat(viewProjMatrix, center, screenSpaceCenter);

                    screenSpaceCenter.x = (screenSpaceCenter.x + 1.0f) * 0.5f;
                    screenSpaceCenter.y = (screenSpaceCenter.y + 1.0f) * 0.5f;

                    ENG_Vector2D screenSpaceToPixels = ENG_Utility.convertFromScreenSpaceToPixels(screenSpaceCenter.x, screenSpaceCenter.y);

                    // Now we actually have the center in screen space.
                    crossRect.set(crosshairOuter.getLeft(), crosshairOuter.getTop(), crosshairOuter.getLeft() + crosshairOuter.getWidth(),
                            crosshairOuter.getTop() + crosshairOuter.getHeight());
                    if (crossRect.inside(screenSpaceToPixels.x, screenSpaceToPixels.y)) {
                        insideCrosshair = true;
                    }
                }
            }
            if (!insideCrosshair) {
                currentPotentialSelectedEnemy = null;
            }
            if (!insideCrosshair && resetUpdateEnemySelection(playerShipProperties)) return;
        }

        // Ugly update to reflect the new enemy selection when cross over with
        // crosshair. raySceneQueryResultEntry is guaranteed not null for CrosshairType.SELECTION.
        if (WeaponType.getWeaponCrosshairType(currentWeaponType) == CrosshairType.SELECTION) {
            // We have a ship in our current crosshair.
            // raySceneQueryResultEntry may be null.
            updateCrosshairColor(raySceneQueryResultEntry, insideCrosshair);
        }

        // ENG_MovableObject skybox =
        // sceneManager.getSkyboxNode().getAttachedObject("skybox");
        Entity entity = null;
        if (raySceneQueryResultEntry != null) {
            // The native side returns a pointer to the native movable object.
            // From that we must map to the entity id on the vm side.
            long pointer = raySceneQueryResultEntry.movable.getPointer();
            entity = worldManager.getEntityByItemId(raySceneQueryResultEntry.movable.getId());
//            System.out.println("raySceneQueryResultEntry != null and entity " + ((entity != null) ? "not null" : "null"));
        } else if (insideCrosshair && currentPotentialSelectedEnemy != null) {
            // We may still have an entity.
            entity = worldManager.getEntityByItemId(currentPotentialSelectedEnemy.getId());
        }
//        System.out.println("entityCheck time: " + (System.nanoTime() - entityCheckBeginTime) / 1000000.0f);
        if (entity == null) {
//            long entityCheckNullBeginTime = System.nanoTime();
            if (WeaponType.getWeaponCrosshairType(currentWeaponType) == CrosshairType.CROSSHAIR) {
                // There is still a chance that there is a selected ship if
                // the crosshair intersects the cross
//                long getDerivedBeginTime = System.nanoTime();
                float crossLeft = cross.getLeft();
                float crossTop = cross.getTop();
                float crossRight = crossLeft + cross.getWidth();
                float crossBottom = crossTop + cross.getHeight();

                float crosshairCrossLeft = crosshairCross.getLeft();
                float crosshairCrossTop = crosshairCross.getTop();
                float crosshairCrossRight = crosshairCrossLeft + crosshairCross.getWidth();
                float crosshairCrossBottom = crosshairCrossTop + crosshairCross.getHeight();
//                System.out.println("getDerived time: " + (System.nanoTime() - getDerivedBeginTime) / 1000000.0f);

//                long intersectionCheckBeginTime = System.nanoTime();
                crossRect.set(crossLeft, crossTop, crossRight, crossBottom);
                crosshairCrossRect.set(crosshairCrossLeft, crosshairCrossTop, crosshairCrossRight, crosshairCrossBottom);
                crossRect.intersect(crosshairCrossRect, crosshairIntersectionRect);
                if (!crosshairIntersectionRect.isNull()) {
                    entity = worldManager.getEntityByGameEntityId(entityFollowedByCross);
                }
//                System.out.println("intersectionCheck time: " + (System.nanoTime() - intersectionCheckBeginTime) / 1000000.0f);
            }
//            long enemySelectionOverlayHideBeginTime = System.nanoTime();
            enemySelectionOverlay.hide();
//            System.out.println("enemySelectionOverlayHide time: " + (System.nanoTime() - enemySelectionOverlayHideBeginTime) / 1000000.0f);
//            System.out.println("entityCheckNull time: " + (System.nanoTime() - entityCheckNullBeginTime) / 1000000.0f);
        }
        if (entity != null) {
//            long entityCheckNotNullBeginTime = System.nanoTime();
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
            ShipProperties otherShipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(entity);
            currentShipSelectionId = entityProperties.getEntityId();
//            System.out.println("Ship " + entityProperties.getName() + " intersected");

            // if we are using bullet then the ray test might get a collision response with a newly created item.
            // This new item does not yet have the native pointer set from the native side so we can't continue.
            if (!entityProperties.getItem().isNativePointerSet()) {
                return;
            }
            entityProperties.getItem().getWorldAABB().getAllCorners(corners);
            if (!entityProperties.isScannable()) {
                resetUpdateEnemySelection(playerShipProperties);
                return;
            }

            transformBoundingBoxToClipSpace(corners, viewProjMatrix, transCorners, leftTop, rightBottom);
            float width = rightBottom.x - leftTop.x;
            float height = rightBottom.y - leftTop.y;
            if (leftTop.x < 0.0f || leftTop.y < 0.0f || rightBottom.x > 1.0f
                    || rightBottom.y > 1.0f) {
                enemySelectionOverlay.hide();
//                System.out.println("enemySelection hidden");
                return;
            }

            enemySelection.setLeft(leftTop.x);
            enemySelection.setTop(leftTop.y);
            enemySelection.setWidth(width);
            enemySelection.setHeight(height);
//            System.out.println("enemySelection shown");
            enemySelectionOverlay.show();
            showHealth(entityProperties);

//            System.out.println("entityCheckNotNull time: " + (System.nanoTime() - entityCheckNotNullBeginTime) / 1000000.0f);
        }

    }

    private static void transformBoundingBoxToClipSpace(ENG_Vector4D[] corners, ENG_Matrix4 mat, ENG_Vector4D[] transCorners, ENG_Vector2D leftTop, ENG_Vector2D rightBottom) {
        for (int i = 0; i < corners.length; ++i) {
            transformByProjectionMat(mat, corners[i], transCorners[i]);
        }
        leftTop.set(transCorners[0]);
        for (int i = 1; i < corners.length; ++i) {
            ENG_Vector4D test = transCorners[i];
            if (test.x < leftTop.x) {
                leftTop.x = test.x;
            }
            if (test.y > leftTop.y) {
                leftTop.y = test.y;
            }
        }
        rightBottom.set(transCorners[0]);
        for (int i = 1; i < corners.length; ++i) {
            ENG_Vector4D test = transCorners[i];
            if (test.x > rightBottom.x) {
                rightBottom.x = test.x;
            }
            if (test.y < rightBottom.y) {
                rightBottom.y = test.y;
            }
        }

        // Now we have the limit rectangle that we must transform in clip
        // space
        leftTop.x = (leftTop.x + 1.0f) * 0.5f;
        leftTop.y = 1.0f - (leftTop.y + 1.0f) * 0.5f;
        rightBottom.x = (rightBottom.x + 1.0f) * 0.5f;
        rightBottom.y = 1.0f - (rightBottom.y + 1.0f) * 0.5f;
    }

    private static void transformByProjectionMat(ENG_Matrix4 mat, ENG_Vector4D corner, ENG_Vector4D transCorner) {
        mat.transform(corner, transCorner);
        float winv = 1.0f / transCorner.w;
        transCorner.x *= winv;
        transCorner.y *= winv;
    }

    private void showHealth(EntityProperties entityProperties) {
        if (entityProperties.isShowHealth()) {
            if (DEBUG) {
                enemySelectionText.setCaption(entityProperties.getName() + ":" + entityProperties.getHealth());
            } else {
                enemySelectionText.setCaption(String.valueOf(entityProperties.getHealth()));
            }
//                enemySelectionText.setWidth(0.0f);
//                enemySelectionText._update();
            // Now we have the real width
//                enemySelectionText.setLeft(width / 2 - enemySelectionText.getWidth() / 2);
//                enemySelectionText.setTop(height);
            enemySelectionText.show();
        } else {
            enemySelectionText.hide();
//                System.out.println("enemySelection hidden");
        }
    }

    private void initializeCornersVectors() {
        if (!cornersVectorsInitialized) {
            for (int i = 0; i < corners.length; ++i) {
                corners[i] = new ENG_Vector4D(true);
                transCorners[i] = new ENG_Vector4D(true);
                cameraSpaceTransCorners[i] = new ENG_Vector4D(true);
            }
            cornersVectorsInitialized = true;
        }
    }

    private void createRaySceneQueryPair() {
        ENG_RaySceneQuery rayQuery = ENG_NativeCalls.createRayQuery(new ENG_Ray(rayOrigin, rayDir), true);
        RaySceneQueryPair raySceneQueryPair = new RaySceneQueryPair();
        raySceneQueryPair.rayQuery = rayQuery;
        raySceneQueryPairList.add(raySceneQueryPair);
        rayQuery.setSortByDistance(true);
        raySceneQueryPair.rayQueryResultsList = rayQuery.execute(raySceneQueryPair);

        // The destruction of the ray query now happens in the native side and the FrameEndListener().
//        ENG_NativeCalls.destroyRayQuery(rayQueryList, true);
    }

    private boolean resetUpdateEnemySelection(ShipProperties playerShipProperties) {
        enemySelectionOverlay.hide();
        currentShipSelectionId = -1;
        // If we didn't hit anything also reset the selection color
        setCrosshairColor(defaultCrosshairColor);
        currentSelectedEnemy = null;
        currentPotentialSelectedEnemy = null;
        currentSelectedEnemyFollowable = false;
        playerShipProperties.resetCurrentSelectedEnemy();
        return WeaponType.getWeaponCrosshairType(currentWeaponType) == CrosshairType.SELECTION;
    }

    private static class EnemyCrossData implements ENG_PoolObject {
        public Entity entity;
        public ENG_Boolean visibility = new ENG_Boolean();
        public ENG_Boolean visibilityDataSet = new ENG_Boolean();

        public EnemyCrossData(Entity entity) {
            this.entity = entity;
        }

        public EnemyCrossData() {

        }

        public void reset() {
            // We cannot just set the values of the fields as we wish since they might have been
            // already passed to ENG_NativeCalls functions. Read the following comment from
            // radarVisibilityDataQueue.add() below:
            //
            // The problem with this is that we might add to the pool an entity for which the
            // visibilityDataSet has already been passed as a pointer to a ENG_NativeCalls function.
            // So we could end up as getting an object from the queue whose pointer is shared with
            // an ENG_NativeCalls function that then overrides the visibilityDataSet when it returns.
            // In this case we are dead since visibilityDataSet will become true with no
            // actual data behind it.
            visibility = new ENG_Boolean();
            visibilityDataSet = new ENG_Boolean();
        }
    }

    private final ArrayList<Entity> entityListForCrossGuide = new ArrayList<>();
    private final HashMap<Long, ArrayList<EnemyCrossData>> enemyCrossDataVisibilityMap = new HashMap<>();
    private final HashMap<Long, EnemyCrossData> currentEnemyCrossVisibilityMap = new HashMap<>();
    private final HashMap<Long, Entity> currentEnemySelectionMap = new HashMap<>();
    private final ArrayList<Entity> enemySelectionOrderedByDistance = new ArrayList<>();
    private long currentEnemySelectionItemId = -1;
    private int currentEnemySelection = -1;
    private static final long ENEMY_SELECTED_NO_ENEMY_DISPLAY_TIME = 1000;
    private long enemySelectedNoEnemyBeginTime;
    private boolean enemySelectedNoEnemySelected;

    private static final int DEFAULT_ENEMY_CROSS_DATA_QUEUE_LENGTH = 256;
    private final ENG_ObjectPool<EnemyCrossData> enemyCrossDataQueue = new ENG_ObjectPool<>(new ENG_ObjectFactory<EnemyCrossData>() {
        @Override
        public EnemyCrossData create() {
            return new EnemyCrossData();
        }

        @Override
        public void destroy(EnemyCrossData obj) {

        }
    }, DEFAULT_ENEMY_CROSS_DATA_QUEUE_LENGTH, true, "enemyCrossDataQueue");

    public void resetCurrentEnemyCrossDataVisibilityIds() {
        currentEnemyCrossVisibilityMap.clear();
    }

    public void updateEnemyCrossGuide(Entity entity) {
        if (!visible) {
            return;
        }

        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
        ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);

        // TODO replace with rayTest from Bullet.
        if (worldManager.getPlayerShipData().team != shipProperties.getShipData().team
                && !WeaponType.isHomingMissileType(currentWeaponType)
                && entityProperties.getItem().getWorldAABB().isFinite()) {
            EnemyCrossData enemyCrossData = enemyCrossDataQueue.get();
            enemyCrossData.entity = entity;
            camera.isVisibleNative(entityProperties.getItem().getWorldAABB(),
                    enemyCrossData.visibility, enemyCrossData.visibilityDataSet);
            currentEnemyCrossVisibilityMap.put(entityProperties.getItemId(), enemyCrossData);
        }

    }

    public void checkCurrentEnemySelectedStillAvailable() {
        if (!visible) {
            return;
        }

        if (currentEnemySelectionItemId != -1) {
            Entity entity = currentEnemySelectionMap.get(currentEnemySelectionItemId);
            if (entity == null) {
                currentEnemySelectionItemId = -1;
                currentEnemySelection = -1;
                hideEnemySelectionOutsideScreenOverlay();
            }
        }
    }

    private final ENG_Vector4D orderEntitiesByDistancePosition = new ENG_Vector4D();
    private final Comparator<Entity> orderEntitiesByDistanceComparator = new Comparator<Entity>() {

        private float getDistance(Entity e) {
            ComponentMapper<EntityProperties> entityPropertiesComponentMapper =
                    WorldManager.getSingleton().getEntityPropertiesComponentMapper();
            EntityProperties entityProperties = entityPropertiesComponentMapper.get(e);
            entityProperties.getNode().getPosition(orderEntitiesByDistancePosition);
            return playerShipPosition.squaredDistance(orderEntitiesByDistancePosition);
        }
        /** @noinspection ComparatorMethodParameterNotUsed*/
        @Override
        public int compare(Entity lhs, Entity rhs) {
            if (getDistance(lhs) < getDistance(rhs)) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    public void orderEnemyShipsByDistanceFromPlayerShip() {

        final WorldManager worldManager = WorldManager.getSingleton();
        long playerShipId = worldManager.getPlayerShipEntityId();
        if (playerShipId != -1) {
            Entity entity = worldManager.getShipByGameEntityId(playerShipId);
            if (entity != null) {
                ComponentMapper<EntityProperties> entityPropertiesComponentMapper = worldManager.getEntityPropertiesComponentMapper();
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);
                if (!entityProperties.isDestroyed()) {
                    entityProperties.getNode().getPosition(playerShipPosition);
                    Collections.sort(enemySelectionOrderedByDistance, orderEntitiesByDistanceComparator);
                }
            }
        }
    }

    public void updateCurrentEnemyList(Entity entity) {
        if (!visible) {
            return;
        }

        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
        ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);

        if (worldManager.getPlayerShipData().team != shipProperties.getShipData().team
                && entityProperties.getItem().getWorldAABB().isFinite()) {
            currentEnemySelectionMap.put(entityProperties.getItemId(), entity);
            enemySelectionOrderedByDistance.add(entity);
        }
    }

    public void resetCurrentEnemyList() {
        currentEnemySelectionMap.clear();
        enemySelectionOrderedByDistance.clear();
    }

    private final ENG_Vector2D enemySelectedBoxDiff = new ENG_Vector2D();
    private final ENG_Vector2D enemyPosition = new ENG_Vector2D();
//    private final ENG_Vector4D playerShipPos = new ENG_Vector4D(true);
    private final ENG_Vector4D enemyShipPos = new ENG_Vector4D(true);
    private final ENG_Vector4D enemyShipPosCameraSpace = new ENG_Vector4D(true);
//    private final ENG_Vector4D playerShipFrontVec = new ENG_Vector4D();

    public void updateCurrentEnemySelectedShip() {
        if (!visible) {
            return;
        }
//        System.out.println("updateCurrentEnemySelectedShip called");

        Entity playerShip = WorldManager.getSingleton().getPlayerShip();
        if (playerShip == null) {
            return;
        }

        if (currentEnemySelection == -1) {
            hideEnemySelectionOutsideScreenOverlay();
            if (enemySelectedNoEnemySelected) {
                enemySelectedNoEnemySelected = false;
                enemySelectedNoEnemyBeginTime = currentTimeMillis();
            }
            return;
        }
        enemySelectedNoEnemySelected = true;

        initializeCornersVectors();

        createViewProjectionMatrix(camera);
        if (!matricesConcatenated) {
            return;
//                throw new IllegalStateException(
//                        "The view matrix and "
//                                + "projection matrix must be concatenated before calling "
//                                + "this method. Make sure that updateRadarData() is "
//                                + "called first to setup the viewprojection matrix. "
//                                + "This is a hack for optimization so we don't setup "
//                                + "the matrices for every method call");
        }

        Entity currentSelectedEnemy = currentEnemySelectionMap.get(currentEnemySelectionItemId);
        if (currentSelectedEnemy == null) {
            // Maybe the enemy has been destroyed by somebody else or exited the level.
            resetCurrentEnemySelection();
            return;
        }
        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(currentSelectedEnemy);
        EntityProperties playerShipEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);
        ShipProperties playerShipShipProperties = worldManager.getShipPropertiesComponentMapper().get(playerShip);

        playerShipEntityProperties.getNode().getPosition(playerShipPosition);
        playerShipEntityProperties.getNode().getLocalInverseZAxis(playerShipFrontVec);
        entityProperties.getNode().getPosition(enemyShipPos);

        enemyShipPos.sub(playerShipPosition, distanceOtherPos);
        distanceOtherPos.normalize();
        float angleBetween = distanceOtherPos.angleBetween(playerShipFrontVec);

        if (USE_BULLET_COLLISION_SHAPE) {
            ENG_Vector4D position = entityProperties.getNode().getPosition();
            // TODO test if getting the aabb is correct and faster than the commented out part using the particular collision shapes.
            ENG_AxisAlignedBox aabb = PhysicsUtility.getAxisAlignedBox(entityProperties.getRigidBody());
            aabb.getAllCorners(corners);
//            btCollisionShape collisionShape = entityProperties.getCollisionShape();
//            switch (entityProperties.getCollisionShapeType()) {
//                case BOX: {
//                    btBoxShape box = (btBoxShape) collisionShape;
//                    ENG_AxisAlignedBox aabb = PhysicsUtility.getBoxCollisionShape(box, position);
//                    aabb.getAllCorners(corners);
//                }
//                    break;
//                case CAPSULE_Z: {
//                    btCapsuleShapeZ capsule = (btCapsuleShapeZ) collisionShape;
//                    ENG_AxisAlignedBox aabb = PhysicsUtility.getCapsuleCollisionShape(capsule, position);
//                    aabb.getAllCorners(corners);
//                }
//                    break;
//                case CAPSULE:
////                    break;
//                case CAPSULE_X:
////                    break;
//                case CYLINDER:
////                    break;
//                case BVH_TRIANGLE_MESH:
////                    break;
//                default:
//                    throw new IllegalStateException("Unexpected value: " + entityProperties.getCollisionShapeType());
//            }
        } else {
            entityProperties.getItem().getWorldAABB().getAllCorners(corners);
        }
//        if (!entityProperties.isScanable()) {
//            resetUpdateEnemySelection(playerShipProperties);
//            return;
//        }

        transformBoundingBoxToClipSpace(corners, viewProjMatrix, transCorners, leftTop, rightBottom);
        float width = rightBottom.x - leftTop.x;
        float height = rightBottom.y - leftTop.y;
        if (leftTop.x < 0.0f || leftTop.y < 0.0f || rightBottom.x > 1.0f
                || rightBottom.y > 1.0f || angleBetween > ENG_Math.HALF_PI) {
            enemySelectionOverlay.hide();
//                System.out.println("enemySelection hidden");
//            String s = "outside screen";
            viewMatrix.transform(enemyShipPos, enemyShipPosCameraSpace);
            if (enemyShipPosCameraSpace.z > 0.0f) {
                // The enemy ship is behind the camera.
                for (int i = 0; i < corners.length; ++i) {
                    viewMatrix.transform(corners[i], cameraSpaceTransCorners[i]);
                    if (cameraSpaceTransCorners[i].z > 0.0f) {
                        cameraSpaceTransCorners[i].z = -cameraSpaceTransCorners[i].z;
                    }
                }
                transformBoundingBoxToClipSpace(cameraSpaceTransCorners, projMatrix, transCorners, leftTop, rightBottom);
//                s += " enemyShipPosCameraSpace.z > 0.0f";

            }
//            System.out.println(s);

            // Take the center of the box.
            rightBottom.sub(leftTop, enemySelectedBoxDiff);
            enemySelectedBoxDiff.mul(0.5f);
            leftTop.add(enemySelectedBoxDiff, enemyPosition);
            // Check where it is relative to player. Transform to NDC.
            enemyPosition.x = enemyPosition.x * 2.0f - 1.0f;
            enemyPosition.y = - (enemyPosition.y * 2.0f - 1.0f);
//            System.out.println("enemyPosition: " + enemyPosition);
            enemyPosition.normalize();
            // Display the overlay on a circle around centre of screen.
            enemyPosition.mulInPlace(0.3f);
            enemyPosition.x = (enemyPosition.x + 1.0f) * 0.5f;
            enemyPosition.y = 1.0f - (enemyPosition.y + 1.0f) * 0.5f;

            float xPos = ENG_Math.floor(enemyPosition.x * windowWidth) - enemySelectionOutsideScreenHalfWidth;
            float yPos = ENG_Math.floor(enemyPosition.y * windowHeight) - enemySelectionOutsideScreenHalfHeight;

//            ENG_Vector2D enemySelectionOutsideScreenPos = new ENG_Vector2D(xPos, yPos);
//            System.out.println("enemySelectionOutsideScreenPos: " + enemySelectionOutsideScreenPos);

            enemySelectionOutsideScreen.setLeft(xPos);
            enemySelectionOutsideScreen.setTop(yPos);
//            enemySelectionOutsideScreen.setWidth(2.0f * enemySelectionOutsideScreenHalfWidth);
//            enemySelectionOutsideScreen.setHeight(2.0f * enemySelectionOutsideScreenHalfHeight);
            enemySelectionOutsideScreenOverlay.show();

            return;
        }

        if (angleBetween < ENG_Math.HALF_PI) {
            enemySelection.setLeft(leftTop.x);
            enemySelection.setTop(leftTop.y);
            enemySelection.setWidth(width);
            enemySelection.setHeight(height);
//            System.out.println("enemySelection shown");
            enemySelectionOverlay.show();
            enemySelectionOutsideScreenOverlay.hide();
            showHealth(entityProperties);
//            System.out.println("on screen");
        } else {
//            System.out.println("outside 90 deg");
        }

        // Enable aim assisting only if the ship is not spinning rapidly.
        if (MainApp.getGame().isAimAssistEnabled()) {
            ENG_InputManager inputManager = MainApp.getGame().getInputManager();
            InGameInputConvertorListener inputConvertorListener = (InGameInputConvertorListener) inputManager.getInputConvertorListener(inputManager.getCurrentInputStack().getInputConvertorListener());
            // Should we take into consideration that the result should be before applying the maxAngularVelocity?
            ENG_Vector3D shipTorqueImpulse = inputConvertorListener.getResult();
            // If you have homing then stick to the enemy position.
            if (WeaponType.isHomingMissileType(currentWeaponType)) {
                if (angleBetween < ANGLE_BETWEEN_CROSSHAIR_AND_TARGETED_ENEMY_FOR_AIM_ASSIST &&
                        shipTorqueImpulse.length() < SHIP_TORQUE_IMPULSE_FOR_AIM_ASSIST) {
                    if (!aimAssisting) {
                        System.out.println("HudManager: aimAssisting homing true");
                    }
                    aimAssisting = true;
                    Utility.rotateToPosition(playerShipFrontVec,
                            distanceOtherPos,
                            (float) AIM_ASSIST_ROTATION_STEPS,//APP_Game.AI_SYSTEM_INTERVAL,
                            playerShipEntityProperties,
                            playerShipShipProperties.getShipData().maxAngularVelocity);
                } else {
                    if (aimAssisting) {
                        System.out.println("HudManager: aimAssisting homing false");
                    }
                    aimAssisting = false;
                }
            } else {
                // Aim to where the aiming crosshair position is located.
                if (collisionPositionValid) {
                    invViewMatrix.transform(crossPositionInViewSpace, crossPositionInWorldSpace);
                    crossPositionInWorldSpace.sub(playerShipPosition, distanceCrossPos);
                    distanceCrossPos.normalize();
                    float angleBetweenShipFrontVecAndCrosshair = distanceCrossPos.angleBetween(playerShipFrontVec);
//                    System.out.println("HudManager: angleBetweenShipFrontVecAndCrosshair: " + angleBetweenShipFrontVecAndCrosshair);
                    if (angleBetweenShipFrontVecAndCrosshair < ANGLE_BETWEEN_CROSSHAIR_AND_TARGETED_ENEMY_FOR_AIM_ASSIST &&
                            shipTorqueImpulse.length() < SHIP_TORQUE_IMPULSE_FOR_AIM_ASSIST) {
                        if (!aimAssisting) {
                            System.out.println("HudManager: aimAssisting non-homing true");
                        }
                        aimAssisting = true;
                        Utility.rotateToPosition(playerShipFrontVec,
                                distanceCrossPos,
                                (float) AIM_ASSIST_ROTATION_STEPS,//APP_Game.AI_SYSTEM_INTERVAL,
                                playerShipEntityProperties,
                                playerShipShipProperties.getShipData().maxAngularVelocity);
                    } else {
                        if (aimAssisting) {
                            System.out.println("HudManager: aimAssisting non-homing false");
                        }
                        aimAssisting = false;
                    }
                }
            }
        } else {
            aimAssisting = false;
        }
    }

    public void updateEntityListForCrossGuide() {
        // Check that all the visibility data not belonging to the current frame is ignored.
        for (Iterator<Map.Entry<Long, ArrayList<EnemyCrossData>>> it = enemyCrossDataVisibilityMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, ArrayList<EnemyCrossData>> next = it.next();
            EnemyCrossData enemyCrossData = currentEnemyCrossVisibilityMap.get(next.getKey());
            if (enemyCrossData == null) {
                // We no longer care about what the native side returns since the object is no longer in front of us.
                for (EnemyCrossData crossData : next.getValue()) {
                    enemyCrossDataQueue.add(crossData);
                }

                it.remove();
            }
        }

        // Add the current frame data to the radar visibility map.
        for (Map.Entry<Long, EnemyCrossData> entry : currentEnemyCrossVisibilityMap.entrySet()) {
            Long itemId = entry.getKey();
            EnemyCrossData enemyCrossData = entry.getValue();
            ArrayList<EnemyCrossData> enemyCrossDataList = enemyCrossDataVisibilityMap.get(itemId);
            if (enemyCrossDataList == null) {
                enemyCrossDataList = new ArrayList<>();
                enemyCrossDataVisibilityMap.put(itemId, enemyCrossDataList);
            }
            enemyCrossDataList.add(enemyCrossData);

        }

//        ArrayList<RadarVisibilityData> radarVisibilityDataToRemove = new ArrayList<>();
        // Check if any data has been updated from the native side, and if yes, send it back to native to
        // draw the updated radar.
        for (Iterator<Map.Entry<Long, ArrayList<EnemyCrossData>>> mapIt = enemyCrossDataVisibilityMap.entrySet().iterator(); mapIt.hasNext(); ) {
            Map.Entry<Long, ArrayList<EnemyCrossData>> next = mapIt.next();
            ArrayList<EnemyCrossData> visibilityDataList = next.getValue();

            // We only care about the latest updated visibility data. Once we have that we can send it to native
            // and delete the previous data from the list. We no longer care about the responses from native
            // for the old data.
            boolean visibilityDataUpdated = false;
            for (ListIterator<EnemyCrossData> it = visibilityDataList.listIterator(visibilityDataList.size()); it.hasPrevious(); ) {
                EnemyCrossData enemyCrossData = it.previous();
                if (visibilityDataUpdated) {
                    enemyCrossDataQueue.add(enemyCrossData);
                    it.remove();
                    continue;
                }
                if (enemyCrossData.visibilityDataSet.getValue()) {
                    if (enemyCrossData.visibility.getValue()) {
                        entityListForCrossGuide.add(enemyCrossData.entity);
//                        if (visibilityDataUpdated) {
//                            it.remove();
//                        }
//                        System.out.println("updated radar with point for id: " + next.getKey());
//                    radarVisibilityDataToRemove.add(visibilityData);
                    } else {

//            System.out.println("centre not visible");
                    }
                    visibilityDataUpdated = true;

                }
            }

            if (visibilityDataList.isEmpty()) {
                mapIt.remove();
            }
        }
    }

    public void recalcFrustum() {
        camera.setShouldRecalcFrustumPlanes(true);
    }

    private final ENG_Vector4D entityCentre = new ENG_Vector4D(true);
    private final ENG_Vector4D transformedEntityCentre = new ENG_Vector4D(true);
    private final ENG_Vector2D distFromZNegative = new ENG_Vector2D();
    private final ENG_Vector4D entityVelocity = new ENG_Vector4D();
    private final ENG_Quaternion entityOrientation = new ENG_Quaternion(true);
    private final ENG_Vector4D transformedEntityVelocity = new ENG_Vector4D();
    private final ENG_Vector4D finalEntityVelocity = new ENG_Vector4D();
    private final ENG_Vector4D targetPos4D = new ENG_Vector4D(true);
    private final ENG_Vector4D finalTargetPos4D = new ENG_Vector4D(true);
    private final ENG_Vector2D targetPos = new ENG_Vector2D();
    private final ENG_Vector2D targetVelocity = new ENG_Vector2D();
    private final QuadraticEquationResult collisionTime = new QuadraticEquationResult();
    private final ENG_Vector4D crossPositionInViewSpace = new ENG_Vector4D(true);
    private final ENG_Vector4D crossPositionInWorldSpace = new ENG_Vector4D(true);
    private final ENG_Vector4D transformedCrossPosition = new ENG_Vector4D(true);
    private long entityFollowedByCross = -1;
    private final ENG_Vector4D tempVelocity = new ENG_Vector4D();
    private ENG_TextAreaOverlayElement healthOverlayElement;
    private int currentHealth = -1; // 0 is a valid health number
    private ENG_TextAreaOverlayElement spawnInfoOverlayElement;
    private ENG_TextAreaOverlayElement playerSpawnInfoOverlayElement;
    private ENG_TextAreaOverlayElement beaconInfoOverlayElement;
    private boolean spawnInfoTextChanged;
    private long spawnInfoTextBeginTime;
    private boolean spawnInfoTextShown;
    private boolean playerSpawnInfoTextChanged;
    private long playerSpawnInfoTextBeginTime;
    private boolean playerSpawnInfoTextShown;
    private boolean tutorialInfoTextChanged;
    private long tutorialInfoTextBeginTime;
    private long tutorialInfoTextTimeShown;
    private boolean tutorialInfoTextShown;
    private boolean belowCrosshairTextChanged;
    private long belowCrosshairTextBeginTime;
    private long belowCrosshairTextTimeShown;
    private boolean belowCrosshairTextShown;
    private final ENG_Vector4D playerShipPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D beaconPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D cargoPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D playerShipFrontVec = new ENG_Vector4D();
    private int currentBeaconDistance;
    private final ENG_ClosestObjectData data = new ENG_ClosestObjectData();
    private final ENG_Vector4D distanceOtherPos = new ENG_Vector4D(true);
    private final ENG_Vector4D distanceCrossPos = new ENG_Vector4D(true);
    // private boolean chasingMissileSoundActive;
    private long chasingMissileBeginTime;
    private long chasingMissileCurrentDelay;
    private boolean movementFlaresCreated;

    public void update() {

        if (!visible) {
            return;
        }
//        long viewProjMatBeginTime = System.nanoTime();
        createViewProjectionMatrix(camera);
//        System.out.println("createViewProjectionMatrix time: " + (System.nanoTime() - viewProjMatBeginTime) / 1000000.0f);

        // Update the cross for the closest enemy
//        long updateCrossFollowingEnemyBeginTime = System.nanoTime();
        if (!WeaponType.isHomingMissileType(currentWeaponType)) {
            updateCrossFollowingEnemy();
        } else {
            hideCrossOverlay();
        }
//        System.out.println("updateCrossFollowingEnemy time: " + (System.nanoTime() - updateCrossFollowingEnemyBeginTime) / 1000000.0f);

        updateCurrentEnemySelectedShip();

        updateHealth();
        updateWeaponAndAmmo();

        updateSpawnInfoText();

        updatePlayerSpawnInfoText();

        updateEnemySelectedNoEnemyText();

        updateEnemySelectedDistanceText();

        updateTutorialInfoText();

        updateBelowCrosshairText();

        updateBeaconInfoText();

        updatePlayerShipIncomingMissileSound();

        updateCargoCountText();

        updateCargoScanText();

        udpateAboveCrosshairText();

        updateBeaconDirection();

        hitMarker.update();

        updateHudVibration();

        if (SHOW_DEBUGGING_INDICATORS && APP_Game.GAME_RESOURCE_UPDATE_CHECKER_ENABLED) {
            updateGameResourcesCheckerText();
        }

        // Since we need the inverse of the projection matrix to unproject some coords
        // it doesn't make any sense to update unless we actually have the proj mat data.
        if (matricesConcatenated) {
            updateMovementFlareManager(projMatrix);
        }
    }

    private void udpateAboveCrosshairText() {
        if (currentTimeMillis() - aboveCrosshairCaptionCurrentTime > ABOVE_CROSSHAIR_CAPTION_TIME) {
            aboveCrosshairOverlayElement.setCaption("");
        }
    }

    private void updateHudVibration() {
        if (vibrationActive) {
            if (ENG_Utility.hasTimePassed(
                            FrameInterval.HUD_VIBRATION_TIME + currentVibrationDuration + " " + currentVibrationStartTime + " " + currentTimeMillis() + " " + ENG_Utility.getRandom().nextInt(),
                            currentVibrationStartTime,
                            currentVibrationDuration)) {
                vibrationActive = false;
                resetToOriginalHudElementsPosition();
            } else {
                if (ENG_Utility.hasTimePassed(currentVibrationMovementTime, currentVibrationsPerSecondWaitTime)) {
                    currentVibrationMovementTime = currentTimeMillis();
                    // Choose a random vector a few times per second to move all elements
                    // around the screen.
                    float x = ENG_Utility.rangeRandom(-currentVibrationDistanceX, currentVibrationDistanceX);
                    float y = ENG_Utility.rangeRandom(-currentVibrationDistanceY, currentVibrationDistanceY);
                    for (VibrationOverlayElement elem : vibrationOverlayElementList) {
                        elem.addToOriginalPosition(x, y);
                    }
                }

            }
        }
    }

    private void saveOriginalHudElementsPosition() {
        for (VibrationOverlayElement elem : vibrationOverlayElementList) {
            elem.savePosition();
        }
    }

    private void resetToOriginalHudElementsPosition() {
        for (VibrationOverlayElement elem : vibrationOverlayElementList) {
            elem.restorePosition();
        }
    }

    private void updateMovementFlareManager(ENG_Matrix4 projMatrix) {
        movementFlareManager.update(projMatrix);
    }

    private void updateBeaconDirection() {
        boolean dirShown = false;
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        Entity currentBeacon = worldManager.getCurrentBeacon();
        if (playerShip != null && currentBeacon != null) {
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);
            EntityProperties beaconEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(currentBeacon);
            BeaconProperties beaconProperties = worldManager.getBeaconPropertiesComponentMapper().getSafe(currentBeacon);
            if (beaconProperties == null) {
                throw new ENG_InvalidFieldStateException(beaconEntityProperties
                        .getItem().getName()
                        + " is not a valid beacon object");
            }
            if (!entityProperties.isDestroyed()
                    && !beaconProperties.isReached()) {
                entityProperties.getNode().getPosition(playerShipPosition);
                entityProperties.getNode().getLocalInverseZAxis(shipFrontVec);
                beaconEntityProperties.getNode().getPosition(beaconPosition);
                beaconEntityProperties.getNode()._getFullTransform(
                        beaconFullTransform);
                viewMatrix.concatenateAffine(beaconFullTransform,
                        beaconWorldViewMatrix);
                frustum.getProjectionMatrix().concatenate(
                        beaconWorldViewMatrix, beaconProjectionSpace);
                // beaconWorldViewMatrix.concatenate(, );
                // MAKE SURE THE VIEWPROJMATRIX IS OK !!!
                // beaconProjectionSpace.transform(beaconPosition,
                // beaconTransformedPosition);
                beaconTransformedPosition.set(beaconProjectionSpace.get(0, 3),
                        beaconProjectionSpace.get(1, 3),
                        beaconProjectionSpace.get(2, 3),
                        beaconProjectionSpace.get(3, 3));
                float winv = 1.0f / beaconTransformedPosition.w;
                beaconTransformedPosition.x *= winv;
                beaconTransformedPosition.y *= winv;
                beaconPosition.sub(playerShipPosition, shipToBeacon);
                shipToBeacon.normalize();
                boolean behind = shipFrontVec.dotProduct(shipToBeacon) < 0.0f;
                if (beaconTransformedPosition.x < -1.0f
                        || beaconTransformedPosition.x > 1.0f
                        || beaconTransformedPosition.y < -1.0f
                        || beaconTransformedPosition.y > 1.0f || behind) {
                    BeaconDir dir;
                    if (Math.abs(beaconTransformedPosition.x) > Math
                            .abs(beaconTransformedPosition.y)) {
                        if (behind) {
                            if (beaconTransformedPosition.x > 0.0f) {
                                dir = BeaconDir.LEFT;
                            } else {
                                dir = BeaconDir.RIGHT;
                            }
                        } else {
                            if (beaconTransformedPosition.x < 0.0f) {
                                dir = BeaconDir.LEFT;
                            } else {
                                dir = BeaconDir.RIGHT;
                            }
                        }
                    } else {
                        if (behind) {
                            if (beaconTransformedPosition.y > 0.0f) {
                                dir = BeaconDir.DOWN;
                            } else {
                                dir = BeaconDir.UP;
                            }
                        } else {
                            if (beaconTransformedPosition.y < 0.0f) {
                                dir = BeaconDir.DOWN;
                            } else {
                                dir = BeaconDir.UP;
                            }
                        }
                    }
                    setBeaconDir(dir);
                    beaconDirOverlay.show();
                    dirShown = true;
                }

            }
        }
        if (!dirShown) {
            beaconDirOverlay.hide();
        }
    }

    private void updateCargoCountText() {
        WorldManager worldManager = WorldManager.getSingleton();
        long playerShipId = worldManager.getPlayerShipEntityId();
        if (playerShipId != -1
                && worldManager.getNumCargosInLevel() > 0
                && worldManager.getNumCargosNotScanned() > 0) {
            Entity entity = worldManager.getShipByGameEntityId(playerShipId);
            if (entity != null) {
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
                if (!entityProperties.isDestroyed()) {
                    ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);
                    cargoCountOverlayElement.setCaption("Cargo count: " + worldManager.getNumCargosNotScanned());
                }
            }
        } else {
            cargoCountOverlayElement.setCaption("");
        }
    }

    private void updateCargoScanText() {
        boolean textShown = false;
        WorldManager worldManager = WorldManager.getSingleton();
        long playerShipId = worldManager.getPlayerShipEntityId();
        if (playerShipId != -1) {
            Entity entity = worldManager.getShipByGameEntityId(playerShipId);
            if (entity != null) {
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
                ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);
                if (!entityProperties.isDestroyed()) {
                    entityProperties.getNode().getPosition(playerShipPosition);
                    // WorldManager.getSingleton().getClosestCargo(
                    // playerShipPosition,
                    // data);
                    Iterator<Long> iterator = worldManager.getCargoNameListIterator();
                    while (iterator.hasNext()) {
                        Long cargoName = iterator.next();
                        Entity cargo = worldManager.getEntityByGameEntityId(cargoName);

                        // Entity cargo = WorldManager.getSingleton().getEntity(
                        // data.objectId);
                        if (cargo == null) {
                            throw new NullPointerException("Cargo " + cargoName + " is not a valid cargo name");
                        }
                        EntityProperties cargoEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(cargo);
                        cargoEntityProperties.getNode().getPosition(cargoPosition);
                        float distance = cargoPosition.distance(playerShipPosition);
                        if (distance < cargoEntityProperties.getRadius()) {

                            CargoProperties cargoProperties = worldManager.getCargoPropertiesComponentMapper().get(cargo);
                            if (!cargoProperties.isScanned()) {

                                entityProperties.getNode().getLocalInverseZAxis(playerShipFrontVec);
                                cargoPosition.sub(playerShipPosition, distanceOtherPos);
                                distanceOtherPos.normalize();
                                float angleBetween = distanceOtherPos.angleBetween(playerShipFrontVec);

                                if (angleBetween < CARGO_SELECTION_ANGLE) {
                                    textShown = _updateCargoScanText(shipProperties, cargoEntityProperties, cargoProperties, textShown);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!textShown) {
                    shipProperties.setScanningCargo(false);
                    shipProperties.setCurrentScannedCargo(null);

                }
            }

        }
        if (!textShown) {
            cargoScanOverlayElement.setCaption("");
        }
    }

    private boolean _updateCargoScanText(ShipProperties shipProperties,
                                         EntityProperties cargoEntityProperties,
                                         CargoProperties cargoProperties, boolean textShown) {
        if (shipProperties.isScanningCargo()) {
            if (cargoEntityProperties.getItem().getName()
                    .equals(shipProperties.getCurrentScannedCargo())) {
                if (ENG_Utility
                        .hasTimePassed(
                                FrameInterval.CARGO_SCAN_TIME + cargoEntityProperties.getNode().getName(),
                                shipProperties.getCargoScanStartTime(),
                                CARGO_SCAN_TIME)) {
                    shipProperties.setScanningCargo(false);
                    cargoProperties.setScanned(true);
                } else {

                    textShown = true;
                }
            }
        } else {
            shipProperties.setCargoScanStartTime();
            shipProperties.setCurrentScannedCargo(cargoEntityProperties.getItem().getName());
            shipProperties.setScanningCargo(true);
            textShown = true;
        }
        if (textShown) {
            cargoScanOverlayElement.setCaption("Scanning cargo");
        }
        return textShown;
    }

    private void updateGameResourcesCheckerText() {
        if (ENG_Utility.hasTimePassed(gameResourcesCheckerBeginTime, gameResourcesCheckerDuration)) {
            gameResourcesCheckerIndicator.hide();
        }
    }

    private void updatePlayerShipIncomingMissileSound() {

        WorldManager worldManager = WorldManager.getSingleton();
        long playerShipId = worldManager.getPlayerShipEntityId();
        if (playerShipId != -1) {
            Entity entity = worldManager.getShipByGameEntityId(playerShipId);
            if (entity != null) {
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
                if (!entityProperties.isDestroyed()) {
                    ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(entity);
                    if (shipProperties.isChased()) {
                        entityProperties.getNode().getPosition(playerShipPosition);
                        worldManager.getClosestProjectile(playerShipPosition, shipProperties, data);
                        if (data.minDist < MIN_BEEP_DISTANCE) {
                            // chasingMissileSoundActive = true;
                            // Adjust the time between sounds
                            float delayRate = data.minDist / MIN_BEEP_DISTANCE;
                            if (ENG_Utility.hasTimePassed(FrameInterval.CHASING_MISSILE_SOUND_DELAY, chasingMissileBeginTime, chasingMissileCurrentDelay)) {
//                                MainApp.getGame().playSoundMaxVolume(BEEP_SND);
                                WorldManager.getSingleton().playSoundFromCameraNode(BEEP_SND);
                                MainApp.getGame().vibrate(APP_Game.VibrationEvent.HOMING_BEEP);
                                chasingMissileBeginTime = currentTimeMillis();
                                chasingMissileCurrentDelay = MINIMUM_DELAY_BETWEEN_BEEPS + (long) (delayRate * BEEP_RATE);
                            }
                        } else {
                            // chasingMissileSoundActive = false;
                        }
                    } else {
                        // chasingMissileSoundActive = false;
                    }

                }
            }
        }
    }

    private void updateBeaconInfoText() {
        boolean textShown = false;
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        Entity currentBeacon = worldManager.getCurrentBeacon();
        if (playerShip != null && currentBeacon != null) {
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);
            EntityProperties beaconEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(currentBeacon);
            BeaconProperties beaconProperties = worldManager.getBeaconPropertiesComponentMapper().getSafe(currentBeacon);
            if (beaconProperties == null) {
                throw new ENG_InvalidFieldStateException(beaconEntityProperties.getItem().getName() + " is not a valid beacon object");
            }
            if (!entityProperties.isDestroyed() && !beaconProperties.isReached()) {
                entityProperties.getNode().getPosition(playerShipPosition);
                beaconEntityProperties.getNode().getPosition(beaconPosition);
                int distance = (int) beaconPosition.distance(playerShipPosition);
                if (currentBeaconDistance != distance) {
                    currentBeaconDistance = distance;
                    beaconInfoOverlayElement.setCaption("Distance to beacon: " + currentBeaconDistance);
                }
                textShown = true;
            }
        } else {
        }
        if (!textShown) {
            beaconInfoOverlayElement.setCaption("");
        }
    }

    private void updateSpawnInfoText() {
        if (spawnInfoTextChanged) {
            spawnInfoTextBeginTime = currentTimeMillis();
            spawnInfoTextShown = true;
            spawnInfoTextChanged = false;
        }

        if (spawnInfoTextShown
                && ENG_Utility.hasTimePassed(
                spawnInfoTextBeginTime,
                SPAWN_INFO_TIME)) {
            spawnInfoOverlayElement.setCaption("");
            spawnInfoTextShown = false;
        }
    }

    private void updatePlayerSpawnInfoText() {
        if (playerSpawnInfoTextChanged) {
            playerSpawnInfoTextBeginTime = currentTimeMillis();
            playerSpawnInfoTextShown = true;
            playerSpawnInfoTextChanged = false;
        }

        if (playerSpawnInfoTextShown
                && ENG_Utility.hasTimePassed(
                playerSpawnInfoTextBeginTime,
                PLAYER_SPAWN_INFO_TIME)) {
            playerSpawnInfoOverlayElement.setCaption("");
            playerSpawnInfoTextShown = false;
        }
    }

    private void updateTutorialInfoText() {
        if (tutorialInfoTextChanged) {
            tutorialInfoTextBeginTime = currentTimeMillis();
            tutorialInfoTextShown = true;
            tutorialInfoTextChanged = false;
        }

        if (tutorialInfoTextShown
                && ENG_Utility.hasTimePassed(
                tutorialInfoTextBeginTime,
                tutorialInfoTextTimeShown)) {
            tutorialInfoOverlayElement.setCaption("");
            tutorialInfoTextShown = false;
        }
    }

    private void updateBelowCrosshairText() {
        if (belowCrosshairTextChanged) {
            belowCrosshairTextBeginTime = currentTimeMillis();
            belowCrosshairTextShown = true;
            belowCrosshairTextChanged = false;
        }

        if (belowCrosshairTextShown
                && ENG_Utility.hasTimePassed(
                belowCrosshairTextBeginTime,
                belowCrosshairTextTimeShown)) {
            belowCrosshairOverlayElement.setCaption("");
            belowCrosshairTextShown = false;
        }
    }

    private void updateHealth() {

        Entity playerShip = WorldManager.getSingleton().getPlayerShip();
        if (playerShip != null) {
            int health = WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(playerShip).getHealth();
            if (currentHealth != health || healthShown) {
                healthShown = false;
                currentHealth = health;
                healthOverlayElement.setCaption("Shield: " + currentHealth);
                if (currentHealth <= HEALTH_LOW) {
                    healthOverlayElement.setColour(ENG_ColorValue.RED);
                } else {
                    healthOverlayElement.setColour(ENG_ColorValue.GREEN);
                }
            }
        }
    }

    private void updateEnemySelectedDistanceText() {
        if (!visible) {
            return;
        }

        if (currentEnemySelection == - 1) {
            enemySelectedDistanceOverlayElement.setCaption("");
            return;
        }

        Entity playerShip = WorldManager.getSingleton().getPlayerShip();
        if (playerShip == null) {
            enemySelectedDistanceOverlayElement.setCaption("");
            return;
        }
        Entity currentSelectedEnemy = currentEnemySelectionMap.get(currentEnemySelectionItemId);
        if (currentSelectedEnemy == null) {
            // Maybe the enemy has been destroyed by somebody else or exited the level.
            resetCurrentEnemySelection();
            enemySelectedDistanceOverlayElement.setCaption("");
            return;
        }
        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(currentSelectedEnemy);
        EntityProperties playerShipEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);

        playerShipEntityProperties.getNode().getPosition(playerShipPosition);
        entityProperties.getNode().getPosition(enemyShipPos);

        float distance = playerShipPosition.distance(enemyShipPos);
        enemySelectedDistanceOverlayElement.setCaption("Distance: " + StringUtils.leftPad(String.valueOf((int) distance), 5));
    }

    private boolean currentEnemySelectionChanged;
    private boolean enemySelectedNoEnemyTextChanged;
    private boolean enemySelectedNoEnemyTimePassedTextChanged;

    private void updateEnemySelectedNoEnemyText() {
        if (!visible) {
            return;
        }

        if (currentEnemySelection == -1) {
            if (currentEnemySelectionChanged) {
                if (ENG_Utility.hasTimePassed(enemySelectedNoEnemyBeginTime, ENEMY_SELECTED_NO_ENEMY_DISPLAY_TIME)) {
                    if (!enemySelectedNoEnemyTimePassedTextChanged) {
                        enemySelectedNoEnemyOverlayElement.setCaption("");
                        enemySelectedNoEnemyTimePassedTextChanged = true;
                        currentEnemySelectionChanged = false;
                    }
                } else {
                    if (!enemySelectedNoEnemyTextChanged) {
                        enemySelectedNoEnemyOverlayElement.setCaption("No enemy selected");
                        enemySelectedNoEnemyTextChanged = true;
                    }
                }
            }
        } else {
            currentEnemySelectionChanged = true;
            if (enemySelectedNoEnemyTextChanged && !enemySelectedNoEnemyTimePassedTextChanged) {
                // We are coming from a no enemy selection position.
                enemySelectedNoEnemyOverlayElement.setCaption("");
            }
            enemySelectedNoEnemyTextChanged = false;
            enemySelectedNoEnemyTimePassedTextChanged = false;
        }
    }

    private void updateCrossFollowingEnemy() {
        // Find the closest to the Z axis and also closest to the camera
        Entity entity = null;
        float currentScore = 0.0f;
        hideCrossOverlay();
        WorldManager worldManager = WorldManager.getSingleton();
        if (currentEnemySelection != -1) {
            entity = currentEnemySelectionMap.get(currentEnemySelectionItemId);
            if (entity != null) {
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().getSafe(entity);
                if (entityProperties == null) {
                    return;
                }
                entityProperties.getItem().getWorldAABB().getCenter(entityCentre);
                // node.getPosition(entityCentre);
                viewMatrix.transform(entityCentre, transformedEntityCentre);
                if (transformedEntityCentre.z > 0.0f) {
                    hideCrossOverlay();
                    return;
                }
            }
//        long entityListForCrossGuideIterationBeginTime = System.nanoTime()
        }
        if (entity == null) {
            for (Entity e : entityListForCrossGuide) {
//            long entityListForCrossGuideBeginTime = System.nanoTime();
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().getSafe(e);
                if (entityProperties == null) {
                    continue;
                }
                ENG_SceneNode node = entityProperties.getNode();
                if (USE_CURRENT_POSITION) {
                    entityProperties.getNode().getPosition(entityCentre);
                } else if (USE_BULLET_AABB_POSITION) {
                    ENG_AxisAlignedBox aabb = PhysicsUtility.getAxisAlignedBox(entityProperties.getRigidBody());
                    aabb.getCenter(entityCentre);
                } else {
                    entityProperties.getItem().getWorldAABB().getCenter(entityCentre);
                }
                // node.getPosition(entityCentre);
                viewMatrix.transform(entityCentre, transformedEntityCentre);
                distFromZNegative.set(transformedEntityCentre);
                float distFromNegativeZ = distFromZNegative.length();
                float distFromCamera = -transformedEntityCentre.z;
                float distFromNegativeZScore = (distFromNegativeZ > 100.0f) ? 0.0f : 100.0f - distFromNegativeZ;
                float distFromCameraScore = (distFromCamera > 1000.0f) ? 0.0f : (100.0f - distFromCamera * 0.1f);
                float score = (80.0f * distFromNegativeZScore + 20.0f * distFromCameraScore) * 0.01f;
                if (entity == null) {
                    currentScore = score;
                    entity = e;
                } else {
                    if (score > currentScore) {
                        currentScore = score;
                        entity = e;
                    }
                }
//            System.out.println("entityListForCrossGuide time: " + (System.nanoTime() - entityListForCrossGuideBeginTime) / 1000000.0f);
            }
        }
//        System.out.println("entityListForCrossGuideIteration time: " + (System.nanoTime() - entityListForCrossGuideIterationBeginTime) / 1000000.0f);

        if (entity != null) {
//            long entityNotNullBeginTime = System.nanoTime();
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(entity);
            Entity playerShip = worldManager.getPlayerShip();
            WeaponProperties weaponProperties = worldManager.getWeaponPropertiesComponentMapper().get(playerShip);
            WeaponData weaponData = WeaponData.getWeaponData(currentWeaponType);
            if (WeaponType.isHomingMissileType(currentWeaponType)) {
                throw new IllegalArgumentException(currentWeaponType + " should not be " + "a homing missile type");
            }
            float projectileSpeed = weaponData.maxSpeed;
            entityProperties.getVelocityAsVec(entityVelocity);
            entityProperties.getNode().getOrientation(entityOrientation);
            entityProperties.getNode().getPosition(targetPos4D);
//            long calculateCollisionPositionBeginTime = System.nanoTime();
            boolean currentCollisionPositionValid = collisionPositionValid;
            collisionPositionValid = ENG_Utility.calculateCollisionPosition(
                    projectileSpeed, viewMatrix, crossPositionInViewSpace, entityVelocity,
                    entityOrientation, targetPos4D, transformedEntityVelocity,
                    finalEntityVelocity, finalTargetPos4D, tempVelocity);
            if (currentCollisionPositionValid != collisionPositionValid) {
                System.out.println("HudManager: collisionPositionValid: " + collisionPositionValid);
            }
//            System.out.println("calculateCollisionPosition time: " + (System.nanoTime() - calculateCollisionPositionBeginTime) / 1000000.0f);
//            if (valid) {
//                entityProperties.getItem().getWorldAABB().getAllCorners(corners);
////                if (!entityProperties.isScanable()) {
////                    resetUpdateEnemySelection(playerShipProperties);
////                    return;
////                }
//
//                transformBoundingBoxToClipSpace(corners, viewProjMatrix, transCorners, leftTop, rightBottom);
//                if (leftTop.x < 0.0f || leftTop.y < 0.0f || rightBottom.x > 1.0f
//                        || rightBottom.y > 1.0f) {
//
//                }
//            }
            if (!collisionPositionValid) {
                hideCrossOverlay();
            } else {
//                long transformBeginTime = System.nanoTime();
                transformByProjectionMat(projMatrix, crossPositionInViewSpace, transformedCrossPosition);

                transformedCrossPosition.x = (transformedCrossPosition.x + 1.0f) * 0.5f;
                transformedCrossPosition.y = 1.0f - (transformedCrossPosition.y + 1.0f) * 0.5f;
//                System.out.println("transform time: " + (System.nanoTime() - transformBeginTime) / 1000000.0f);

                // Transform the cross position into pixel coordinates
                // GLRenderSurface renderSurface =
                // GLRenderSurface.getSingleton();
//                long windowSizeBeginTime = System.nanoTime();
//                ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
                int width = windowWidth;// renderSurface.getWidth();
                int height = windowHeight;// renderSurface.getHeight();
//                System.out.println("windowSize time: " + (System.nanoTime() - windowSizeBeginTime) / 1000000.0f);
//                long positionBeginTime = System.nanoTime();
                float xPos = ENG_Math.floor(transformedCrossPosition.x * width) - crossHalfWidth;
                float yPos = ENG_Math.floor(transformedCrossPosition.y * height) - crossHalfHeight;
                if (xPos >= 0 && yPos >= 0
                        && xPos + (int) cross.getWidth() < width
                        && yPos + (int) cross.getHeight() < height) {
                    cross.setLeft(xPos);
                    cross.setTop(yPos);
                    entityFollowedByCross = worldManager.getEntityPropertiesComponentMapper().get(entity).getEntityId();
                    crossOverlay.show();
                } else {
                    hideCrossOverlay();
                }
//                System.out.println("position time: " + (System.nanoTime() - positionBeginTime) / 1000000.0f);
            }
//            System.out.println("entityNotNull time: " + (System.nanoTime() - entityNotNullBeginTime) / 1000000.0f);
        } else {
            hideCrossOverlay();
        }
        entityListForCrossGuide.clear();
    }

    private void hideCrossOverlay() {
        entityFollowedByCross = -1;
        crossOverlay.hide();
    }

    private void hideEnemySelectionOutsideScreenOverlay() {
        enemySelectionOutsideScreenOverlay.hide();
    }

    private void updateCrosshairColor(RaySceneQueryResultEntry raySceneQueryResultEntry, boolean insideCrosshair) {
        WorldManager worldManager = WorldManager.getSingleton();
        if (raySceneQueryResultEntry != null) {
            Entity ship = worldManager.getEntityByItemId(raySceneQueryResultEntry.movable.getId());
            if (ship == null) {
                return;
            }
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(ship);
            ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(ship);
            enemySelectionTime = WeaponType.getWeaponEnemySelectionTime(currentWeaponType);
//            System.out.println("enemySelectionTime: " + enemySelectionTime);
            if (shipProperties != null && worldManager.getPlayerShipData().team != shipProperties.getShipData().team && enemySelectionTime != 0) {
                if (MainActivity.isDebugmode() && WeaponType.getWeaponCrosshairType(currentWeaponType) != CrosshairType.SELECTION) {
                    throw new IllegalArgumentException("The crosshair type does not match with the enemy selection waiting time");
                }
                if (currentPotentialSelectedEnemy != raySceneQueryResultEntry.movable) {
                    currentPotentialSelectedEnemy = raySceneQueryResultEntry.movable;
                    currentEnemySelectionTime = currentTimeMillis();
//                    System.out.println("Setting inner crosshair color");
                    setOverlayElementColor(crosshairInner, enemySelectedColor);
                }

                checkSelectedEnemyFollowable(worldManager, entityProperties);
            }
        } else if (insideCrosshair) {
            if (currentPotentialSelectedEnemy != null) {
                // We don't have a ray query but the enemy is still inside our square crosshair so we must count
                // down the time until the missiles become tracking missiles.
                Entity ship = worldManager.getEntityByItemId(currentPotentialSelectedEnemy.getId());
                if (ship == null) {
                    return;
                }
                EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(ship);
                checkSelectedEnemyFollowable(worldManager, entityProperties);
            }
        }
    }

    private void checkSelectedEnemyFollowable(WorldManager worldManager, EntityProperties entityProperties) {
        if (ENG_Utility.hasTimePassed(FrameInterval.CURRENT_ENEMY_SELECTION_TIME + entityProperties.getUniqueName(),
                currentEnemySelectionTime, enemySelectionTime)) {
//            System.out.println("enemySelectionTime: " + enemySelectionTime + " enemy selected for currentEnemySelectionTime: " + currentEnemySelectionTime);
            setCrosshairColor(enemySelectedColor);
            currentSelectedEnemyFollowable = true;
            currentSelectedEnemy = currentPotentialSelectedEnemy;
            // We've updated this method. Now we set the followed ship in
            // the ship properties. Reason: decoupling.
//            System.out.println("setCurrentSelectedEnemy from HudManager with name: " + currentSelectedEnemy.getName());
            worldManager.getShipPropertiesComponentMapper().get(worldManager.getPlayerShip())
                    .setCurrentSelectedEnemy(currentSelectedEnemy.getId());
        }
    }

    private void checkWeaponChanged() {
        if (previousWeaponType != currentWeaponType || recolorCrosshair) {
            recolorCrosshair = false;
            previousWeaponType = currentWeaponType;
            setCrosshairColor(defaultCrosshairColor);
            currentEnemySelectionTime = currentTimeMillis();
        }
    }

    private void setCrosshairColor(ENG_ColorValue col) {
//        ENG_GpuProgramParameters crosshairFragmentProgramParameters = crosshair
//                .getMaterial().getTechnique((short) 0).getPass((short) 0)
//                .getFragmentProgramParameters();
//        crosshairFragmentProgramParameters.setNamedConstant("color", col);
        setOverlayElementColor(crosshairOuter, col);
        setOverlayElementColor(crosshairInner, col);
    }

    private void setOverlayElementColor(ENG_OverlayElement crosshair, ENG_ColorValue col) {
        ENG_NativeCalls.unlitDatablock_setUseColour(crosshair.getDatablock(), true);
        ENG_NativeCalls.unlitDatablock_setColour(crosshair.getDatablock(), col);
    }

    /**
     * For after context loss
     */
    public void showIfVisible() {
        setVisible(visible);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            if (!created) {
                createHud();
            }
//            createMovementFlares();
            show();
        } else {
            hide();
            destroyMovementFlares();
        }
    }

    private void hide() {

        if (created) {
            removeListeners();

            hudOverlay.hide();
            enemySelectionOverlay.hide();
            crosshairOuterOverlay.hide();
            crosshairInnerOverlay.hide();
            crosshairCrossOverlay.hide();
            hideCrossOverlay();
            hideEnemySelectionOutsideScreenOverlay();
            beaconDirOverlay.hide();
            // chasingMissileSoundActive = false;
            movementFlareManager.setVisible(false);
            hitMarker.setVisible(false);
            stopVibration();
        }
    }

    private void removeListeners() {

        if (MainApp.Platform.isMobile()) {
            fireButtonOverlay.removeListener(fireButtonListener);
            speedScrollOverlay.removeListener(speedScrollListener);
            if (controlsOverlayElement != null && !MainApp.getGame().isAccelerometerEnabled()) {
                controlsOverlayElement.removeListener(controlsListener);
            }

            afterburnerButtonOverlay.removeListener(afterburnerButtonListener);
            countermeasuresButtonOverlay.removeListener(countermeasuresButtonListener);
            reloaderButtonOverlay.removeListener(reloaderButtonListener);

            rotateLeftButtonOverlay.removeListener(rotationLeftButtonListener);
            rotateRightButtonOverlay.removeListener(rotationRightButtonListener);

            enemySelectionLeftButtonOverlay.removeListener(enemySelectionPreviousButtonListener);
            enemySelectionRightButtonOverlay.removeListener(enemySelectionNextButtonListener);

            weaponSelectionPreviousButtonOverlay.removeListener(weaponSelectionPreviousButtonListener);
            weaponSelectionNextButtonOverlay.removeListener(weaponSelectionNextButtonListener);

            attackSelectedEnemyButtonOverlay.removeListener(attackSelectedEnemyButtonListener);

            defendPlayerShipButtonOverlay.removeListener(defendPlayerShipButtonListener);

            backButtonOverlay.removeListener(backButtonListener);
        }

    }

    private void show() {

        addListeners();

        updateWeaponAndAmmo();

        hudOverlay.show();
        if (controlsOverlayElement != null && MainApp.getGame().isAccelerometerEnabled()) {
            controlsOverlayElement.hide();
        }
        movementFlareManager.setVisible(true);
        hitMarker.setVisible(true);
        // crosshairOverlay.show();
        // crosshairCrossOverlay.show(); This we only show when we need to
    }

    private void updateWeaponAndAmmo() {
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip == null) {
            return;
        }
        WeaponProperties weaponProperties = worldManager.getWeaponPropertiesComponentMapper().get(playerShip);

        WeaponType weaponType = weaponProperties.getCurrentWeaponType();
        int ammo = weaponProperties.getCurrentWeaponAmmo();
        if (weaponType != currentWeaponType || weaponHudShown) {
            // Use the weaponHudShown after reloading the game when returning
            // from
            // an activity since the current weaponType hasn't changed
            weaponHudShown = false;
            currentWeaponType = weaponType;
            currentWeaponAmmo = ammo;

            updateWeaponString();

            // Also update the crosshair
            switch (WeaponType.getWeaponCrosshairType(currentWeaponType)) {
                case CROSSHAIR:
                    crosshairOuterOverlay.hide();
                    crosshairInnerOverlay.hide();
                    crosshairCrossOverlay.show();
                    break;
                case SELECTION:
                    crosshairCrossOverlay.hide();
                    crosshairOuterOverlay.show();
                    crosshairInnerOverlay.show();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid crosshair type");
            }
        } else if (ammo != currentWeaponAmmo) {
            currentWeaponAmmo = ammo;
            updateWeaponString();
        }
    }

    private void updateWeaponString() {
        String wpnStr = WeaponType.getWeapon(currentWeaponType) + (currentWeaponAmmo == WeaponData.INFINITE_AMMO ? "" : (" " + currentWeaponAmmo));
        weaponOverlayElement.setCaption(wpnStr);
        if (WeaponType.hasInfiniteAmmo(currentWeaponType)) {
            weaponOverlayElement.setColour(ENG_ColorValue.GREEN);
        } else {
            if (currentWeaponAmmo <= WeaponType.getWeaponLowAmmo(currentWeaponType)) {
                weaponOverlayElement.setColour(ENG_ColorValue.RED);
            } else {
                weaponOverlayElement.setColour(ENG_ColorValue.GREEN);
            }
        }
    }

//    public void setFireButtonCooldownTime() {
//        fireButtonListener.setWaitingTime(WeaponType.getWeaponCooldownTime(currentWeaponType));
//    }

    public boolean isFireButtonPressed() {
        return fireButtonListener.isClicked();
    }

    public boolean isSpeedScrollerButtonPressed() {
        return speedScrollListener.isClicked();
    }

    public boolean isControlsButtonPressed() {
        return controlsListener.isClicked();
    }

    public boolean isAfterburnerButtonPressed() {
        return afterburnerButtonListener.isClicked();
    }

    public boolean isCountermeasuresButtonPressed() {
        return countermeasuresButtonListener.isClicked();
    }

    public boolean isReloaderButtonPressed() {
        return reloaderButtonListener.isClicked();
    }

    public boolean isRotationLeftButtonPressed() {
        return rotationLeftButtonListener.isClicked();
    }

    public boolean isRotationRightButtonPressed() {
        return rotationRightButtonListener.isClicked();
    }

    public boolean isWeaponSelectionPreviousButtonPressed() {
        return weaponSelectionPreviousButtonListener.isClicked();
    }

    public boolean isWeaponSelectionNextButtonPressed() {
        return weaponSelectionNextButtonListener.isClicked();
    }

    public boolean isEnemySelectionPreviousButtonPressed() {
        return enemySelectionPreviousButtonListener.isClicked();
    }

    public boolean isEnemySelectionNextButtonPressed() {
        return enemySelectionNextButtonListener.isClicked();
    }

    public boolean isAttackSelectedEnemyButtonPressed() {
        return attackSelectedEnemyButtonListener.isClicked();
    }

    public boolean isDefendPlayerShipButtonPressed() {
        return defendPlayerShipButtonListener.isClicked();
    }

    public boolean isBackButtonPressed() {
        return backButtonListener.isClicked();
    }

    public void resetAllButtons() {
        if (MainApp.Platform.isMobile()) {
            fireButtonListener.resetClicked();
            speedScrollListener.resetClicked();
            controlsListener.resetClicked();
            afterburnerButtonListener.resetClicked();
            countermeasuresButtonListener.resetClicked();
            reloaderButtonListener.resetClicked();
            rotationLeftButtonListener.resetClicked();
            rotationRightButtonListener.resetClicked();
            weaponSelectionPreviousButtonListener.resetClicked();
            weaponSelectionNextButtonListener.resetClicked();
            attackSelectedEnemyButtonListener.resetClicked();
            defendPlayerShipButtonListener.resetClicked();
            backButtonListener.resetClicked();
        }
    }

    private void addListeners() {
        if (MainApp.Platform.isMobile()) {
            fireButtonListener = new FireButtonListener("fire_button_overlay", fireButtonOverlay);
            speedScrollListener = new SpeedScrollListener(speedScrollOverlay);
            controlsListener = new MovementControlsListener("controls_overlay_element", controlsOverlayElement);
            afterburnerButtonListener = new AfterburnerButtonListener(afterburnerButtonOverlay);
            countermeasuresButtonListener = new CountermeasuresButtonListener("countermeasure_button_overlay", countermeasuresButtonOverlay);
            reloaderButtonListener = new ReloaderButtonListener(reloaderButtonOverlay);
            rotationLeftButtonListener = new RotationButtonListener("rotate_left_button_overlay",
                    rotateLeftButtonOverlay, RotationButtonListener.RotationDirection.LEFT);
            rotationRightButtonListener = new RotationButtonListener("rotate_right_button_overlay",
                    rotateRightButtonOverlay, RotationButtonListener.RotationDirection.RIGHT);
            weaponSelectionPreviousButtonListener = new WeaponSelectionButtonListener("weapon_selection_previous_button_overlay",
                    weaponSelectionPreviousButtonOverlay, WeaponSelectionButtonListener.WeaponSelectionDirection.PREVIOUS);
            weaponSelectionNextButtonListener = new WeaponSelectionButtonListener("weapon_selection_next_button_overlay",
                    weaponSelectionNextButtonOverlay, WeaponSelectionButtonListener.WeaponSelectionDirection.NEXT);
            enemySelectionPreviousButtonListener = new EnemySelectionButtonListener("enemy_selection_previous_button_overlay",
                    enemySelectionLeftButtonOverlay, EnemySelectionButtonListener.EnemySelectionDirection.PREVIOUS);
            enemySelectionNextButtonListener = new EnemySelectionButtonListener("enemy_selection_next_button_overlay",
                    enemySelectionRightButtonOverlay, EnemySelectionButtonListener.EnemySelectionDirection.NEXT);
            attackSelectedEnemyButtonListener = new AttackSelectedEnemyButtonListener(attackSelectedEnemyButtonOverlay);
            defendPlayerShipButtonListener = new DefendPlayerShipButtonListener(defendPlayerShipButtonOverlay);

            backButtonListener = new BackButtonListener("back_button_overlay", backButtonOverlay);

            fireButtonOverlay.addListener(fireButtonListener);
            speedScrollOverlay.addListener(speedScrollListener);
            if (controlsOverlayElement != null && !MainApp.getGame().isAccelerometerEnabled()) {
                controlsOverlayElement.addListener(controlsListener);
            }

            afterburnerButtonOverlay.addListener(afterburnerButtonListener);
            countermeasuresButtonOverlay.addListener(countermeasuresButtonListener);
            reloaderButtonOverlay.addListener(reloaderButtonListener);
            rotateLeftButtonOverlay.addListener(rotationLeftButtonListener);
            rotateRightButtonOverlay.addListener(rotationRightButtonListener);

            weaponSelectionPreviousButtonOverlay.addListener(weaponSelectionPreviousButtonListener);
            weaponSelectionNextButtonOverlay.addListener(weaponSelectionNextButtonListener);

            enemySelectionLeftButtonOverlay.addListener(enemySelectionPreviousButtonListener);
            enemySelectionRightButtonOverlay.addListener(enemySelectionNextButtonListener);

            attackSelectedEnemyButtonOverlay.addListener(attackSelectedEnemyButtonListener);

            defendPlayerShipButtonOverlay.addListener(defendPlayerShipButtonListener);

            backButtonOverlay.addListener(backButtonListener);
        }
    }

    public void destroyHud(boolean skipGLDelete) {
        ENG_GUIOverlayManager guiOverlayManager = ENG_GUIOverlayManager.getSingleton();
        ENG_OverlayManager overlayManager = ENG_OverlayManager.getSingleton();
        if (created) {
            removeListeners();

            if (MainApp.Platform.isMobile()) {
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("Afterburner").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("Countermeasures").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("Reloader").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("WeaponSelectionLeft").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("WeaponSelectionRight").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("RotateLeft").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("RotateRight").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("FireButton").getName());
//                guiOverlayManager.destroyControlsOverlayElement(hudOverlay.getChild("MovementControls").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("EnemySelectionLeft").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("EnemySelectionRight").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("AttackSelectedEnemy").getName());
                guiOverlayManager.destroyButtonOverlayElement(hudOverlay.getChild("DefendPlayerShip").getName());
            }
            guiOverlayManager.destroyScrollOverlayContainer(hudOverlay.getChild("SpeedMeter").getName());


            overlayManager.destroyOverlayAndChildren("hud", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("enemy_selection", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("crosshair_crosshair", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("crosshair_cross", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("cross", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("lens_flare", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("beacon_dir", skipGLDelete);
            overlayManager.destroyOverlayAndChildren("enemy_selection_outside_screen", skipGLDelete);

            resetHudVariables();
            removeAllVibrationElements();
            weaponOverlayElement = null;
            created = false;
        }
        destroyDebuggingIndicators(overlayManager, skipGLDelete);
        destroyMovementFlares();
    }

    private void destroyDebuggingIndicators(ENG_OverlayManager overlayManager, boolean skipGLDelete) {
        destroyFpsIndicator(overlayManager, skipGLDelete);
        destroyPingIndicator(overlayManager, skipGLDelete);
        destroyPlayerPosIndicator(overlayManager, skipGLDelete);
        destroyGameResourcesCheckerIndicator(overlayManager, skipGLDelete);
    }

    private void destroyFpsIndicator(ENG_OverlayManager overlayManager, boolean skipGLDelete) {
        if (fpsCreated) {
            overlayManager.destroyOverlayAndChildren("fps", skipGLDelete);
            fpsCreated = false;
        }
    }

    private void destroyPingIndicator(ENG_OverlayManager overlayManager, boolean skipGLDelete) {
        if (pingCreated) {
            overlayManager.destroyOverlayAndChildren("ping", skipGLDelete);
            pingCreated = false;
        }
    }

    private void destroyPlayerPosIndicator(ENG_OverlayManager overlayManager, boolean skipGLDelete) {
        if (playerPosCreated) {
            overlayManager.destroyOverlayAndChildren("player_pos", skipGLDelete);
            playerPosCreated = false;
        }
    }

    private void destroyGameResourcesCheckerIndicator(ENG_OverlayManager overlayManager, boolean skipGLDelete) {
        if (gameResourcesCheckerCreated) {
            overlayManager.destroyOverlayAndChildren("game_resource_checker", skipGLDelete);
            gameResourcesCheckerCreated = false;
        }
    }

    private void destroyMovementFlares() {
        if (movementFlaresCreated) {
            movementFlareManager.destroy();
            movementFlaresCreated = false;
        }

    }

    private void resetHudVariables() {
        scrollStartingPercentage = 0;
        maxScrollPercentageChange = ENG_ScrollOverlayContainer.DEFAULT_MAX_PERCENTAGE_CHANGE;
    }

    public void reset() {
        weaponHudShown = true;
        collisionPositionValid = false;
        aimAssisting = false;
        radarVisibilityDataMap.clear();
    }

    public void createDebuggingIndicators() {
        if (SHOW_DEBUGGING_INDICATORS) {
            createFpsIndicator();
            createPingIndicator();
            createPlayerPosIndicator();
            if (APP_Game.GAME_RESOURCE_UPDATE_CHECKER_ENABLED) {
                createGameResourcesUpdaterCheckerIndicator();
            }
        }
    }

    private void createFpsIndicator() {
        if (!fpsCreated) {
//            ENG_OverlayLoader.loadOverlay("overlay_fps.txt", MainApp.getGame().getGameResourcesDir(), true);
            ENG_Overlay fpsOverlay = ENG_OverlayManager.getSingleton().create("fps");
            fpsIndicator = (ENG_TextAreaOverlayElement) (fpsOverlay.getChild("fps_indicator").getChild("fps_text"));
            fpsIndicator.setCaption("");
            fpsOverlay.show();
            fpsCreated = true;
        }
    }

    private void createPingIndicator() {
        if (!pingCreated) {
//            ENG_OverlayLoader.loadOverlay("overlay_ping.txt", MainApp.getGame().getGameResourcesDir(), true);
            ENG_Overlay pingOverlay = ENG_OverlayManager.getSingleton().create("ping");
            pingIndicator = (ENG_TextAreaOverlayElement) (pingOverlay.getChild("ping_indicator").getChild("ping_text"));
            pingOverlay.show();
            pingCreated = true;
        }
    }

    private void createPlayerPosIndicator() {
        if (!playerPosCreated) {
            ENG_Overlay playerPosOverlay = ENG_OverlayManager.getSingleton().create("player_pos");
            playerPosIndicator = (ENG_TextAreaOverlayElement) (playerPosOverlay.getChild("player_pos_indicator").getChild("player_pos_text"));
            playerPosOverlay.show();
            playerPosCreated = true;
        }
    }

    private void createGameResourcesUpdaterCheckerIndicator() {
        if (!gameResourcesCheckerCreated)  {
            ENG_Overlay gameResourcesCheckerOverlay = ENG_OverlayManager.getSingleton().create("game_resource_checker");
            gameResourcesCheckerIndicator = (ENG_TextAreaOverlayElement) (gameResourcesCheckerOverlay.getChild("game_resource_checker_indicator").getChild("game_resource_checker_text"));
            gameResourcesCheckerOverlay.show();
            gameResourcesCheckerCreated = true;
        }
    }
    
    public void loadHudOverlays() {
        ENG_OverlayManager overlayManager = ENG_OverlayManager.getSingleton();
        ENG_Overlay hud = overlayManager.create("hud");
        System.out.println("hud loaded");
    }

    private void createHud() {

        // Stop rendering
//		ENG_RenderRoot.getRenderRoot().setSwapBufferEnabled(false);

        // Autoloaded by Ogre.
//        String gameDir = MainApp.getGame().getGameResourcesDir();
//        ENG_OverlayLoader.loadOverlay("overlay.txt", gameDir, true);
//
//        ENG_OverlayLoader.loadOverlay("overlay_enemy_selection.txt", gameDir, true);
//
//        // The crosshair is the box one the crosshairCross is the
//        // center_crosshair
//        // and the cross is the cross. Damn naming screw-up.
//
//        ENG_OverlayLoader.loadOverlay("overlay_cross.txt", gameDir, true);
//        ENG_OverlayLoader.loadOverlay("overlay_crosshair.txt", gameDir, true);
//        ENG_OverlayLoader.loadOverlay("overlay_crosshair_cross.txt", gameDir, true);
//
//        ENG_OverlayLoader.loadOverlay("overlay_lens_flare.txt", gameDir, true);
//
//        ENG_OverlayLoader.loadOverlay("overlay_beacon_dir.txt", gameDir, true);

        ENG_OverlayManager overlayManager = ENG_OverlayManager.getSingleton();
        // This is the crosshair for homing weapons.
        crosshairOuterOverlay = overlayManager.create("crosshair_crosshair");
        // We don't need a completely new overlay for the inner crosshair.
        // Could have done with only a new cloned material, but since
        // the material might change in the future let's just change everything now
        // and make it more flexible for the future.
        crosshairInnerOverlay = overlayManager.create("crosshair_crosshair_inner");
        // This is the plus symbol crosshair for dumb non-homing weapons.
        crosshairCrossOverlay = overlayManager.create("crosshair_cross");
        crossOverlay = overlayManager.create("cross");
        beaconDirOverlay = overlayManager.create("beacon_dir");
        enemySelectionOutsideScreenOverlay = overlayManager.create("enemy_selection_outside_screen");

        crosshairOuter = crosshairOuterOverlay.getChild("crosshair_outer");
        // We use the inner crosshair to signal the player that he still needs to centre
        // on an enemy for the selection time to actually start.
        // Without this it's a bit ambiguous why the selection takes too long.
        crosshairInner = crosshairInnerOverlay.getChild("crosshair_inner");
        crosshairCross = crosshairCrossOverlay.getChild("crosshair_cross");
        cross = crossOverlay.getChild("cross");
        enemySelectionOutsideScreen = enemySelectionOutsideScreenOverlay.getChild("enemy_selection_outside_screen");

        beaconDirLeftContainerElement = beaconDirOverlay.getChild("beacon_dir_left");
        beaconDirUpContainerElement = beaconDirOverlay.getChild("beacon_dir_up");
        beaconDirRightContainerElement = beaconDirOverlay.getChild("beacon_dir_right");
        beaconDirDownContainerElement = beaconDirOverlay.getChild("beacon_dir_down");

        // Put the crosshair on the centre of the screen
        centreCrosshair(crosshairOuter);
        centreCrosshair(crosshairInner, 0.2f, 0.2f);
        centreCrosshair(crosshairCross);

        // Also init the cross crosshair even though we don't have the position
        createCross();
        // Same for enemy selection crosshair.
        createEnemySelectionOutsideScreen();

        enemySelectionOverlay = overlayManager.create("enemy_selection");

        enemySelection = enemySelectionOverlay.getChild("EnemySelection");
        enemySelectionText = (ENG_TextAreaOverlayElement) enemySelection.getChild("EnemyHealth");

//        ENG_NativeCalls.unlitDatablock_setUseColour(enemySelection.getDatablock(), true);
//        ENG_NativeCalls.unlitDatablock_setColour(enemySelection.getDatablock(), ENG_ColorValue.WHITE);

//        ENG_GpuProgramParameters gpuProgramParameters = new ENG_GpuProgramParameters();
//        ENG_NativeCalls.gpuProgramParams_getProgramParams(
//                gpuProgramParameters, enemySelection, (short) 0, (short) 0, ENG_NativeCalls.GpuProgramParametersType.GPU_FRAGMENT);
//        ENG_NativeCalls.gpuProgramParams_setNamedConstant(gpuProgramParameters, "color", ENG_ColorValue.WHITE);

//        ENG_GpuProgramParameters enemySelectionParameters = enemySelection.getMaterial().getTechnique((short) 0).getPass((short) 0).getFragmentProgramParameters();
//        enemySelectionParameters.setNamedConstant("color", ENG_ColorValue.WHITE);

        ENG_Overlay overlay = overlayManager.getByName("hud");
        hudOverlay = overlay;

        healthOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("Health").getChild("HealthText");
        addVibrationElement(healthOverlayElement);
        spawnInfoOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("SpawnInfo").getChild("SpawnInfoText");
        addVibrationElement(spawnInfoOverlayElement);
        playerSpawnInfoOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("PlayerSpawnInfo").getChild("PlayerSpawnInfoText");
        addVibrationElement(playerSpawnInfoOverlayElement);
        beaconInfoOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("BeaconInfo").getChild("BeaconInfoText");
        addVibrationElement(beaconInfoOverlayElement);
        cargoCountOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("CargoCount").getChild("CargoCountText");
        addVibrationElement(cargoCountOverlayElement);
        cargoScanOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("CargoScan").getChild("CargoScanText");
        addVibrationElement(cargoScanOverlayElement);
        tutorialInfoOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("TutorialInfo").getChild("TutorialInfoText");
        enemySelectedNoEnemyOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("EnemySelectionNoEnemy").getChild("EnemySelectionNoEnemyText");
        addVibrationElement(enemySelectedNoEnemyOverlayElement);
        enemySelectedDistanceOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("EnemySelectionDistance").getChild("EnemySelectionDistanceText");
        addVibrationElement(enemySelectedDistanceOverlayElement);
        aboveCrosshairOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("AboveCrosshairMessage").getChild("AboveCrosshairMessageText");
        addVibrationElement(aboveCrosshairOverlayElement);
        belowCrosshairOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("BelowCrosshairMessage").getChild("BelowCrosshairMessageText");
        addVibrationElement(belowCrosshairOverlayElement);

        weaponOverlayElement = (ENG_TextAreaOverlayElement) overlay.getChild("WeaponType").getChild("WeaponText");
        addVibrationElement(weaponOverlayElement);

        ENG_OverlayElement afterburnerElement = overlay.getChild("Afterburner");
        if (MainApp.Platform.isMobile()) {
            afterburnerButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(afterburnerElement);
            addVibrationElement(afterburnerElement);
        } else {
            afterburnerElement.hide();
        }


        ENG_OverlayElement countermeasuresElement = overlay.getChild("Countermeasures");
        if (MainApp.Platform.isMobile()) {
            countermeasuresButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(countermeasuresElement);
            addVibrationElement(countermeasuresElement);
        } else {
            countermeasuresElement.hide();
        }



        ENG_OverlayElement reloaderElement = overlay.getChild("Reloader");
        if (MainApp.Platform.isMobile()) {
            reloaderButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(reloaderElement);
            addVibrationElement(reloaderElement);
        } else {
            reloaderElement.hide();
        }


        ENG_OverlayElement weaponSelectionLeftElement = overlay.getChild("WeaponSelectionLeft");
        if (MainApp.Platform.isMobile()) {
            weaponSelectionPreviousButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(weaponSelectionLeftElement);
            addVibrationElement(weaponSelectionLeftElement);
        } else {
            weaponSelectionLeftElement.hide();
        }


        ENG_OverlayElement weaponSelectionRightElement = overlay.getChild("WeaponSelectionRight");
        if (MainApp.Platform.isMobile()) {
            weaponSelectionNextButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(weaponSelectionRightElement);
            addVibrationElement(weaponSelectionRightElement);
        } else {
            weaponSelectionRightElement.hide();
        }


        ENG_OverlayElement rotateLeftElement = overlay.getChild("RotateLeft");
        if (MainApp.Platform.isMobile()) {
            rotateLeftButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(rotateLeftElement);
            addVibrationElement(rotateLeftElement);
        } else {
            rotateLeftElement.hide();
        }


        ENG_OverlayElement rotateRightElement = overlay.getChild("RotateRight");
        if (MainApp.Platform.isMobile()) {
            rotateRightButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(rotateRightElement);
            addVibrationElement(rotateRightElement);
        } else {
            rotateRightElement.hide();
        }

        ENG_OverlayElement enemySelectionLeftElement = overlay.getChild("EnemySelectionLeft");
        if (MainApp.Platform.isMobile()) {
            enemySelectionLeftButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(enemySelectionLeftElement);
            addVibrationElement(enemySelectionLeftElement);
        } else {
            enemySelectionLeftElement.hide();
        }


        ENG_OverlayElement enemySelectionRightElement = overlay.getChild("EnemySelectionRight");
        if (MainApp.Platform.isMobile()) {
            enemySelectionRightButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(enemySelectionRightElement);
            addVibrationElement(enemySelectionRightElement);
        } else {
            enemySelectionRightElement.hide();
        }

        ENG_OverlayElement attackSelectedEnemyElement = overlay.getChild("AttackSelectedEnemy");
        if (MainApp.Platform.isMobile()) {
            attackSelectedEnemyButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(attackSelectedEnemyElement);
            if (ENABLE_ATTACK_SELECTED_ENEMY) {
                addVibrationElement(attackSelectedEnemyElement);
            } else {
                attackSelectedEnemyElement.hide();
            }
        } else {
            attackSelectedEnemyElement.hide();
        }

        ENG_OverlayElement defendPlayerShipElement = overlay.getChild("DefendPlayerShip");
        if (MainApp.Platform.isMobile()) {
            defendPlayerShipButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(defendPlayerShipElement);
            if (ENABLE_DEFEND_PLAYER_SHIP) {
                addVibrationElement(defendPlayerShipElement);
            } else {
                defendPlayerShipElement.hide();
            }
        } else {
            defendPlayerShipElement.hide();
        }


        ENG_OverlayElement fireButtonElement = overlay.getChild("FireButton");
        if (MainApp.Platform.isMobile()) {
            fireButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(fireButtonElement);
            addVibrationElement(fireButtonElement);
        } else {
            fireButtonElement.hide();
        }

        ENG_OverlayContainer backButtonElement = overlay.getChild("BackButton");
        if (MainApp.Platform.isMobile()) {
            backButtonOverlay = ENG_GUIOverlayManager.getSingleton().createButtonOverlayElement(backButtonElement);
            addVibrationElement(backButtonElement);
        } else {
            backButtonElement.hide();
        }

        /*
		 * ENG_GpuProgramParameters fireButtonParameters = child.getMaterial()
		 * .getTechnique((short) 0).getPass((short) 0)
		 * .getFragmentProgramParameters();
		 * fireButtonParameters.setNamedConstant("color", child.getColour());
		 */


        speedScrollOverlay = ENG_GUIOverlayManager.getSingleton()
                .createScrollOverlayContainer(overlay.getChild("SpeedMeter"), overlay.getChild("SpeedMeter").getChild("SpeedIndicator"), ScrollType.VERTICAL);
        speedScrollOverlay.setMaxPercentageChange(maxScrollPercentageChange);
        speedScrollOverlay.setPercentage(scrollStartingPercentage);
        addVibrationElement(speedScrollOverlay.getContainer());

        // This is pretty much dead and gone.
//        ENG_OverlayContainer controls = overlay.getChild("MovementControls");
////        if (MainApp.PLATFORM == MainApp.Platform.ANDROID) {
////            EnumMap<ENG_ControlsOverlayElement.ControlDirection, Integer> frames = new EnumMap<ENG_ControlsOverlayElement.ControlDirection, Integer>(
////                    ENG_ControlsOverlayElement.ControlDirection.class);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.NONE, 0);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.LEFT, 1);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.LEFT_UP, 2);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.UP, 3);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.RIGHT_UP, 4);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.RIGHT, 5);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.RIGHT_DOWN, 6);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.DOWN, 7);
////            frames.put(ENG_ControlsOverlayElement.ControlDirection.LEFT_DOWN, 8);
////            controlsOverlayElement = ENG_GUIOverlayManager.getSingleton().createControlsOverlayElement(controls, frames);
////        } else {
//            controls.hide();
////        }


        ENG_OverlayContainer radarOverlay = overlay.getChild("Radar");
        addVibrationElement(radarOverlay, GuiMetricsMode.GMM_PIXELS);

        // Ignore the settings from the hud overlay file and centre it
        // GLRenderSurface renderSurface = GLRenderSurface.getSingleton();

        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();

        float screenDensity = ENG_RenderRoot.getRenderRoot().getScreenDensity();
        int width = window.getWidth();
        int height = window.getHeight();
        ENG_TextureNative radarTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(radarTexture, radarOverlay, (short) 0, (short) 0, 0);
//        ENG_Texture radarTexture = ENG_TextureManager.getSingleton().getByName(
//                radarOverlay.getMaterial().getTechnique((short) 0).getPass((short) 0).getTextureUnitState(0).getTextureName());
        radarWidth = radarTexture.getWidth();
        int radarHeight = radarTexture.getHeight();

//        radarWidth = (int) (radarTexture.getWidth() * 2 * screenDensity);
//        radarHeight = (int) (radarTexture.getHeight() * 2 * screenDensity);

        // We are forcing the width and height from the previous 64*64 radar image.
        int virtualRadarWidth = (int) (64 * 2 * screenDensity);
        int virtualRadarHeight = (int) (64 * 2 * screenDensity);
        radarOverlay.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        radarOverlay.setLeft((float) width / 2 - (float) virtualRadarWidth / 2);
        radarOverlay.setTop(height - 0.01f * height - virtualRadarHeight);
        radarOverlay.setWidth(virtualRadarWidth);
        radarOverlay.setHeight(virtualRadarHeight);

        radarOverlayElement = ENG_GUIOverlayManager.getSingleton().createDynamicOverlayElement(radarOverlay, "radar.png", "Essential");
        // After the radar creation so we can put it above the radar.
        createBeaconDir(radarOverlay);
        weaponHudShown = true;
        recolorCrosshair = true;
        healthShown = true;
        created = true;

//		createMovementFlares();
		hitMarker = new HitMarker(viewMatrix, projMatrix);
//		ENG_RenderRoot.getRenderRoot().setSwapBufferEnabled(true);
    }

    private void createMovementFlares() {
        movementFlareManager.setup();
        movementFlaresCreated = true;
    }

    private enum BeaconDir {
        LEFT, UP, RIGHT, DOWN
    }

    private int getBeaconDirInFrame(BeaconDir dir) {
        switch (dir) {
            case LEFT:
                return 0;
            case UP:
                return 1;
            case RIGHT:
                return 2;
            case DOWN:
                return 3;
        }
        throw new IllegalArgumentException(dir
                + " is an invalid beacon direction");
    }

    private void setBeaconDir(BeaconDir dir) {
        switch (dir) {
            case LEFT:
                beaconDirLeftContainerElement.show();
                beaconDirUpContainerElement.hide();
                beaconDirRightContainerElement.hide();
                beaconDirDownContainerElement.hide();
                break;
            case UP:
                beaconDirLeftContainerElement.hide();
                beaconDirUpContainerElement.show();
                beaconDirRightContainerElement.hide();
                beaconDirDownContainerElement.hide();
                break;
            case RIGHT:
                beaconDirLeftContainerElement.hide();
                beaconDirUpContainerElement.hide();
                beaconDirRightContainerElement.show();
                beaconDirDownContainerElement.hide();
                break;
            case DOWN:
                beaconDirLeftContainerElement.hide();
                beaconDirUpContainerElement.hide();
                beaconDirRightContainerElement.hide();
                beaconDirDownContainerElement.show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dir);
        }
//        throw new UnsupportedOperationException();

//        beaconDirContainerElement.getMaterial().getTechnique((short) 0)
//                .getPass((short) 0).getTextureUnitState(0)
//                .setCurrentFrame(getBeaconDirInFrame(dir));
    }

    private void createBeaconDir(ENG_OverlayContainer radarOverlay) {
        beaconDirLeftContainerElement.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        beaconDirUpContainerElement.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        beaconDirRightContainerElement.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        beaconDirDownContainerElement.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        ENG_TextureNative beaconDirLeftTexture = new ENG_TextureNative();
        ENG_TextureNative beaconDirUpTexture = new ENG_TextureNative();
        ENG_TextureNative beaconDirRightTexture = new ENG_TextureNative();
        ENG_TextureNative beaconDirDownTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(beaconDirLeftTexture, beaconDirLeftContainerElement, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(beaconDirUpTexture, beaconDirUpContainerElement, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(beaconDirRightTexture, beaconDirRightContainerElement, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(beaconDirDownTexture, beaconDirDownContainerElement, (short) 0, (short) 0, 0);
//        ENG_Texture beaconDirTexture = ENG_TextureManager.getSingleton()
//                .getByName(
//                        beaconDirContainerElement.getMaterial()
//                                .getTechnique((short) 0).getPass((short) 0)
//                                .getTextureUnitState(0).getTextureName());
//        if (beaconDirTexture == null) {
//            throw new NullPointerException("Make sure the beacon_dir "
//                    + " texture is loaded "
//                    + "before trying to get its dimensions");
//        }

        float beaconDirHalfWidth = (float) (beaconDirLeftTexture.getWidth() / 2);
        float beaconDirHalfHeight = (float) (beaconDirLeftTexture.getHeight() / 2);

        // GLRenderSurface renderSurface = GLRenderSurface.getSingleton();
        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot()
                .getCurrentRenderWindow();
        int width = window.getWidth();
        int height = window.getHeight();
        setBeaconOverlayElementPosition(radarOverlay, beaconDirLeftTexture, width, beaconDirLeftContainerElement, beaconDirHalfWidth, beaconDirHalfHeight);
        setBeaconOverlayElementPosition(radarOverlay, beaconDirUpTexture, width, beaconDirUpContainerElement, beaconDirHalfWidth, beaconDirHalfHeight);
        setBeaconOverlayElementPosition(radarOverlay, beaconDirRightTexture, width, beaconDirRightContainerElement, beaconDirHalfWidth, beaconDirHalfHeight);
        setBeaconOverlayElementPosition(radarOverlay, beaconDirDownTexture, width, beaconDirDownContainerElement, beaconDirHalfWidth, beaconDirHalfHeight);
        beaconDirOverlay.hide();
    }

    private static void setBeaconOverlayElementPosition(ENG_OverlayContainer radarOverlay, ENG_TextureNative beaconDirTexture, int width, ENG_OverlayContainer beaconDirContainerElement, float beaconDirHalfWidth, float beaconDirHalfHeight) {
        beaconDirContainerElement.setLeft((float) width / 2 - beaconDirHalfWidth);
        beaconDirContainerElement.setTop(radarOverlay.getTop()
                - beaconDirTexture.getHeight() - beaconDirHalfHeight);
        beaconDirContainerElement.setWidth(beaconDirTexture.getWidth());
        beaconDirContainerElement.setHeight(beaconDirTexture.getHeight());
    }

    private void createCross() {
        cross.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        ENG_TextureNative crosshairCrossTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(crosshairCrossTexture, cross, (short) 0, (short) 0, 0);
//        ENG_Texture crosshairCrossTexture = ENG_TextureManager.getSingleton()
//                .getByName(
//                        cross.getMaterial().getTechnique((short) 0)
//                                .getPass((short) 0).getTextureUnitState(0)
//                                .getTextureName());
//        if (crosshairCrossTexture == null) {
//            throw new NullPointerException("Make sure the cross "
//                    + "crosshair texture is loaded "
//                    + "before trying to get its dimensions");
//        }
        cross.setWidth(crosshairCrossTexture.getWidth());
        cross.setHeight(crosshairCrossTexture.getHeight());

        crossHalfWidth = (float) (crosshairCrossTexture.getWidth() / 2);
        crossHalfHeight = (float) (crosshairCrossTexture.getHeight() / 2);
    }

    private void createEnemySelectionOutsideScreen() {
        enemySelectionOutsideScreen.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        ENG_TextureNative enemySelectionOutsideScreenTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(enemySelectionOutsideScreenTexture, enemySelectionOutsideScreen, (short) 0, (short) 0, 0);
        enemySelectionOutsideScreen.setWidth(enemySelectionOutsideScreenTexture.getWidth());
        enemySelectionOutsideScreen.setHeight(enemySelectionOutsideScreenTexture.getHeight());

        enemySelectionOutsideScreenHalfWidth = (float) (enemySelectionOutsideScreenTexture.getWidth() / 2);
        enemySelectionOutsideScreenHalfHeight = (float) (enemySelectionOutsideScreenTexture.getHeight() / 2);
    }

    private void centreCrosshair(ENG_OverlayContainer crosshair) {
        centreCrosshair(crosshair, 1.0f, 1.0f);
    }

    private void centreCrosshair(ENG_OverlayContainer crosshair, float widthResize, float heightResize) {
        crosshair.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
        // GLRenderSurface renderSurface = GLRenderSurface.getSingleton();
        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot()
                .getCurrentRenderWindow();
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        ENG_TextureNative crosshairTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(crosshairTexture, crosshair, (short) 0, (short) 0, 0);
//        ENG_Texture crosshairTexture = ENG_TextureManager.getSingleton()
//                .getByName(
//                        crosshair.getMaterial().getTechnique((short) 0)
//                                .getPass((short) 0).getTextureUnitState(0)
//                                .getTextureName());
//        if (crosshairTexture == null) {
//            throw new NullPointerException(
//                    "Make sure the crosshair texture is loaded "
//                            + "before trying to get its dimensions");
//        }
        float textureWidth = crosshairTexture.getWidth() * widthResize;
        float textureHeight = crosshairTexture.getHeight() * heightResize;
        float crossLeft = (float) windowWidth / 2 - textureWidth / 2;
        float crossTop = (float) windowHeight / 2 - textureHeight / 2;
        float crossWidth = textureWidth;
        float crossHeight = textureHeight;
        ENG_Log.getInstance().log("centreCrosshair() windowWidth: " + windowWidth + " windowHeight: " + windowHeight + " crossLeft: " + crossLeft + " crossTop: " + crossTop + " crossWidth: " + crossWidth + " crossHeight: " + crossHeight);
        crosshair.setLeft(crossLeft);
        crosshair.setTop(crossTop);
        crosshair.setWidth(crossWidth);
        crosshair.setHeight(crossHeight);
    }

    public void setFps(float fps) {
        if (fpsCreated) {
            fpsIndicator.setCaption(String.valueOf(fps));
        }
    }

    public void setPing(int ping) {
        if (pingCreated) {
            pingIndicator.setCaption(String.valueOf(ping));
        }
    }

    public void setGameResourcesCheckerStatus(String text, long duration) {
        if (gameResourcesCheckerCreated) {
            gameResourcesCheckerIndicator.setCaption(text);
            gameResourcesCheckerIndicator.show();
            gameResourcesCheckerBeginTime = currentTimeMillis();
            gameResourcesCheckerDuration = duration;
        }
    }

    private final NumberFormat formatter = new DecimalFormat("#0000.00");

    public void setPlayerPos(ENG_Vector3D pos) {
        if (playerPosCreated) {
            String s = "x: " + formatter.format(pos.x) + " y: " + formatter.format(pos.y) + " z: " + formatter.format(pos.z);
//            System.out.println("playerPos: " + s);
            playerPosIndicator.setCaption(s);
        }
    }

    public boolean isShowMovementControls() {
        return showMovementControls;
    }

    public void setShowMovementControls(boolean showMovementControls) {
        this.showMovementControls = showMovementControls;
    }

    public ENG_TextAreaOverlayElement getFpsIndicator() {
        return fpsIndicator;
    }

    public int getMaxScrollPercentageChange() {
        return maxScrollPercentageChange;
    }

    public void setMaxScrollPercentageChange(int maxScrollPercentageChange) {
        this.maxScrollPercentageChange = maxScrollPercentageChange;
    }

    public int getScrollStartingPercentage() {
        return scrollStartingPercentage;
    }

    public void setScrollStartingPercentage(int scrollStartingPercentage) {
        this.scrollStartingPercentage = scrollStartingPercentage;
    }

    public ENG_ScrollOverlayContainer getSpeedScrollOverlay() {
        return speedScrollOverlay;
    }

    public ENG_TextAreaOverlayElement getWeaponOverlayElement() {
        return weaponOverlayElement;
    }

    public ENG_Item getCurrentSelectedEnemy() {
        return currentSelectedEnemyFollowable ? currentSelectedEnemy : null;
    }

    public void setSpawnInfoText(String text) {
        if (visible) {
            spawnInfoOverlayElement.setCaption(text);
            spawnInfoTextChanged = true;
        }
    }

    public void setPlayerSpawnInfoText(String text) {
        if (visible) {
            playerSpawnInfoOverlayElement.setCaption(text);
            playerSpawnInfoTextChanged = true;
        }
    }

    public void setTutorialInfoText(String text) {
        setTutorialInfoText(text, 0);
    }

    public void setTutorialInfoText(String text, long timeShown) {
        if (visible) {
            tutorialInfoOverlayElement.setCaption(text);
            tutorialInfoTextChanged = true;
            if (timeShown > 0) {
                tutorialInfoTextTimeShown = timeShown;
            } else {
                tutorialInfoTextTimeShown = TUTORIAL_INFO_TIME;
            }
        }
    }

    public void setBelowCrosshairText(String text) {
        setBelowCrosshairText(text, 0);
    }

    public void setBelowCrosshairText(String text, long timeShown) {
        if (visible) {
            belowCrosshairOverlayElement.setCaption(text);
            belowCrosshairTextChanged = true;
            if (timeShown > 0) {
                belowCrosshairTextTimeShown = timeShown;
            } else {
                belowCrosshairTextTimeShown = BELOW_CROSSHAIR_INFO_TIME;
            }
        }
    }

    public void hit(long entityId) {
        if (visible) {
            hitMarker.hit(entityId);
        }
    }

    public void vibrate(HudVibrationType vibrationType) {
        vibrate(vibrationType.getTime(),
                vibrationType.getVibrationDistanceX(),
                vibrationType.getVibrationDistanceY(),
                vibrationType.getVibrationsPerSecondWaitTime());
    }

    public void vibrate(long duration) {
        vibrate(duration, VIBRATION_DISTANCE_X, VIBRATION_DISTANCE_Y, currentVibrationsPerSecondWaitTime);
    }

    public void vibrate(long duration, long vibrationsPerSecond) {
        if (vibrationsPerSecond == 0) {
            throw new ENG_DivisionByZeroException("vibrationsPerSecond must not be 0");
        }
        vibrate(duration, VIBRATION_DISTANCE_X, VIBRATION_DISTANCE_Y, 1000 / vibrationsPerSecond);
    }

    /**
     *
     * @param duration
     * @param vibrationDistanceX
     * @param vibrationDistanceY
     * @param vibrationsPerSecondWaitTime MAKE SURE TO DIVIDE 1000 / vibrationsPerSecond. This param doesn't do it for you!!! Use 0 or negative number to reset to default VIBRATIONS_PER_SECOND.
     */
    public void vibrate(long duration, float vibrationDistanceX, float vibrationDistanceY, long vibrationsPerSecondWaitTime) {
        if (!HUD_VIBRATION_ENABLED) {
            return;
        }
        if (vibrationActive) {
            resetToOriginalHudElementsPosition();
        }
        currentVibrationDuration = duration;
        currentVibrationDistanceX = vibrationDistanceX;
        currentVibrationDistanceY = vibrationDistanceY;
        if (vibrationsPerSecondWaitTime <= 0) {
            currentVibrationsPerSecondWaitTime = 1000 / VIBRATIONS_PER_SECOND;
        } else {
            currentVibrationsPerSecondWaitTime = vibrationsPerSecondWaitTime;
        }
        currentVibrationStartTime = currentTimeMillis();
        vibrationActive = true;
        saveOriginalHudElementsPosition();
    }

    public void stopVibration() {
        if (vibrationActive) {
            resetToOriginalHudElementsPosition();
        }
        vibrationActive = false;
    }

    public long getCurrentVibrationsPerSecondWaitTime() {
        return currentVibrationsPerSecondWaitTime;
    }

    public long getVibrationsPerSecondNum() {
        return 1000 / currentVibrationsPerSecondWaitTime;
    }

    public void setVibrationsPerSecondNum(int vibrationsPerSecondNum) {
        this.currentVibrationsPerSecondWaitTime = 1000 / vibrationsPerSecondNum;
    }

    public WeaponType getCurrentWeaponType() {
        return currentWeaponType;
    }

    public boolean isAimAssisting() {
        return aimAssisting;
    }

    public static HudManager getSingleton() {
//        if (mgr == null && MainActivity.isDebugmode()) {
//            throw new NullPointerException("Hud manager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getHudManager();
    }
}
