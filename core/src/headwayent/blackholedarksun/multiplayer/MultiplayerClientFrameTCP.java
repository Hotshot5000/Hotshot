/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;


import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;

import java.util.ArrayList;

/**
 * Created by sebas on 11.11.2015.
 */
public class MultiplayerClientFrameTCP extends MultiplayerClientFrame {

//    private int health;
//    private boolean destroyed;
    //    private ArrayList<MultiplayerEntityHolder> addedEntities = new ArrayList<>();
    private final ArrayList<MultiplayerEntityTCP> addedProjectiles = new ArrayList<>();
    private boolean afterburnerActive;
    private boolean countermeasuresLaunched;

    /**
     * For serialization.
     */
    public MultiplayerClientFrameTCP() {
        super(Type.MULTIPLAYER_CLIENT_FRAME_TCP);
    }

    public MultiplayerClientFrameTCP(long userId) {
        super(Type.MULTIPLAYER_CLIENT_FRAME_TCP);
        setUserId(userId);
    }

    public void addProjectile(MultiplayerEntityTCP e) {
        addedProjectiles.add(e);
    }

    public MultiplayerEntityTCP getProjectile(int i) {
        return addedProjectiles.get(i);
    }

    public ArrayList<MultiplayerEntityTCP> getAddedProjectiles() {
        return addedProjectiles;
    }

    public int getProjectileListSize() {
        return addedProjectiles.size();
    }

//    public boolean isDestroyed() {
//        return destroyed;
//    }
//
//    public void setDestroyed(boolean destroyed) {
//        this.destroyed = destroyed;
//    }

//    public int getHealth() {
//        return health;
//    }
//
//    public void setHealth(int health) {
//        this.health = health;
//    }

//    @Override
//    public void prepareNextFrame() {
//        super.prepareNextFrame();
//        addedProjectiles.clear();
//    }

    public void clear() {
        addedProjectiles.clear();
    }

    public boolean isAfterburnerActive() {
        return afterburnerActive;
    }

    public void setAfterburnerActive(boolean afterburnerActive) {
        this.afterburnerActive = afterburnerActive;
    }

    public boolean isCountermeasuresLaunched() {
        return countermeasuresLaunched;
    }

    public void setCountermeasuresLaunched(boolean countermeasuresLaunched) {
        this.countermeasuresLaunched = countermeasuresLaunched;
    }
}
