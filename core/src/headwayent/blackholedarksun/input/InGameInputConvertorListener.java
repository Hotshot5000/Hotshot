/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/13/21, 6:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.input;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;
import java.util.LinkedList;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData.WeaponType;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.LevelEventStatistics;
import headwayent.blackholedarksun.statistics.LevelStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer;
import headwayent.hotshotengine.input.ENG_InputConvertorListener;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

public class InGameInputConvertorListener extends ENG_InputConvertorListener {

    public static final float MIN_SHIP_SENSITIVITY = 1.0f;
    public static final float MAX_SHIP_SENSITIVITY = 18.0f;
    public static final float DEFAULT_SHIP_SENSITIVITY = 12.0f;
    private static final int MOUSE_MOVEMENTS_COUNT = 1;
    private static final int BOOST_MOVEMENTS_COUNT = 5;
    private static final float MOUSE_DIV = 1.0f / (MOUSE_MOVEMENTS_COUNT);
    private static final float MAX_SENSITIVITY_PER_FRAME = 5.0f;
    private final InGameInputConvertor inputConvertor;
    private ENG_ScrollOverlayContainer speedScrollContainer;
    private long countermeasureLastTime;
    private long fireLastTime;
    private long fireWaitingTime;
    private float sensitivity = DEFAULT_SHIP_SENSITIVITY;
    private float invSensitivity = 1.0f / sensitivity;
    private long afterburnerLastTime;
    private final LinkedList<ENG_Vector3D> mouseMovements = new LinkedList<>();
    private final LinkedList<MouseEvent> mouseEvents = new LinkedList<>();
    private final LinkedList<ENG_Float> accelerationList = new LinkedList<>();
    private InGameEvent event;
    private final InGameEvent[] eventList;
    private byte callNum;
    private boolean shouldClearForcesAppliedToPlayerShip;

    private static class MouseEvent {
        public int x;
        public int y;
        public boolean boostApplied;
    }

    public InGameInputConvertorListener(InGameInputConvertor inputConvertor//,
            /*ENG_ScrollOverlayContainer speedScrollContainer*/) {
        this.inputConvertor = inputConvertor;
        eventList = inputConvertor.getEvent();
//		this.speedScrollContainer = speedScrollContainer;

        for (int i = 0; i < MOUSE_MOVEMENTS_COUNT; ++i) {
            mouseMovements.add(new ENG_Vector3D());
        }
        for (int i = 0; i < BOOST_MOVEMENTS_COUNT; ++i) {
            mouseEvents.add(new MouseEvent());
        }
    }

    public void reset() {
        resetMouseMovements();
        if (speedScrollContainer != null) {
            speedScrollContainer.setPercentage(0);
        }
    }

    public void resetMouseMovements() {
        for (ENG_Vector3D v : mouseMovements) {
            v.set(0.0f);
        }
        for (MouseEvent e : mouseEvents) {
            e.x = 0;
            e.y = 0;
            e.boostApplied = false;
        }
    }

//    private boolean angularVelocitySet;
    private final ENG_Vector3D result = new ENG_Vector3D();
    private final Vector3 angularVelocity = new Vector3();
//    private int frameNum;
//    private boolean dir = true;


    public ENG_Vector3D getResult() {
        return result;
    }

    @Override
    public void routeInput() {


        // Avoid the dynamic_cast since it kills performance.
//        long inputConvertorReadBeginTime = ENG_Utility.nanoTime();
        inputConvertor.read();
//        long inputConvertorReadTime = ENG_Utility.nanoTime();
//        long inputConvertorReadTimeDelta = inputConvertorReadTime - inputConvertorReadBeginTime;
        if (callNum == 0) {
            event = eventList[0];
            callNum = 1;
        } else if (callNum == 1) {
            event = eventList[1];
            callNum = 0;
        }

        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip == null) {
//            throw new NullPointerException("playerShip not initialized. Make sure the level has been loaded before setting the input");
            // Cannot fully control when the player ship is not null since we may destroy it and for the next frame we still think it exists while
            // the artemis entity manager has actually removed it for real.
            return;
        }
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);
        ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().get(playerShip);
        WeaponProperties weaponProperties = worldManager.getWeaponPropertiesComponentMapper().get(playerShip);

//        float turnAngle = shipProperties.getShipData().turnAngle;
        float delta = GameWorld.getWorld().getDelta();
        float invDelta = 1.0f / delta;
        float inv = MainApp.getGame().isInvertYAxis() ? -1.0f : 1.0f;


        float yawDeg = /*turnAngle * */delta * -event.dx * sensitivity;
        float pitchDeg = /*-turnAngle **/ -delta * inv * event.dy * sensitivity;
        float rollDeg = /*turnAngle * */delta * -event.rotate * sensitivity * 10;

//        if (event.dx != 0 || event.dy != 0) {
//            System.out.println("delta: " + delta + " event.x: " + event.dx + " event.y: " + event.dy);
//        }

//        yawDeg = ENG_Math.clamp(yawDeg,
//                -MAX_SENSITIVITY_PER_FRAME,
//                MAX_SENSITIVITY_PER_FRAME);
//        pitchDeg = ENG_Math.clamp(pitchDeg,
//                -MAX_SENSITIVITY_PER_FRAME,
//                MAX_SENSITIVITY_PER_FRAME);
//        rollDeg = ENG_Math.clamp(rollDeg,
//                -MAX_SENSITIVITY_PER_FRAME,
//                MAX_SENSITIVITY_PER_FRAME);

//		if (yawDeg != 0.0f) {
//			System.out.println("yawDeg: " + yawDeg);
//		}
//		if (pitchDeg != 0.0f) {
//			System.out.println("pitchDeg: " + pitchDeg);
//		}
//        float finalYawDeg = yawDeg;
//        float finalPitchDeg = pitchDeg;
//        float finalRollDeg = rollDeg;
//        for (ENG_Vector3D v : mouseMovements) {
//            finalYawDeg += v.x;
//            finalPitchDeg += v.y;
//            finalRollDeg += v.z;
//        }
//        if (finalYawDeg != 0 || finalPitchDeg != 0 || finalRollDeg != 0) {
//            System.out.println("finalYawDeg: " + finalYawDeg + " finalPitchDeg: " + finalPitchDeg + " finalRollDeg: " + finalPitchDeg);
//        }
//        finalYawDeg *= MOUSE_DIV;
//        finalPitchDeg *= MOUSE_DIV;
//        finalRollDeg *= MOUSE_DIV;
        ENG_Vector3D mouseMovement = mouseMovements.poll();
        mouseMovement.set(pitchDeg, yawDeg, rollDeg);
//        mouseMovement.normalize();
//		if (yawDeg != 0 || pitchDeg != 0 || rollDeg != 0) {
//			System.out.println("yawDeg: " + yawDeg + " pitchDeg: " + pitchDeg + " rollDeg: " + rollDeg);
//		}
//        if (finalYawDeg != 0 || finalPitchDeg != 0 || finalRollDeg != 0) {
//            System.out.println("finalYawDeg: " + finalYawDeg + " finalPitchDeg: " + finalPitchDeg + " finalRollDeg: " + finalPitchDeg);
//        }
        mouseMovements.add(mouseMovement);
//        entityProperties.yawDeg(finalYawDeg);
//        entityProperties.pitchDeg(finalPitchDeg);
//        entityProperties.rollDeg(finalRollDeg);

        MouseEvent mouseEvent = null;
        if (event.dx != 0 || event.dy != 0) {
            mouseEvent = mouseEvents.poll();
            mouseEvent.x = event.dx;
            mouseEvent.y = event.dy;
            mouseEvent.boostApplied = false;
            mouseEvents.add(mouseEvent);
//            System.out.println("mouseEvent updated: " + mouseEvent + " x: " + mouseEvent.x + " y: " + mouseEvent.y);
        }

        result.set(0.0f, 0.0f, 0.0f);
//        result.set(pitchDeg, yawDeg, rollDeg);
//        result.set(mouseMovement.x, mouseMovement.y, mouseMovement.z);
        int currentMouseMovement = MOUSE_MOVEMENTS_COUNT;
        float mouseMovementStep = 1.0f / currentMouseMovement;
        for (Iterator<ENG_Vector3D> it = mouseMovements.descendingIterator(); it.hasNext(); ) {
            ENG_Vector3D v = it.next();
//            if (!v.isZeroLength()) {
//                System.out.println("mouseMovements v: " + v + " len: " + v.length());
//            }
            float currentX = v.x * currentMouseMovement * mouseMovementStep;
            float currentY = v.y * currentMouseMovement * mouseMovementStep;
            float currentZ = v.z * currentMouseMovement * mouseMovementStep;
            result.addInPlace(currentX, currentY, currentZ);
//            if (!result.isZeroLength()) {
//                System.out.println("mouseMovements result: " + result + " len: " + result.length());
//            }
            if (--currentMouseMovement <= 0) {
                break;
            }
        }
//        result.nor();
//        if (dir) {
//            if (result.y < 5.0f) {
//                result.y += 0.01f;
//            } else {
//                result.y = 5.0f;
//                dir = false;
//            }
//        } else {
//            if (result.y > 0.0f) {
//                result.y -= 0.01f;
//            } else {
//                dir = true;
//                result.y = 0.0f;
//            }
//        }

//        result.x *= MOUSE_DIV;
//        result.y *= MOUSE_DIV;
//        result.z *= MOUSE_DIV;

//        System.out.println("after mouseMovements update result.length(): " + result.length());

        if (result.length() > sensitivity) {
            result.normalize();
            result.mulInPlace(sensitivity);
//            System.out.println("result normalized len: " + result.length() + " result: " + result);
        }

//        ENG_Vector3D beforeAcceleration = new ENG_Vector3D(result);

//        float part = 1.0f / (sensitivity * 0.5f);
//        float tanhX = ENG_Math.tanh(result.x * part);
//        float tanhY = ENG_Math.tanh(result.y * part);
//        float tanhZ = ENG_Math.tanh(result.z * part);
//        result.set(tanhX * sensitivity,
//                tanhY * sensitivity,
//                tanhZ * sensitivity);

        float shortResultLen = result.length();
        float activationPoint = 3.0f;
        float invActivationPoint = 1.0f / activationPoint;
        if (shortResultLen > 0.0f && shortResultLen * invDelta * invSensitivity < activationPoint) {
            float part = 1.0f / sensitivity;
//            ENG_Vector3D mul = result.mul(invActivationPoint * invDelta * invSensitivity);
//            System.out.println("restored result: " + mul);
            float expX = ENG_Math.pow(result.x * invActivationPoint * invDelta * invSensitivity, 3) * activationPoint * delta * sensitivity;
            float expY = ENG_Math.pow(result.y * invActivationPoint * invDelta * invSensitivity, 3) * activationPoint * delta * sensitivity;
            float expZ = ENG_Math.pow(result.z * invActivationPoint * invDelta * invSensitivity, 3) * activationPoint * delta * sensitivity;
            result.set(expX, expY, expZ);
//            System.out.println("result: " + result);
            boolean lenNorm = false;
            if (result.length() * invDelta * invSensitivity > activationPoint) {
                result.normalize();
                result.mulInPlace(activationPoint * delta * sensitivity);
                lenNorm = true;
            }
//            if (result.length() > 0.0f) {
//                System.out.println("Under 5.0 movement lenNorm: " + lenNorm);
//            }

            boolean addBoost = event.dx != 0 || event.dy != 0;
//            System.out.println("addBoost initial: " + addBoost);
            if (addBoost) {
                mouseEvent.boostApplied = true; // mouseEvent should never be null.
//                String mouseEventsListText = "mouseEvents:";
//                for (MouseEvent e : mouseEvents) {
//                    mouseEventsListText += " e.x: " + e.x + " e.y: " + e.y + " e.boostApplied: " + e.boostApplied;
//                }
//                System.out.println(mouseEventsListText);
                for (int i = 0; i < mouseEvents.size() - 2; ++i) {
                    MouseEvent e = mouseEvents.get(i);
                    if (e.x != this.event.dx || e.y != this.event.dy) {
                        addBoost = false;
                        break;
                    }
                }
            }
            if (addBoost) {
                float boost = 1.8f;
                // Also check if previous events also had boost so we boost even more.
                for (int i = mouseEvents.size() - 2; i >= 0; --i) {
                    if (mouseEvents.get(i).boostApplied) {
                        boost += 0.2f;
                    } else {
                        break;
                    }
                }
                result.mulInPlace(boost);
//                System.out.println("Boost applied: " + boost);
            }
        }

//        if (result.length() > 0.0f) {
//            long currentTime = ENG_Utility.nanoTime();
////            System.out.println("result: " + result.length() + " currentTime: " + currentTime + " inputConvertorReadTimeDelta: " + inputConvertorReadTimeDelta + " listenerDelta: " + (currentTime - inputConvertorReadTime));
//            System.out.println("result len: " + result.length() + " result: " + result + " beforeAcceleration len: " + beforeAcceleration.length() + " beforeAcceleration: " + beforeAcceleration);
//        }

        boolean normalized = false;
        float maxAngularVelocity = shipProperties.getShipData().maxAngularVelocity/* / MAX_SENSITIVITY_PER_FRAME*/;// * delta;// * 1500.0f;
//        if (result.length() > 0.0f) {
//            System.out.println("maxAngularVelocity: " + maxAngularVelocity);
//        }
//        if (result.length() > 0.0f) {
//            System.out.println("before maxAngularVelocity mulInPlace result.length(): " + result.length());
//        }
        result.mulInPlace(maxAngularVelocity);



//        result.x = ENG_Math.clamp(result.x,
//                -MAX_SENSITIVITY_PER_FRAME,
//                MAX_SENSITIVITY_PER_FRAME);
//        result.y = ENG_Math.clamp(result.y,
//                -MAX_SENSITIVITY_PER_FRAME,
//                MAX_SENSITIVITY_PER_FRAME);

//        if (result.len() > maxAngularVelocity) {
//            result = result.nor();
//            result.x *= maxAngularVelocity;
//            result.y *= maxAngularVelocity;
//            result.z *= maxAngularVelocity;
////            System.out.println("angularVelocity: " + result.len());
//            System.out.println("angularVelocity: " + result.len() + " x: " + result.x + " y: " + result.y + " z: " + result.z);
//            normalized = true;
//        } else {
//            if (result.len() > 0) {
//                System.out.println("angularVelocity default: " + result.len() + " x: " + result.x + " y: " + result.y + " z: " + result.z);
//            }
//        }

//        angularVelocity = entityProperties.getNode()._getFullTransform().transform(angularVelocity);
//        Vector3 vector3 = new Vector3(pitchDeg, yawDeg, rollDeg);
//        ENG_Vector3D mul = entityProperties.getNode().getOrientation().mul(result);
        angularVelocity.set(result.x, result.y, result.z);
        angularVelocity.mul(entityProperties.getRigidBody().getOrientation());

//        if (!result.isZeroLength()) {
//            System.out.println("result: " + result + " result.length(): " + result.length());
//            Vector3 axis = new Vector3();
//            float axisAngle = entityProperties.getRigidBody().getOrientation().getAxisAngle(axis);
//            System.out.println("orientation axis: " + axis + " angle: " + axisAngle);
//            ENG_Vector4D axisInternal = new ENG_Vector4D();
//            float axisAngleInternal = entityProperties.getNode().getOrientation().toAngleAxisDeg(axisInternal);
//            System.out.println("scene node axis: " + axisInternal + " angle: " + axisAngleInternal);
//        }

//        System.out.println("result: " + result + " angularVelocity: " + new ENG_Vector3D(angularVelocity.x, angularVelocity.y, angularVelocity.z));
//        Matrix3 invInertiaTensorWorld = entityProperties.getRigidBody().getInvInertiaTensorWorld();
//        Vector3 torque = new Vector3(angularVelocity);
//        torque.mul(invInertiaTensorWorld);
//        entityProperties.getRigidBody().getWorldTransform().trn(finalPitchDeg, finalYawDeg, finalRollDeg);

//        ENG_Vector3D angularVelocity = new ENG_Vector3D(-finalPitchDeg, -finalYawDeg, -finalRollDeg);
//        ENG_Vector3D angularVelocity = new ENG_Vector3D(3, 0, 0);
//        angularVelocity.mulInPlace(20);
//        if (!angularVelocitySet) {
//        ENG_Vector3D velocity = new ENG_Vector3D(mul.x, mul.y, mul.z);
//        velocity.mulInPlace(10);
//        System.out.println(velocity);
//        System.out.println("angular velocity: " + angularVelocity.len());

//        angularVelocity.set(0.0f, 6.1f, 0.0f);
//        System.out.println("angularVelocity: " + angularVelocity.len() + " x: " + angularVelocity.x + " y: " + angularVelocity.y + " z: " + angularVelocity.z);

//        System.out.println("angular velocity: " + angularVelocity.len());
//        if (angularVelocity.len() > maxAngularVelocity) {
//            System.out.println("angularVelocity: " + angularVelocity.len() + " x: " + angularVelocity.x + " y: " + angularVelocity.y + " z: " + angularVelocity.z);
//        }
//        entityProperties.getRigidBody().applyTorque(angularVelocity);
//        if (result.len() > 0) {
////            ++frameNum;
////            System.out.println("angularVelocity default: " + result.len() + " x: " + angularVelocity.x + " y: " + angularVelocity.y + " z: " + angularVelocity.z);
////            System.out.println("angularVelocity default: " + result.len() + " x: " + result.x + " y: " + result.y + " z: " + result.z);
//        } else {
////            if (frameNum > 0) {
//////                System.out.println("frameNum: " + frameNum);
////            }
////            frameNum = 0;
//        }
//        Vector3 prevAngularVelocity = entityProperties.getRigidBody().getAngularVelocity();
//        if (prevAngularVelocity.len() > 0 ) {
//            System.out.println("previous angular velocity: " + prevAngularVelocity.len() + " x: " + prevAngularVelocity.x + " y: " + prevAngularVelocity.y + " z: " + prevAngularVelocity.z);
//        }
//        entityProperties.getRigidBody().setDamping(0.0f, 0.9f);
//        PhysicsUtility.setAngularVelocity(entityProperties.getRigidBody(), angularVelocity);
//        angularVelocity.mulAdd(new Vector3(1000, 1000, 1000), 0);
//        entityProperties.getRigidBody().setAngularVelocity(new Vector3(0, 0, 0));
        if (shouldClearForcesAppliedToPlayerShip) {
            System.out.println("playerShip btRigidBody.clearForces()");
            // TODO BUG: For some reason clearForces() doesn't work here.
            entityProperties.getRigidBody().clearForces();
//            entityProperties.setPositionWithoutPhysics(entityProperties.getPosition());
            shouldClearForcesAppliedToPlayerShip = false;
        }
        entityProperties.getRigidBody().applyTorqueImpulse(angularVelocity);
//        if (angularVelocity.len2() > 0.0f) {
//            System.out.println("applyTorqueImpulse currentTime: " + ENG_Utility.nanoTime());
//        }
//        System.out.println("angularVelocity: " + angularVelocity.len());
//            angularVelocitySet = true;
//        }

        if (event.advanceWeapon > 0) {
            for (int i = 0; i < event.advanceWeapon; ++i) {
                weaponProperties.nextWeapon();
                MainApp.getGame().vibrate(APP_Game.VibrationEvent.ADVANCE_WEAPON);
            }
        } else if (event.advanceWeapon < 0) {
            for (int i = 0; i < -event.advanceWeapon; ++i) {
                weaponProperties.previousWeapon();
                MainApp.getGame().vibrate(APP_Game.VibrationEvent.ADVANCE_WEAPON);
            }
        }

        if (event.advanceEnemySelection > 0) {
            for (int i = 0; i < event.advanceEnemySelection; ++i) {
                HudManager.getSingleton().nextEnemySelection();
                MainApp.getGame().vibrate(APP_Game.VibrationEvent.ADVANCE_SELECTION);
            }
        } else if (event.advanceEnemySelection < 0) {
            for (int i = 0; i < -event.advanceEnemySelection; ++i) {
                HudManager.getSingleton().previousEnemySelection();
                MainApp.getGame().vibrate(APP_Game.VibrationEvent.ADVANCE_SELECTION);
            }
        }

        if (event.attackSelectedEnemy) {
            HudManager.getSingleton().attackCurrentEnemySelection();
            MainApp.getGame().vibrate(APP_Game.VibrationEvent.ATTACK_SELECTED_ENEMY);
        }
        if (event.defendPlayerShip) {
            HudManager.getSingleton().defendPlayerShip();
            MainApp.getGame().vibrate(APP_Game.VibrationEvent.DEFEND_PLAYER_SHIP);
        }

        // Just to be sure
        if (speedScrollContainer != null) {
            speedScrollContainer.setPercentage(
                    ENG_Math.clamp(
                            speedScrollContainer.getPercentage() +
                                    event.speedModification *
                                            shipProperties.getShipData().acceleration,
                            0,
                            100));
            entityProperties.setVelocity(shipProperties.getVelocity(
                    speedScrollContainer.getPercentage()));
        }

        if (event.afterburner && ENG_Utility.hasTimePassed(
                FrameInterval.AFTERBURNER_TIME,
                afterburnerLastTime,
                shipProperties.getAfterburnerCooldownTime())) {
//            System.out.println("InGameInputConvertorListener setAfterburnerActive: true");
            shipProperties.setAfterburnerActive(true);
            // We need to first check that setting the afterburner as active actually worked.
            // We need to take into account the afterburner cooldown time before
            // we start the HUD vibration animation.
            if (shipProperties.isAfterburnerActive()) {
                HudManager.getSingleton().vibrate(HudManager.HudVibrationType.AFTERBURNER);
            }
            afterburnerLastTime = currentTimeMillis();
        }

//		shipProperties.setCountermeasureLaunched(event.countermeasures);
        if (event.countermeasures && ENG_Utility.hasTimePassed(
                FrameInterval.COUNTERMEASURE_TIME,
                countermeasureLastTime, ShipData.COUNTERMEASURE_TIME)) {
//            System.out.println("InGameInputConvertorListener createCountermeasures");
            worldManager.createCountermeasures(playerShip);
            if (shipProperties.isCountermeasureLaunched()) {
                HudManager.getSingleton().vibrate(HudManager.HudVibrationType.COUNTERMEASURES);
            }
            countermeasureLastTime = currentTimeMillis();

            InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
            SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
            if (latestSessionStatistics != null) {
                LevelStatistics latestLevelStatistics = latestSessionStatistics.getLatestLevelStatistics();
                if (latestLevelStatistics != null) {
                    LevelEventStatistics latestLevelEventStatistics = latestLevelStatistics.getLatestLevelEventStatistics();
                    if (latestLevelEventStatistics != null) {
                        ++latestLevelEventStatistics.countermeasuresLaunchedNum;
                    } else {
                        System.out.println("latestLevelEventStatistics == null");
                    }
                } else {
                    System.out.println("latestLevelStatistics == null");
                }
            } else {
                System.out.println("latestSessionStatistics == null");
            }
        }

        if (event.reloadShip) {
            worldManager.createReloaderEntity();
        }

        if (event.fire && ENG_Utility.hasTimePassed(FrameInterval.FIRE_WAITING_TIME, fireLastTime, fireWaitingTime)) {
            worldManager.createProjectile(playerShip);
            weaponProperties.decrementCurrentWeaponAmmo();
//			HudManager.getSingleton().setFireButtonCooldownTime();
            fireLastTime = currentTimeMillis();
            fireWaitingTime = WeaponType.getWeaponCooldownTime(
                    HudManager.getSingleton().getCurrentWeaponType());
            if (WeaponType.isMissileType(HudManager.getSingleton().getCurrentWeaponType())) {
                MainApp.getGame().vibrate(APP_Game.VibrationEvent.PLAYER_FIRE_WEAPON);
            }
        }

        if (event.escape) {
//			GameMenuManager.getSingleton().showInGameMenu();
            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_GAME_MENU);
        }

        if (event.console) {
            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.CONSOLE);
        }

    }

    public ENG_ScrollOverlayContainer getSpeedScrollContainer() {
        return speedScrollContainer;
    }

    public void setSpeedScrollContainer(
            ENG_ScrollOverlayContainer speedScrollContainer) {
        this.speedScrollContainer = speedScrollContainer;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = ENG_Math.clamp(sensitivity, MIN_SHIP_SENSITIVITY, MAX_SHIP_SENSITIVITY);
        this.invSensitivity = 1.0f / sensitivity;
        // We need to make sure we don't mix previous stored movements with previous
        // sensitivity with the new ones in the same queue.
        resetMouseMovements();
        shouldClearForcesAppliedToPlayerShip = true;
        System.out.println("Ship sensitivity set: " + this.sensitivity);
    }

}
