/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/16/21, 6:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;

import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;

/**
 * Created by sebas on 17.11.2015.
 */
public abstract class UDPBreaker {

    private final int mtuLimit;

    public UDPBreaker(int mtuLimit) {
        this.mtuLimit = mtuLimit;
    }

    public int getMtuLimit() {
        return mtuLimit;
    }

    public abstract boolean sendUDPFrameList(Connection connection, ArrayList<?> frameList);

    public abstract boolean createNewUDPFrames(ArrayList<?> udpFramesList, Object mainFrame, int iterationDivNum);

    public boolean breakUDP(Connection connection, ArrayList<?> frameList) {
        int iterationDivNum = 2;
        Object mainFrame = frameList.get(0);
        boolean udpSent = true;
        while (!sendUDPFrameList(connection, frameList)) {
            ArrayList<MultiplayerServerFrameUDP> multiplayerServerFrameUDPArrayList = (ArrayList<MultiplayerServerFrameUDP>) frameList;
            int totalEntities = 0;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not send udp frames size: ").append(multiplayerServerFrameUDPArrayList.size());
            for (MultiplayerServerFrameUDP frame : multiplayerServerFrameUDPArrayList) {
                totalEntities += frame.getEntityListSize();
                stringBuilder.append("\nframe entity size: ").append(frame.getEntityListSize());
            }
            stringBuilder.append("\ntotal entity num: ").append(totalEntities);
            stringBuilder.append("\n iterationDivNum: ").append(iterationDivNum);
            System.out.println(stringBuilder);

            frameList.clear();
            if (!createNewUDPFrames(frameList, mainFrame, iterationDivNum)) {
                udpSent = false;
                break;
            }
            iterationDivNum *= 2;
        }
        return udpSent;
    }
}
