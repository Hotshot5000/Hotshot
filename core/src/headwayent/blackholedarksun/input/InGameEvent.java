/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/3/21, 6:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.input;

public class InGameEvent {

    public int dx, dy;
    public boolean afterburner, reloadShip, countermeasures, fire;
    public int advanceWeapon;
    public int advanceEnemySelection;
    public int speedModification;
    public int rotate;
    public boolean attackSelectedEnemy;
    public boolean defendPlayerShip;
    public boolean escape;
    public boolean console;

    public void reset() {
        dx = 0;
        dy = 0;
        afterburner = false;
        reloadShip = false;
        countermeasures = false;
        fire = false;
        advanceWeapon = 0;
        advanceEnemySelection = 0;
        speedModification = 0;
        rotate = 0;
        attackSelectedEnemy = false;
        defendPlayerShip = false;
        escape = false;
        console = false;
    }

}
