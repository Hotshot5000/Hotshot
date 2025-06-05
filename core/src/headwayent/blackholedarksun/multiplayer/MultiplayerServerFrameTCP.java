/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/6/16, 8:34 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;

import java.util.ArrayList;

/**
 * Created by sebas on 11.11.2015.
 */
public class MultiplayerServerFrameTCP extends MultiplayerServerFrame {

    private final ArrayList<MultiplayerEntityTCP> addedEntities = new ArrayList<>();
    private final ArrayList<MultiplayerEntityTCP> addedProjectiles = new ArrayList<>();
//    private final ArrayList<Long> entitiesToRemove = new ArrayList<>();
//    private ArrayList<MultiplayerEntityTCP> clientSpecificAddedProjectiles = new ArrayList<>();
    private final ArrayList<MultiplayerEntityTCP> updateEntities = new ArrayList<>();
    // 0 bit: level ended.
    // 1 bit: level won/lost 1/0.
    private byte flag;

    public MultiplayerServerFrameTCP() {
        super(Type.MULTIPLAYER_SERVER_FRAME_TCP);
    }

    public void addMultiplayerEntityAddedCurrentFrame(MultiplayerEntityTCP e) {
        addedEntities.add(e);
    }

//    public void addClientSpecificProjectileAddedCurrentFrame(MultiplayerEntityTCP e) {
//        clientSpecificAddedProjectiles.add(e);
//    }
//
//    public void addClientSpecificProjectileAddedCurrentFrame(MultiplayerEntityHolder e) {
//        addClientSpecificProjectileAddedCurrentFrame(e.getMultiplayerEntityTCP());
//    }

    public void addProjectileAddedCurrentFrame(MultiplayerEntityTCP e) {
        addedProjectiles.add(e);
    }

//    public void addEntityToRemove(Long entityId) {
//        entitiesToRemove.add(entityId);
//    }

//    public void addEntityToRemove(MultiplayerEntityHolder e) {
//        addEntityToRemove(e.getMultiplayerEntityTCP());
//    }
//
//    public void addEntityToRemove(MultiplayerEntityTCP e) {
//        entitiesToRemove.add(e);
//    }

    public void addUpdateEntity(MultiplayerEntityTCP e) {
        updateEntities.add(e);
    }

    public ArrayList<MultiplayerEntityTCP> getAddedEntities() {
        return addedEntities;
    }

    public ArrayList<MultiplayerEntityTCP> getAddedProjectiles() {
        return addedProjectiles;
    }

//    public ArrayList<Long> getEntitiesToRemove() {
//        return entitiesToRemove;
//    }

//    public ArrayList<MultiplayerEntityTCP> getClientSpecificAddedProjectiles() {
//        return clientSpecificAddedProjectiles;
//    }

    public ArrayList<MultiplayerEntityTCP> getUpdateEntities() {
        return updateEntities;
    }

    public void setLevelEnded() {
        flag |= 0x1;
    }

    public boolean isLevelEnded() {
        return getBit(0);
    }

    public void setLevelWon(boolean won) {
        flag |= (byte) (won ? 0x1 : 0x0);
    }

    public boolean isLevelWon() {
        return getBit(1);
    }

    private boolean getBit(int position) {
        return ((flag >> position) & 0x1) == 1;
    }

    @Override
    public void prepareNextFrame() {
        super.prepareNextFrame();
        addedEntities.clear();
        addedProjectiles.clear();
//        entitiesToRemove.clear();
//        clientSpecificAddedProjectiles.clear();
        updateEntities.clear();
    }
}
