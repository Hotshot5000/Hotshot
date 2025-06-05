/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.gamestatedebugger;

import java.util.HashMap;

/**
 * Created by sebas on 04.10.2015.
 */
public class Timers {
    private final HashMap<String, Boolean> isAfterburnerActive = new HashMap<>();
    private final HashMap<String, Boolean> setAfterburnerActive = new HashMap<>();
    private boolean aiSystemReloaderTimeBetweenReloadingUnits;
    private final HashMap<String, Boolean> collisionDetectionTimeDamage = new HashMap<>();
    private boolean timedButtonListener;
    private boolean hudManagerEnemySelectionTime;
    private final HashMap<String, Boolean> textFieldCurrentPasswordCharTime = new HashMap<>();
    private final HashMap<String, Boolean> updateLevelEventsStartEvent = new HashMap<>();
    private boolean resumeGameEscapeDelay;
    private final HashMap<String, Boolean> worldManagerCheckDelayPassed = new HashMap<>();
    private final HashMap<String, Boolean> textFieldKeyCodeListenerDelay = new HashMap<>();
    private final HashMap<String, Boolean> textFieldCursorBlinkTime = new HashMap<>();
    private boolean hudManagerSpawnInfoDelay;
    private final HashMap<String, Boolean> worldManagerTimeElapsed = new HashMap<>();
    private boolean worldManagerPlayerShipIncomingMissile;
    private final HashMap<String, Boolean> movementSystemTrackingTimeDelay = new HashMap<>();
    private boolean helpMenuNextDelay;
    private boolean helpMenuPreviousDelay;
    private final HashMap<String, Boolean> aiSystemEvadeCollisionTime = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemMaxProjectilesLaunchedDelay = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemEnemySelectionTime = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemWeaponColldownTime = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemCountermeasureLastTime = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemCollisionResponseAccelerationTime = new HashMap<>();
    private final HashMap<String, Boolean> aiSystemEvasionHitTime = new HashMap<>();
    private boolean checkboxWaitBetweenClicks;
    private boolean worldManagerSoundTime;
    private boolean hudManagerCargoScanTime;
    private boolean inGameInputConvertorListenerAfterburnerCooldownTime;
    private boolean inGameInputConvertorListenerCountermeasureTime;
    private boolean inGameInputConvertorListenerFireWaitingTime;
    private final HashMap<String, Boolean> aiSystemPatrollingRotationTime = new HashMap<>();

    public Boolean getIsAfterburnerActive(String name) {
        return isAfterburnerActive.get(name);
    }

    public void setIsAfterburnerActive(String name, boolean isAfterburnerActive) {
        this.isAfterburnerActive.put(name, isAfterburnerActive);
    }

    public Boolean getIsSetAfterburnerActive(String name) {
        return setAfterburnerActive.get(name);
    }

    public void setSetAfterburnerActive(String name, boolean setAfterburnerActive) {
        this.setAfterburnerActive.put(name, setAfterburnerActive);
    }

    public boolean isAiSystemReloaderTimeBetweenReloadingUnits() {
        return aiSystemReloaderTimeBetweenReloadingUnits;
    }

    public void setAiSystemReloaderTimeBetweenReloadingUnits(boolean aiSystemReloaderTimeBetweenReloadingUnits) {
        this.aiSystemReloaderTimeBetweenReloadingUnits = aiSystemReloaderTimeBetweenReloadingUnits;
    }

    public Boolean isCollisionDetectionTimeDamage(String name) {
        return collisionDetectionTimeDamage.get(name);
    }

    public void setCollisionDetectionTimeDamage(String name, boolean b) {
        this.collisionDetectionTimeDamage.put(name, b);
    }

    public boolean isTimedButtonListener() {
        return timedButtonListener;
    }

    public void setTimedButtonListener(boolean timedButtonListener) {
        this.timedButtonListener = timedButtonListener;
    }

    public boolean isHudManagerEnemySelectionTime() {
        return hudManagerEnemySelectionTime;
    }

    public void setHudManagerEnemySelectionTime(boolean hudManagerEnemySelectionTime) {
        this.hudManagerEnemySelectionTime = hudManagerEnemySelectionTime;
    }

    public Boolean isTextFieldCurrentPasswordCharTime(String name) {
        return textFieldCurrentPasswordCharTime.get(name);
    }

    public void setTextFieldCurrentPasswordCharTime(String name, boolean b) {
        this.textFieldCurrentPasswordCharTime.put(name, b);
    }

    public Boolean isUpdateLevelEventsStartEvent(String name) {
        return updateLevelEventsStartEvent.get(name);
    }

    public void setUpdateLevelEventsStartEvent(String name, boolean b) {
        this.updateLevelEventsStartEvent.put(name, b);
    }

    public boolean isResumeGameEscapeDelay() {
        return resumeGameEscapeDelay;
    }

    public void setResumeGameEscapeDelay(boolean resumeGameEscapeDelay) {
        this.resumeGameEscapeDelay = resumeGameEscapeDelay;
    }

    public Boolean isWorldManagerCheckDelayPassed(String name) {
        return worldManagerCheckDelayPassed.get(name);
    }

    public void setWorldManagerCheckDelayPassed(String name, boolean b) {
        this.worldManagerCheckDelayPassed.put(name, b);
    }

    public Boolean isTextFieldKeyCodeListenerDelay(String name) {
        return textFieldKeyCodeListenerDelay.get(name);
    }

    public void setTextFieldKeyCodeListenerDelay(String name, boolean b) {
        this.textFieldKeyCodeListenerDelay.put(name, b);
    }

    public Boolean isTextFieldCursorBlinkTime(String name) {
        return textFieldCursorBlinkTime.get(name);
    }

    public void setTextFieldCursorBlinkTime(String name, boolean b) {
        this.textFieldCursorBlinkTime.put(name, b);
    }

    public boolean isHudManagerSpawnInfoDelay() {
        return hudManagerSpawnInfoDelay;
    }

    public void setHudManagerSpawnInfoDelay(boolean hudManagerSpawnInfoDelay) {
        this.hudManagerSpawnInfoDelay = hudManagerSpawnInfoDelay;
    }

    public Boolean isWorldManagerTimeElapsed(String name) {
        return worldManagerTimeElapsed.get(name);
    }

    public void setWorldManagerTimeElapsed(String name, boolean b) {
        this.worldManagerTimeElapsed.put(name, b);
    }

    public Boolean isMovementSystemTrackingTimeDelay(String name) {
        return movementSystemTrackingTimeDelay.get(name);
    }

    public void setMovementSystemTrackingTimeDelay(String name, boolean b) {
        this.movementSystemTrackingTimeDelay.put(name, b);
    }

    public boolean isHelpMenuNextDelay() {
        return helpMenuNextDelay;
    }

    public void setHelpMenuNextDelay(boolean helpMenuNextDelay) {
        this.helpMenuNextDelay = helpMenuNextDelay;
    }

    public boolean isHelpMenuPreviousDelay() {
        return helpMenuPreviousDelay;
    }

    public void setHelpMenuPreviousDelay(boolean helpMenuPreviousDelay) {
        this.helpMenuPreviousDelay = helpMenuPreviousDelay;
    }

    public Boolean isAiSystemEvadeCollisionTime(String name) {
        return aiSystemEvadeCollisionTime.get(name);
    }

    public void setAiSystemEvadeCollisionTime(String name, boolean b) {
        this.aiSystemEvadeCollisionTime.put(name, b);
    }

    public Boolean isAiSystemMaxProjectilesLaunchedDelay(String name) {
        return aiSystemMaxProjectilesLaunchedDelay.get(name);
    }

    public void setAiSystemMaxProjectilesLaunchedDelay(String name, boolean b) {
        this.aiSystemMaxProjectilesLaunchedDelay.put(name, b);
    }

    public Boolean isAiSystemEnemySelectionTime(String name) {
        return aiSystemEnemySelectionTime.get(name);
    }

    public void setAiSystemEnemySelectionTime(String name, boolean b) {
        this.aiSystemEnemySelectionTime.put(name, b);
    }

    public Boolean isAiSystemWeaponColldownTime(String name) {
        return aiSystemWeaponColldownTime.get(name);
    }

    public void setAiSystemWeaponColldownTime(String name, boolean b) {
        this.aiSystemWeaponColldownTime.put(name, b);
    }

    public Boolean isAiSystemCountermeasureLastTime(String name) {
        return aiSystemCountermeasureLastTime.get(name);
    }

    public void setAiSystemCountermeasureLastTime(String name, boolean b) {
        this.aiSystemCountermeasureLastTime.put(name, b);
    }

    public Boolean isAiSystemCollisionResponseAccelerationTime(String name) {
        return aiSystemCollisionResponseAccelerationTime.get(name);
    }

    public void setAiSystemCollisionResponseAccelerationTime(String name, boolean b) {
        this.aiSystemCollisionResponseAccelerationTime.put(name, b);
    }

    public Boolean isAiSystemEvasionHitTime(String name) {
        return aiSystemEvasionHitTime.get(name);
    }

    public void setAiSystemEvasionHitTime(String name, boolean b) {
        this.aiSystemEvasionHitTime.put(name, b);
    }

    public boolean isCheckboxWaitBetweenClicks() {
        return checkboxWaitBetweenClicks;
    }

    public void setCheckboxWaitBetweenClicks(boolean checkboxWaitBetweenClicks) {
        this.checkboxWaitBetweenClicks = checkboxWaitBetweenClicks;
    }

    public boolean isWorldManagerSoundTime() {
        return worldManagerSoundTime;
    }

    public void setWorldManagerSoundTime(boolean worldManagerSoundTime) {
        this.worldManagerSoundTime = worldManagerSoundTime;
    }

    public boolean isHudManagerCargoScanTime() {
        return hudManagerCargoScanTime;
    }

    public void setHudManagerCargoScanTime(boolean hudManagerCargoScanTime) {
        this.hudManagerCargoScanTime = hudManagerCargoScanTime;
    }

    public boolean isInGameInputConvertorListenerAfterburnerCooldownTime() {
        return inGameInputConvertorListenerAfterburnerCooldownTime;
    }

    public void setInGameInputConvertorListenerAfterburnerCooldownTime(boolean inGameInputConvertorListenerAfterburnerCooldownTime) {
        this.inGameInputConvertorListenerAfterburnerCooldownTime = inGameInputConvertorListenerAfterburnerCooldownTime;
    }

    public boolean isInGameInputConvertorListenerCountermeasureTime() {
        return inGameInputConvertorListenerCountermeasureTime;
    }

    public void setInGameInputConvertorListenerCountermeasureTime(boolean inGameInputConvertorListenerCountermeasureTime) {
        this.inGameInputConvertorListenerCountermeasureTime = inGameInputConvertorListenerCountermeasureTime;
    }

    public boolean isInGameInputConvertorListenerFireWaitingTime() {
        return inGameInputConvertorListenerFireWaitingTime;
    }

    public void setInGameInputConvertorListenerFireWaitingTime(boolean inGameInputConvertorListenerFireWaitingTime) {
        this.inGameInputConvertorListenerFireWaitingTime = inGameInputConvertorListenerFireWaitingTime;
    }

    public Boolean isAiSystemPatrollingRotationTime(String name) {
        return aiSystemPatrollingRotationTime.get(name);
    }

    public void setAiSystemPatrollingRotationTime(String name, boolean b) {
        this.aiSystemPatrollingRotationTime.put(name, b);
    }

    public boolean isWorldManagerPlayerShipIncomingMissile() {
        return worldManagerPlayerShipIncomingMissile;
    }

    public void setWorldManagerPlayerShipIncomingMissile(boolean worldManagerPlayerShipIncomingMissile) {
        this.worldManagerPlayerShipIncomingMissile = worldManagerPlayerShipIncomingMissile;
    }
}
