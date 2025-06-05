/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 2:22 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import headwayent.blackholedarksun.net.UDPBreaker;

import java.util.ArrayList;

/**
 * Created by sebas on 17.11.2015.
 */
public class MultiplayerServerFrameUDPBreaker extends UDPBreaker {

    public MultiplayerServerFrameUDPBreaker(int mtuLimit) {
        super(mtuLimit);
    }

    @Override
    public boolean sendUDPFrameList(Connection connection, ArrayList<?> frameList) {
        for (Object frame : frameList) {
            int udpSentBytes = connection.sendUDP(frame, getMtuLimit());
//            System.out.println("udpSentBytes: " + udpSentBytes);
//            if (udpSentBytes >= getMtuLimit()) {
//                return false;
//            }
            if (udpSentBytes == -1) {
                // Bigger than the mtu.
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean createNewUDPFrames(ArrayList<?> udpFramesList, Object udpFrame, int iterationDivNum) {
        MultiplayerServerFrameUDP mainFrame = (MultiplayerServerFrameUDP) udpFrame;
        ArrayList<MultiplayerServerFrameUDP> framesList = (ArrayList<MultiplayerServerFrameUDP>) udpFramesList;
        int entityListSize = mainFrame.getEntityListSize();
        int divPoint = entityListSize / iterationDivNum;
        if (divPoint == 0) {
            System.out.println("entityListSize: " + entityListSize + " iterationDivNum: " + iterationDivNum);
            return false;
        }
        MultiplayerServerFrameUDP multiplayerServerFrameUDP = null;
        long frameNum = mainFrame.getFrameNum();
        for (int i = 0; i < mainFrame.getEntityListSize(); ++i) {
            try {
                if (i % divPoint == 0) {
                    multiplayerServerFrameUDP = new MultiplayerServerFrameUDP();
                    multiplayerServerFrameUDP.setFrameNum(frameNum++);
                    multiplayerServerFrameUDP.setTimestamp(mainFrame.getTimestamp());
                    framesList.add(multiplayerServerFrameUDP);
                }
                multiplayerServerFrameUDP.addEntity(mainFrame.getEntity(i));
            } catch (ArithmeticException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return true;
    }
}
