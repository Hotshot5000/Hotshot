/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.levelresource.levelmesh.LevelMesh;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Light;

import java.util.ArrayList;
import java.util.EnumMap;

public class LevelStart {

    public String skyboxName;
    public final ENG_Vector4D lightDir = new ENG_Vector4D();
    /**
     * @noinspection deprecation
     */
    public ENG_Light.LightTypes lightType = ENG_Light.LightTypes.LT_DIRECTIONAL;
    public float lightPowerScale = 1.0f;
    public final ENG_Vector4D lightPos = new ENG_Vector4D(true);
    public final ENG_ColorValue lightDiffuseColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    public final ENG_ColorValue lightSpecularColor = new ENG_ColorValue(ENG_ColorValue.WHITE);
    public final ENG_ColorValue ambientLightUpperHemisphere = new ENG_ColorValue(ENG_ColorValue.WHITE);
    public final ENG_ColorValue ambientLightLowerHemisphere = new ENG_ColorValue(ENG_ColorValue.WHITE);
    public final ENG_Vector4D ambientLighthemisphereDir = new ENG_Vector4D();
    public boolean reloaderAllowed = true;
    public final ArrayList<LevelObject> startObjects = new ArrayList<>();
    public transient final ArrayList<LevelObject> waypointObjects = new ArrayList<>();
    public final ArrayList<LevelSpawnPoint> spawnPoints = new ArrayList<>();
    public transient final EnumMap<ShipData.ShipTeam, LevelPlayerShipSelection> playerShipSelectionMap = new EnumMap<>(ShipData.ShipTeam.class);
    public final ArrayList<LevelObject> playerShipSelectionObjects = new ArrayList<>();
    public final ArrayList<String> alienPlayerShipSelectionList = new ArrayList<>();
    public final ArrayList<String> humanPlayerShipSelectionList = new ArrayList<>();
    public final ENG_Vector3D playerShipPosition = new ENG_Vector3D();
    public final ENG_Quaternion playerShipOrientation = new ENG_Quaternion();
    public String cutsceneName;
    //    public Cutscene cutscene;
    public ArrayList<LevelWaypointSector> waypointSectors = new ArrayList<>();

    public LevelMesh levelMesh;
    public boolean useSkyboxDataFromLevel;

    public LevelStart() {
    }

    public String getSkyboxName() {
        return skyboxName;
    }

    public ENG_Vector4D getLightDir() {
        return lightDir;
    }

    public ENG_Light.LightTypes getLightType() {
        return lightType;
    }

    public float getLightPowerScale() {
        return lightPowerScale;
    }

    public ENG_Vector4D getLightPos() {
        return lightPos;
    }

    public ENG_ColorValue getLightDiffuseColor() {
        return lightDiffuseColor;
    }

    public ENG_ColorValue getLightSpecularColor() {
        return lightSpecularColor;
    }

    public ENG_ColorValue getAmbientLightUpperHemisphere() {
        return ambientLightUpperHemisphere;
    }

    public ENG_ColorValue getAmbientLightLowerHemisphere() {
        return ambientLightLowerHemisphere;
    }

    public ENG_Vector4D getAmbientLighthemisphereDir() {
        return ambientLighthemisphereDir;
    }

    public boolean isReloaderAllowed() {
        return reloaderAllowed;
    }

    public ArrayList<LevelObject> getStartObjects() {
        return startObjects;
    }

    public ArrayList<LevelSpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    public EnumMap<ShipData.ShipTeam, LevelPlayerShipSelection> getPlayerShipSelectionMap() {
        return playerShipSelectionMap;
    }

    public ArrayList<LevelObject> getPlayerShipSelectionObjects() {
        return playerShipSelectionObjects;
    }

    public String getCutsceneName() {
        return cutsceneName;
    }

    public ArrayList<LevelWaypointSector> getWaypointSectors() {
        return waypointSectors;
    }

    public LevelMesh getLevelMesh() {
        return levelMesh;
    }

    public boolean isUseSkyboxDataFromLevel() {
        return useSkyboxDataFromLevel;
    }

    public ArrayList<String> getAlienPlayerShipSelectionList() {
        return alienPlayerShipSelectionList;
    }

    public ArrayList<String> getHumanPlayerShipSelectionList() {
        return humanPlayerShipSelectionList;
    }

    public ENG_Vector3D getPlayerShipPosition() {
        return playerShipPosition;
    }

    public ENG_Quaternion getPlayerShipOrientation() {
        return playerShipOrientation;
    }

    // For multiplayer as a client the server sends us the name of the level object and we must search for it in order to initialize it.

}
