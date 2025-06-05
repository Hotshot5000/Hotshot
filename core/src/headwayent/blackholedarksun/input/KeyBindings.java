/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/3/21, 6:29 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.input;

import com.badlogic.gdx.Input.Keys;

public class KeyBindings {

    public static final int ACCELERATE_DEFAULT = Keys.W;
    public static final int DECELERATE_DEFAULT = Keys.S;
    public static final int NEXT_WEAPON_DEFAULT = Keys.D;
    public static final int PREVIOUS_WEAPON_DEFAULT = Keys.A;
    public static final int NEXT_ENEMY_SELECTION_DEFAULT = Keys.X;
    public static final int PREVIOUS_ENEMY_SELECTION_DEFAULT = Keys.Z;
    public static final int AFTERBURNER_DEFAULT = Keys.SHIFT_LEFT;
    public static final int COUNTERMEASURES_DEFAULT = Keys.CONTROL_LEFT;
    public static final int RELOAD_SHIP_DEFAULT = Keys.R;
    public static final int ROTATE_LEFT = Keys.Q;
    public static final int ROTATE_RIGHT = Keys.E;
    public static final int ATTACK_SELECTED_ENEMY = Keys.F;
    public static final int DEFEND_PLAYER_SHIP = Keys.G;
    public static final int ESCAPE = Keys.ESCAPE;
    public static final int CONSOLE = Keys.GRAVE;

    private int accelerate;
    private int decelerate;
    private int nextWeapon, previousWeapon;
    private int nextEnemySelection, previousEnemySelection;
    private int afterburner;
    private int countermeasures;
    private int reloadShip;
    private int rotateLeft, rotateRight;
    private int attackSelectedEnemy;
    private int defendPlayerShip;
    private int escape;
    private int escape2; // The new keycode is 131 for unknown reasons.
    private int console;

    public void setDefaults() {
        accelerate = ACCELERATE_DEFAULT;
        decelerate = DECELERATE_DEFAULT;
        nextWeapon = NEXT_WEAPON_DEFAULT;
        previousWeapon = PREVIOUS_WEAPON_DEFAULT;
        nextEnemySelection = NEXT_ENEMY_SELECTION_DEFAULT;
        previousEnemySelection = PREVIOUS_ENEMY_SELECTION_DEFAULT;
        afterburner = AFTERBURNER_DEFAULT;
        countermeasures = COUNTERMEASURES_DEFAULT;
        reloadShip = RELOAD_SHIP_DEFAULT;
        rotateLeft = ROTATE_LEFT;
        rotateRight = ROTATE_RIGHT;
        attackSelectedEnemy = ATTACK_SELECTED_ENEMY;
        defendPlayerShip = DEFEND_PLAYER_SHIP;
        escape = ESCAPE;
        escape2 = 131;
        console = CONSOLE;
    }

    public int getAccelerate() {
        return accelerate;
    }

    public void setAccelerate(int accelerate) {
        this.accelerate = accelerate;
    }

    public int getDecelerate() {
        return decelerate;
    }

    public void setDecelerate(int decelerate) {
        this.decelerate = decelerate;
    }

    public int getNextWeapon() {
        return nextWeapon;
    }

    public void setNextWeapon(int nextWeapon) {
        this.nextWeapon = nextWeapon;
    }

    public int getPreviousWeapon() {
        return previousWeapon;
    }

    public void setPreviousWeapon(int previousWeapon) {
        this.previousWeapon = previousWeapon;
    }

    public int getNextEnemySelection() {
        return nextEnemySelection;
    }

    public void setNextEnemySelection(int nextEnemySelection) {
        this.nextEnemySelection = nextEnemySelection;
    }

    public int getPreviousEnemySelection() {
        return previousEnemySelection;
    }

    public void setPreviousEnemySelection(int previousEnemySelection) {
        this.previousEnemySelection = previousEnemySelection;
    }

    public int getAfterburner() {
        return afterburner;
    }

    public void setAfterburner(int afterburner) {
        this.afterburner = afterburner;
    }

    public int getCountermeasures() {
        return countermeasures;
    }

    public void setCountermeasures(int countermeasures) {
        this.countermeasures = countermeasures;
    }

    public int getReloadShip() {
        return reloadShip;
    }

    public void setReloadShip(int reloadShip) {
        this.reloadShip = reloadShip;
    }

    public int getRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(int rotateLeft) {
        this.rotateLeft = rotateLeft;
    }

    public int getRotateRight() {
        return rotateRight;
    }

    public void setRotateRight(int rotateRight) {
        this.rotateRight = rotateRight;
    }

    public int getAttackSelectedEnemy() {
        return attackSelectedEnemy;
    }

    public void setAttackSelectedEnemy(int attackSelectedEnemy) {
        this.attackSelectedEnemy = attackSelectedEnemy;
    }

    public int getDefendPlayerShip() {
        return defendPlayerShip;
    }

    public void setDefendPlayerShip(int defendPlayerShip) {
        this.defendPlayerShip = defendPlayerShip;
    }

    public int getEscape() {
        return escape;
    }

    public void setEscape(int escape) {
        this.escape = escape;
    }

    public int getEscape2() {
        return escape2;
    }

    public void setEscape2(int escape2) {
        this.escape2 = escape2;
    }

    public int getConsole() {
        return console;
    }

    public void setConsole(int console) {
        this.console = console;
    }
}
