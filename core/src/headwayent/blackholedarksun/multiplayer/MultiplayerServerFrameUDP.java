/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/25/21, 5:07 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;

import java.util.ArrayList;

/**
 * Created by sebas on 11.11.2015.
 */
public class MultiplayerServerFrameUDP extends MultiplayerServerFrame {

    private static final boolean DEBUG = false;

    private final ArrayList<MultiplayerEntityUDP> entities = new ArrayList<>();
//    private ArrayList<MultiplayerEntityUDP> clientSpecificEntities = new ArrayList<>();

    public MultiplayerServerFrameUDP() {
        super(Type.MULTIPLAYER_SERVER_FRAME_UDP);
    }

    public void addEntity(MultiplayerEntityUDP entity) {
        entities.add(entity);
    }

//    public void addClientSpecificEntity(MultiplayerEntityHolder entity) {
//        addClientSpecificEntity(entity.getMultiplayerEntityUDP());
//    }
//
//    public void addClientSpecificEntity(MultiplayerEntityUDP entity) {
//        clientSpecificEntities.add(entity);
//    }

    public int getEntityListSize() {
        return entities.size();
    }

    public MultiplayerEntityUDP getEntity(int i) {
        return entities.get(i);
    }

    public ArrayList<MultiplayerEntityUDP> getEntities() {
        return entities;
    }

//    public ArrayList<MultiplayerEntityUDP> getClientSpecificEntities() {
//        return clientSpecificEntities;
//    }

    @Override
    public void prepareNextFrame() {
        super.prepareNextFrame();
        entities.clear();
//        clientSpecificEntities.clear();
    }

    public boolean isEmptyFrame() {
        return entities.isEmpty() /*&& clientSpecificEntities.isEmpty()*/;
    }

    @Override
    public String toString() {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(super.toString());
            stringBuilder.append("\n");
            if (!entities.isEmpty()) {

                stringBuilder.append("ENTITIES:");
                for (MultiplayerEntityUDP multiplayerEntityUDP : entities) {
                    stringBuilder.append("\n");
                    stringBuilder.append(multiplayerEntityUDP.toString());
                }
            } else {
                stringBuilder.append("NO ENTITIES");
            }
//        stringBuilder.append("\n");
//        if (!clientSpecificEntities.isEmpty()) {
//
//            stringBuilder.append("CLIENT SPECIFIC ENTITIES:");
//            for (MultiplayerEntityUDP clientSpecificEntity : clientSpecificEntities) {
//                stringBuilder.append("\n");
//                stringBuilder.append(clientSpecificEntity.toString());
//            }
//        } else {
//            stringBuilder.append("NO CLIENT SPECIFIC ENTITIES");
//        }

            return stringBuilder.toString();
        } else {
            return super.toString();
        }
    }
}
