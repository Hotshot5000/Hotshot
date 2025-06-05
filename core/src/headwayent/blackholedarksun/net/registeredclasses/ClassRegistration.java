/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/30/21, 10:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.multiplayer.*;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;
import headwayent.blackholedarksun.multiplayer.rmi.UserStatsList;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.*;

/**
 * Created by Sebi on 07.06.2014.
 */
public class ClassRegistration {

    public static void register(Kryo kryo) {

        kryo.register(byte[].class);
        kryo.register(short[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);

        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(TreeSet.class);
        kryo.register(HashSet.class);
        kryo.register(TreeMap.class);
        kryo.register(HashMap.class);

        kryo.register(NetBase.Type.class);
        kryo.register(MainApp.GameType.class);
        kryo.register(ServerListRequest.class);
        kryo.register(ServerListResponse.class);
        kryo.register(ServerConnectionRequest.class);
        kryo.register(ServerConnectionResponse.class);
        kryo.register(JoinServerConnectionRequest.class);
        kryo.register(ServerLeaveRequest.class);
        kryo.register(ServerLeaveResponse.class);
        kryo.register(ClientLeaveRequest.class);
        kryo.register(ClientLeaveResponse.class);
        kryo.register(ServerRespawnRequest.class);

        kryo.register(MultiplayerClientFrameTCP.class);
        kryo.register(MultiplayerClientFrameUDP.class);
        kryo.register(MultiplayerServerFrameTCP.class);
        kryo.register(MultiplayerServerFrameUDP.class);
        kryo.register(PlayerState.class);

        kryo.register(ENG_Vector4D.class);
        kryo.register(ENG_Vector3D.class);
        kryo.register(ENG_Vector2D.class);
        kryo.register(ENG_Quaternion.class);

        kryo.register(EntityProperties.class);
        kryo.register(ShipProperties.class);
        kryo.register(ProjectileProperties.class);
        kryo.register(WeaponProperties.class);
        kryo.register(TrackerProperties.class);
        kryo.register(BeaconProperties.class);
        kryo.register(CargoProperties.class);
        kryo.register(WaypointProperties.class);
        kryo.register(StaticEntityProperties.class);

        kryo.register(MultiplayerEntityTCP.class);
        kryo.register(MultiplayerEntityUDP.class);

        kryo.register(WeaponData.WeaponType.class);

        kryo.register(UserStats.class);
        kryo.register(UserStatsList.class);

        kryo.register(Vector3.class);
        kryo.register(Matrix4.class);
        kryo.register(Matrix3.class);
    }
}
