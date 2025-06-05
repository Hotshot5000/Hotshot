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
import headwayent.blackholedarksun.parser.ast.Cutscene;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Light;

import java.util.ArrayList;
import java.util.EnumMap;

public class LevelStart {

    public String skyboxName;
    public final ENG_Vector4D lightDir = new ENG_Vector4D();
    /** @noinspection deprecation */
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
    public final ArrayList<LevelSpawnPoint> spawnPoints = new ArrayList<>();
    public final EnumMap<ShipData.ShipTeam, LevelPlayerShipSelection> playerShipSelectionMap = new EnumMap<>(ShipData.ShipTeam.class);
    public final ArrayList<LevelObject> playerShipSelectionObjects = new ArrayList<>();
    public String cutsceneName;
    public Cutscene cutscene;
    public ArrayList<LevelWaypointSector> waypointSectors = new ArrayList<>();

    public LevelMesh levelMesh;
    public boolean useSkyboxDataFromLevel;

    // For multiplayer as a client the server sends us the name of the level object and we must search for it in order to initialize it.

}
